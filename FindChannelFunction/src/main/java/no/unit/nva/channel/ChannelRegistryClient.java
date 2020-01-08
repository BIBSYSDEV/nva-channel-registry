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

public class ChannelRegistryClient {

    private final ObjectMapper objectMapper;
    private final CloseableHttpClient httpClient;
    private final String url;

    public ChannelRegistryClient(ObjectMapper objectMapper, CloseableHttpClient httpClient, String url) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
        this.url = url;
    }

    public List<Channel> fetchChannels(String searchTerm) throws IOException, NoResultsFoundException {
        FetchJsonTableDataRequest fetchJsonTableDataRequest = FetchJsonTableDataRequest.searchTerm(searchTerm);
        List<Channel> results = new ArrayList<>();
        HttpPost request = new HttpPost(url);
        request.setHeader(ACCEPT, "application/json");
        request.setHeader(CONTENT_TYPE, "application/json");
        request.setEntity(new StringEntity(objectMapper.writeValueAsString(fetchJsonTableDataRequest)));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            JsonNode json;
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                throw new NoResultsFoundException("Response from registry is empty.");
            }
            String entityString = EntityUtils.toString(entity);
            json = objectMapper.readTree(entityString);
            validateJsonResponse(json);
            json.forEach(result -> {
                results.add(toChannel(result));
            });
        }
        return results;
    }

    private void validateJsonResponse(JsonNode jsonResponse) throws NoResultsFoundException {
        if (jsonResponse.has(1) &&
                jsonResponse.get(1).at("/returnerte poster").isNumber() &&
                jsonResponse.get(1).at("/returnerte poster").intValue() == 0) {
            throw new NoResultsFoundException("No results found for this search term.");
        }
    }

    protected Channel toChannel(JsonNode jsonNode) {
        return new Channel(
                jsonNode.get("Original tittel").textValue(),
                jsonNode.get("Online ISSN").textValue(),
                jsonNode.get("Print ISSN").textValue(),
                null,
                jsonNode.get("Forlag").textValue()
        );
    }

}
