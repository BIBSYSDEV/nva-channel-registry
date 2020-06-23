package no.unit.nva.channel;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.channel.exception.NoResultsFoundException;
import no.unit.nva.channel.model.outgoing.Channel;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChannelRegistryClientTest {

    public static final String ANY_URI = "http://example.org";
    public static final String SEARCH_TERM = "searchTerm";
    public static final String VALID_RESPONSE_JSON = "valid_response.json";
    public static final String STATUS_MESSAGE_RESPONSE_JSON = "status_message_response.json";
    public static final int TABLE_ID = 851;
    private static final String LINE_SEPARATOR = System.lineSeparator();
    public static final int EXPECT_EIGHT_FILTERED_RESULTS = 8;
    public static final String EXPECTED_NO_RESULTS_FROM_SERVICE_MESSAGE = ChannelRegistryClient.NO_RESULTS_FROM_SERVICE;
    private final ObjectMapper objectMapper = MainHandler.createObjectMapper();

    private CloseableHttpResponse response;
    private ChannelRegistryClient channelRegistryClient;

    /**
     * Set up mocks for testing.
     *
     * @throws IOException if connection fails, or problem was encountered.
     */
    @Before
    public void setUp() throws IOException {
        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        response = mock(CloseableHttpResponse.class);
        channelRegistryClient = new ChannelRegistryClient(objectMapper, httpClient, ANY_URI);
        when(httpClient.execute(any())).thenReturn(response);
    }

    @Test
    public void testEmptyResponse() {
        // TODO: This may to cover up an issue with this project where we should return BAD GATEWAY.
        setUpEmptyResponse();
        Throwable exception = assertThrows(NoResultsFoundException.class, this::fetchChannels);
        String actual = exception.getMessage();
        assertEquals(EXPECTED_NO_RESULTS_FROM_SERVICE_MESSAGE, actual);
    }

    @Test
    public void testStatusMessageResponse() throws IOException {
        setUpNoResults();
        Throwable exception = assertThrows(NoResultsFoundException.class, this::fetchChannels);
        String actual = exception.getMessage();
        assertEquals(EXPECTED_NO_RESULTS_FROM_SERVICE_MESSAGE, actual);
    }

    @Test
    public void testValidResponse() throws IOException, NoResultsFoundException {
        setUpResponseWithResults();
        List<Channel> channels = fetchChannels();
        int actual = channels.size();
        Assert.assertEquals(EXPECT_EIGHT_FILTERED_RESULTS, actual);
    }

    private void setUpEmptyResponse() {
        // No-OP
    }

    private void setUpNoResults() throws UnsupportedEncodingException, FileNotFoundException {
        when(response.getEntity()).thenReturn(getStringEntity(STATUS_MESSAGE_RESPONSE_JSON));
    }

    private void setUpResponseWithResults() throws UnsupportedEncodingException, FileNotFoundException {
        when(response.getEntity()).thenReturn(getStringEntity(VALID_RESPONSE_JSON));
    }

    private List<Channel> fetchChannels() throws IOException, NoResultsFoundException {
        return channelRegistryClient.fetchChannels(TABLE_ID, SEARCH_TERM);
    }

    private StringEntity getStringEntity(String overlappingResourcesResponseJson) throws UnsupportedEncodingException,
            FileNotFoundException {
        return new StringEntity(stringFromResources(overlappingResourcesResponseJson));
    }

    private static String stringFromResources(String path) throws FileNotFoundException {

        return streamToString(inputStreamFromResources(Path.of(path)));
    }

    private static InputStream inputStreamFromResources(Path path) throws FileNotFoundException {
        try {
            String pathString = path.toString();
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(pathString);
            Objects.requireNonNull((stream));
            return stream;
        } catch (Exception e) {
            throw new FileNotFoundException(path.toString());
        }
    }

    private static String streamToString(InputStream stream) {
        try (BufferedReader reader = new BufferedReader(newInputStreamReader(stream))) {
            return reader.lines().collect(Collectors.joining(LINE_SEPARATOR));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Reader newInputStreamReader(InputStream stream) {
        return new InputStreamReader(stream, StandardCharsets.UTF_8);
    }
}
