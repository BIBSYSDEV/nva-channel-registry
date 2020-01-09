package no.unit.nva.channel;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.channel.exception.NoResultsFoundException;
import no.unit.nva.channel.model.incoming.SearchRequest;
import no.unit.nva.channel.model.outgoing.Channel;
import no.unit.nva.channel.model.outgoing.ErrorMessage;
import no.unit.nva.channel.model.outgoing.SearchResponse;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.HttpStatus.*;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

public class App implements RequestStreamHandler {

    private final ObjectMapper objectMapper;
    private final ChannelRegistryClient channelRegistryClient;

    public static final String CHANNEL_REGISTRY_URI = "https://api.nsd.no/dbhapitjener/Tabeller/hentJSONTabellData";
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static final String CORS_ORIGIN = "http://localhost:3000";

    public App() {
        this(new ObjectMapper(), new ChannelRegistryClient(new ObjectMapper(), HttpClients.createDefault(), CHANNEL_REGISTRY_URI));
    }

    public App(ObjectMapper objectMapper, ChannelRegistryClient channelRegistryClient) {
        this.objectMapper = objectMapper;
        this.channelRegistryClient = channelRegistryClient;
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        Map<String,String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, APPLICATION_JSON.getMimeType());
        headers.put(ACCESS_CONTROL_ALLOW_ORIGIN, CORS_ORIGIN);

        SearchRequest searchRequest;
        try {
            JsonNode event = objectMapper.readTree(input);
            searchRequest = objectMapper.readValue(event.get("body").asText(), SearchRequest.class);
        } catch (Exception e) {
            writeGatewayResponse(output, new ErrorMessage("Invalid JSON in request body."), headers, SC_BAD_REQUEST);
            return;
        }

        try {
            List<Channel> channels = channelRegistryClient.fetchChannels(searchRequest.getTableId(), searchRequest.getSearchTerm());
            SearchResponse searchResponse = new SearchResponse(channels);
            writeGatewayResponse(output, searchResponse, headers, SC_OK);
        } catch (NoResultsFoundException e) {
            writeGatewayResponse(output, new ErrorMessage(e.getMessage()), headers, SC_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            writeGatewayResponse(output, new ErrorMessage(e.getMessage()), headers, SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void writeGatewayResponse(OutputStream output, Object body, Map<String,String> headers, int statusCode) throws IOException{
        objectMapper.writeValue(output, new GatewayResponse<>(objectMapper.writeValueAsString(body), headers, statusCode));
    }
}