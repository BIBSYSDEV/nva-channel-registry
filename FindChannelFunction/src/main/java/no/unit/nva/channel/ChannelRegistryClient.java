package no.unit.nva.channel;

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

import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

public class ChannelRegistryClient {

    private final transient ObjectMapper objectMapper;
    private final transient CloseableHttpClient httpClient;
    private final transient String url;

    public ChannelRegistryClient(ObjectMapper objectMapper, CloseableHttpClient httpClient, String url) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
        this.url = url;
    }

    public List<Channel> fetchChannels(Integer tableId, String searchTerm) throws IOException, NoResultsFoundException {
        FetchJsonTableDataRequest fetchJsonTableDataRequest = FetchJsonTableDataRequest.create(tableId, searchTerm);
        List<Channel> results = new ArrayList<>();
        HttpPost request = new HttpPost(url);
        request.setHeader(ACCEPT, APPLICATION_JSON.getMimeType());
        request.setHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType());
        request.setEntity(new StringEntity(objectMapper.writeValueAsString(fetchJsonTableDataRequest)));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                throw new NoResultsFoundException("Response from registry is empty.");
            }
            String entityString = EntityUtils.toString(entity);
            JsonNode json = objectMapper.readTree(entityString);
            validateJsonResponse(json);
            json.forEach(result -> {
                results.add(toChannel(result));
            });
        }
        return results;
    }

    private void validateJsonResponse(JsonNode jsonResponse) throws NoResultsFoundException {
        if (jsonResponse.has(1)
                && jsonResponse.get(1).at("/returnerte poster").isNumber()
                && jsonResponse.get(1).at("/returnerte poster").intValue() == 0) {
            throw new NoResultsFoundException("No results found for this search term.");
        }
    }

    protected Channel toChannel(JsonNode json) {
        Channel channel = new Channel();
        if (json.has("Original tittel")) {
            channel.setOriginalTitle(json.get("Original tittel").textValue());
        }
        if (json.has("Online ISSN")) {
            channel.setOnlineIssn(json.get("Online ISSN").textValue());
        }
        if (json.has("Print ISSN")) {
            channel.setOnlineIssn(json.get("Print ISSN").textValue());
        }
        if (json.has("Nivå 2019")) {
            try {
                channel.setLevel(Integer.parseInt(json.get("Nivå 2019").textValue()));
            } catch (NumberFormatException e) {
                System.out.println("Error parsing level " + e.getMessage());
            }
        }
        return channel;
    }

}
