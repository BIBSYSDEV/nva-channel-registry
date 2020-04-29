package no.unit.nva.channel;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.channel.exception.NoResultsFoundException;
import no.unit.nva.channel.model.outgoing.Channel;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChannelRegistryClientTest {

    private ObjectMapper objectMapper = MainHandler.createObjectMapper();

    @Test
    public void testEmptyResponse() throws IOException, NoResultsFoundException {
        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);

        when(httpClient.execute(any())).thenReturn(response);

        ChannelRegistryClient channelRegistryClient = new ChannelRegistryClient(objectMapper, httpClient,
                "http://example.org");
        Throwable exception = assertThrows(NoResultsFoundException.class, () -> {
            channelRegistryClient.fetchChannels(851, "searchTerm");
        });

        assertEquals(ChannelRegistryClient.NO_RESULTS_FROM_SERVICE, exception.getMessage());
    }

    @Test
    public void testStatusMessageResponse() throws IOException, NoResultsFoundException, URISyntaxException {
        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);

        when(httpClient.execute(any())).thenReturn(response);
        when(response.getEntity()).thenReturn(new StringEntity(new String(Files.readAllBytes(Paths.get(getClass()
                .getClassLoader().getResource("status_message_response.json").toURI())))));

        ChannelRegistryClient channelRegistryClient = new ChannelRegistryClient(objectMapper, httpClient,
                "http://example.org");

        Throwable exception = assertThrows(NoResultsFoundException.class, () -> {
            channelRegistryClient.fetchChannels(851, "searchTerm");
        });

        assertEquals(ChannelRegistryClient.NO_RESULTS_FROM_SERVICE, exception.getMessage());
    }

    @Test
    public void testValidResponse() throws IOException, NoResultsFoundException, URISyntaxException {
        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);

        when(httpClient.execute(any())).thenReturn(response);
        when(response.getEntity()).thenReturn(new StringEntity(new String(Files.readAllBytes(Paths.get(getClass()
                .getClassLoader().getResource("valid_response.json").toURI())))));

        ChannelRegistryClient channelRegistryClient = new ChannelRegistryClient(objectMapper, httpClient,
                "http://example.org");
        List<Channel> channels = channelRegistryClient.fetchChannels(851, "searchTerm");

        Assert.assertEquals(10, channels.size());
    }

}
