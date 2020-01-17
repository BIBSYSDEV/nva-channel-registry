package no.unit.nva.channel;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.channel.exception.NoResultsFoundException;
import no.unit.nva.channel.model.internal.FetchJsonTableDataRequest;
import no.unit.nva.channel.model.outgoing.Channel;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

public class ChannelRegistryClient {

    public static final String NO_RESULTS_FROM_SERVICE = "No results from service.";
    public static final String RETURNED_POSTS_JSON_POINTER = "/1/returnerte poster";
    public static final String ORIGINAL_TITLE = "Original tittel";
    public static final String ONLINE_ISSN = "Online ISSN";
    public static final String PRINT_ISSN = "Print ISSN";
    public static final String LEVEL_2019 = "Nivå 2019";

    private final transient ObjectMapper objectMapper;
    private final transient CloseableHttpClient httpClient;
    private final transient String url;

    /**
     * Constructor for ChannelRegistryClient.
     *
     * @param objectMapper  object mapper instance
     * @param httpClient   http client instance
     * @param url   channel registry service url
     */
    public ChannelRegistryClient(ObjectMapper objectMapper, CloseableHttpClient httpClient, String url) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
        this.url = url;
    }

    /**
     * Fetch channels from service.
     *
     * @param tableId   table id of entity
     * @param searchTerm search term to search for
     * @return  list of channels
     * @throws IOException  thrown when problems occur when processing response from service
     * @throws NoResultsFoundException thrown when service respons but has no results
     */
    public List<Channel> fetchChannels(Integer tableId, String searchTerm) throws IOException, NoResultsFoundException {
        FetchJsonTableDataRequest fetchJsonTableDataRequest = FetchJsonTableDataRequest.create(tableId, searchTerm);
        System.out.println("Request: " + objectMapper.writeValueAsString(fetchJsonTableDataRequest));
        List<Channel> results = new ArrayList<>();
        HttpPost request = new HttpPost(url);
        request.setHeader(ACCEPT, APPLICATION_JSON.getMimeType());
        request.setHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType());
        request.setEntity(new StringEntity(objectMapper.writeValueAsString(fetchJsonTableDataRequest)));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = Optional.ofNullable(response.getEntity())
                    .orElseThrow(() -> new NoResultsFoundException(NO_RESULTS_FROM_SERVICE));
            String entityString = EntityUtils.toString(entity);
            JsonNode json = objectMapper.readTree(entityString);
            validateJsonResponse(json);
            json.forEach(result -> results.add(toChannel(result)));
        }
        return results;
    }

    private void validateJsonResponse(JsonNode jsonResponse) throws NoResultsFoundException {
        JsonPointer jsonPointer = JsonPointer.compile(RETURNED_POSTS_JSON_POINTER);
        JsonNode returnedPostsNode = jsonResponse.at(jsonPointer);
        if (returnedPostsNode.isNumber() && returnedPostsNode.intValue() == 0) {
            throw new NoResultsFoundException(NO_RESULTS_FROM_SERVICE);
        }
    }

    protected Channel toChannel(JsonNode json) {
        Channel channel = new Channel();
        if (json.has(ORIGINAL_TITLE)) {
            channel.setOriginalTitle(json.get(ORIGINAL_TITLE).textValue());
        }
        if (json.has(ONLINE_ISSN)) {
            channel.setOnlineIssn(json.get(ONLINE_ISSN).textValue());
        }
        if (json.has(PRINT_ISSN)) {
            channel.setOnlineIssn(json.get(PRINT_ISSN).textValue());
        }
        if (json.has(LEVEL_2019)) {
            try {
                channel.setLevel(Integer.parseInt(json.get(LEVEL_2019).textValue()));
            } catch (NumberFormatException e) {
                System.out.println("Error parsing level " + e.getMessage());
            }
        }
        return channel;
    }

}
