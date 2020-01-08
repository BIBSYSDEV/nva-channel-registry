package no.unit.nva.channel;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.channel.exception.NoResultsFoundException;
import no.unit.nva.channel.model.incoming.SearchRequest;
import no.unit.nva.channel.model.outgoing.SearchResponse;
import org.apache.http.HttpHeaders;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AppTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void test() throws IOException {
        App app = new App();
        Context context = mock(Context.class);
        OutputStream output = new ByteArrayOutputStream();

        app.handleRequest(inputStream(), output, context);

        GatewayResponse gatewayResponse = objectMapper.readValue(output.toString(), GatewayResponse.class);
        assertEquals(SC_OK, gatewayResponse.getStatusCode());
        Assert.assertTrue(gatewayResponse.getHeaders().keySet().contains(HttpHeaders.CONTENT_TYPE));
        Assert.assertTrue(gatewayResponse.getHeaders().keySet().contains("Access-Control-Allow-Origin"));
        SearchResponse searchResponse = objectMapper.readValue(gatewayResponse.getBody().toString(), SearchResponse.class);
        assertEquals(10, searchResponse.getResults().size());
    }

    @Test
    public void testNoResultsFoundException() throws IOException, NoResultsFoundException {
        ChannelRegistryClient channelRegistryClient = mock(ChannelRegistryClient.class);
        when(channelRegistryClient.fetchChannels(anyString())).thenThrow(NoResultsFoundException.class);
        App app = new App(objectMapper, channelRegistryClient);
        Context context = mock(Context.class);
        OutputStream output = new ByteArrayOutputStream();

        app.handleRequest(inputStream(), output, context);

        GatewayResponse gatewayResponse = objectMapper.readValue(output.toString(), GatewayResponse.class);
        assertEquals(SC_NOT_FOUND, gatewayResponse.getStatusCode());
    }

    @Test
    public void testIOException() throws IOException, NoResultsFoundException {
        ChannelRegistryClient channelRegistryClient = mock(ChannelRegistryClient.class);
        when(channelRegistryClient.fetchChannels(anyString())).thenThrow(IOException.class);
        App app = new App(objectMapper, channelRegistryClient);

        Context context = mock(Context.class);
        OutputStream output = new ByteArrayOutputStream();

        app.handleRequest(inputStream(), output, context);

        GatewayResponse gatewayResponse = objectMapper.readValue(output.toString(), GatewayResponse.class);
        assertEquals(SC_INTERNAL_SERVER_ERROR, gatewayResponse.getStatusCode());

    }

    @Test
    public void testBadRequest() throws IOException {
        App app = new App();
        Context context = mock(Context.class);
        OutputStream output = new ByteArrayOutputStream();

        app.handleRequest(new ByteArrayInputStream(new byte[0]), output, context);

        GatewayResponse gatewayResponse = objectMapper.readValue(output.toString(), GatewayResponse.class);
        assertEquals(SC_BAD_REQUEST, gatewayResponse.getStatusCode());
    }

    private InputStream inputStream() throws JsonProcessingException {
        Map<String, Object> event = new HashMap<>();
        event.put("body", objectMapper.writeValueAsString(new SearchRequest("%test%")));
        return new ByteArrayInputStream(objectMapper.writeValueAsBytes(event));
    }
}
