package no.unit.nva.channel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.channel.exception.ChannelsNotFoundException;
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

public class ChannelRegistry {

    private final ObjectMapper objectMapper;
    private final CloseableHttpClient httpClient;

    public ChannelRegistry(ObjectMapper objectMapper, CloseableHttpClient httpClient) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    public List<Channel> fetchChannels(String searchTerm) throws IOException, ChannelsNotFoundException {
        FetchJsonTableDataRequest fetchJsonTableDataRequest = FetchJsonTableDataRequest.searchTerm(searchTerm);
        List<Channel> results = new ArrayList<>();
        HttpPost request = new HttpPost("https://api.nsd.no/dbhapitjener/Tabeller/hentJSONTabellData");
        request.setHeader(ACCEPT, "application/json");
        request.setHeader(CONTENT_TYPE, "application/json");
        request.setEntity(new StringEntity(objectMapper.writeValueAsString(fetchJsonTableDataRequest)));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            JsonNode json;
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String entityString = EntityUtils.toString(entity);
                json = objectMapper.readTree(entityString);

                validateResponseHasResults(json);
                json.forEach(result -> {
                    results.add(toChannel(result));
                });
            }

        }
        return results;
    }

    private void validateResponseHasResults(JsonNode json) throws ChannelsNotFoundException {
        if (json.has(0) && json.get(0).at("/status/antall").isNumber() && json.get(0).at("/status/antall").intValue() == 0) {
            throw new ChannelsNotFoundException(json.get(0).at("/status/melding").textValue());
        }
    }

    protected Channel toChannel(JsonNode jsonNode) {
        return new Channel(
                jsonNode.get("Original tittel").asText(),
                jsonNode.get("Online ISSN").asText(),
                jsonNode.get("Print ISSN").asText(),
                null,
                jsonNode.get("Forlag").asText()
        );
    }

}
