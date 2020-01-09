package no.unit.nva.channel;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.channel.exception.NoResultsFoundException;
import no.unit.nva.channel.model.incoming.SearchRequest;
import no.unit.nva.channel.model.outgoing.SearchResponse;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static no.unit.nva.channel.FindChannelFunctionApp.ACCESS_CONTROL_ALLOW_ORIGIN;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FindChannelFunctionAppTest {

    private final ObjectMapper objectMapper = FindChannelFunctionApp.createObjectMapper();

    @Test
    public void test() throws IOException {
        FindChannelFunctionApp findChannelFunctionApp = new FindChannelFunctionApp();
        Context context = mock(Context.class);
        OutputStream output = new ByteArrayOutputStream();

        findChannelFunctionApp.handleRequest(inputStream(), output, context);

        GatewayResponse gatewayResponse = objectMapper.readValue(output.toString(), GatewayResponse.class);
        assertEquals(SC_OK, gatewayResponse.getStatusCode());
        Assert.assertTrue(gatewayResponse.getHeaders().keySet().contains(CONTENT_TYPE));
        Assert.assertTrue(gatewayResponse.getHeaders().keySet().contains(ACCESS_CONTROL_ALLOW_ORIGIN));
        SearchResponse searchResponse = objectMapper.readValue(gatewayResponse.getBody().toString(),
                SearchResponse.class);
        assertEquals(10, searchResponse.getResults().size());
    }

    @Test
    public void testNoResultsFoundException() throws IOException, NoResultsFoundException {
        ChannelRegistryClient channelRegistryClient = mock(ChannelRegistryClient.class);
        when(channelRegistryClient.fetchChannels(anyInt(), anyString())).thenThrow(NoResultsFoundException.class);
        FindChannelFunctionApp findChannelFunctionApp = new FindChannelFunctionApp(objectMapper, channelRegistryClient);
        Context context = mock(Context.class);
        OutputStream output = new ByteArrayOutputStream();

        findChannelFunctionApp.handleRequest(inputStream(), output, context);

        GatewayResponse gatewayResponse = objectMapper.readValue(output.toString(), GatewayResponse.class);
        assertEquals(SC_NOT_FOUND, gatewayResponse.getStatusCode());
    }

    @Test
    public void testIOException() throws IOException, NoResultsFoundException {
        ChannelRegistryClient channelRegistryClient = mock(ChannelRegistryClient.class);
        when(channelRegistryClient.fetchChannels(anyInt(), anyString())).thenThrow(IOException.class);
        FindChannelFunctionApp findChannelFunctionApp = new FindChannelFunctionApp(objectMapper, channelRegistryClient);

        Context context = mock(Context.class);
        OutputStream output = new ByteArrayOutputStream();

        findChannelFunctionApp.handleRequest(inputStream(), output, context);

        GatewayResponse gatewayResponse = objectMapper.readValue(output.toString(), GatewayResponse.class);
        assertEquals(SC_INTERNAL_SERVER_ERROR, gatewayResponse.getStatusCode());

    }

    @Test
    public void testBadRequest() throws IOException {
        FindChannelFunctionApp findChannelFunctionApp = new FindChannelFunctionApp();
        Context context = mock(Context.class);
        OutputStream output = new ByteArrayOutputStream();

        findChannelFunctionApp.handleRequest(new ByteArrayInputStream(new byte[0]), output, context);

        GatewayResponse gatewayResponse = objectMapper.readValue(output.toString(), GatewayResponse.class);
        assertEquals(SC_BAD_REQUEST, gatewayResponse.getStatusCode());
    }

    @Test
    public void testBadUri() {
        Context context = mock(Context.class);
        when(context.getAwsRequestId()).thenReturn("::/&(%¤#");
        URI instance = FindChannelFunctionApp.createProblemInstance(context);
        assertNull(instance);
    }

    private InputStream inputStream() throws JsonProcessingException {
        Map<String, Object> event = new HashMap<>();
        event.put("body", objectMapper.writeValueAsString(new SearchRequest(851, "%test%")));
        return new ByteArrayInputStream(objectMapper.writeValueAsBytes(event));
    }
}
