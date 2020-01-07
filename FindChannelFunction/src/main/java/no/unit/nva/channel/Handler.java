package no.unit.nva.channel;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.channel.exception.ChannelsNotFoundException;
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

public class Handler implements RequestStreamHandler {

    private final ObjectMapper objectMapper;
    private final ChannelRegistry channelRegistry;

    public Handler() {
        objectMapper = new ObjectMapper();
        channelRegistry = new ChannelRegistry(objectMapper, HttpClients.createDefault());
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        Map<String,String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, "application/json");
        headers.put("Access-Control-Allow-Origin", "http://localhost:3000");

        SearchRequest request;
        try {
            JsonNode event = objectMapper.readTree(input);
            request = objectMapper.readValue(event.get("body").asText(), SearchRequest.class);
        } catch (IOException e) {
            objectMapper.writeValue(output, new GatewayResponse<>(new ErrorMessage(e.getMessage()), headers, SC_BAD_REQUEST));
            return;
        }

        try {
            List<Channel> channels = channelRegistry.fetchChannels(request.getSearchTerm());
            SearchResponse response = new SearchResponse(channels);
            objectMapper.writeValue(output, new GatewayResponse<>(objectMapper.writeValueAsString(response), headers, SC_OK));
        } catch (ChannelsNotFoundException e) {
            objectMapper.writeValue(output, new GatewayResponse<>(new ErrorMessage(e.getMessage()), headers, SC_NOT_FOUND));
        } catch (Exception e) {
            e.printStackTrace();
            objectMapper.writeValue(output, new GatewayResponse<>(new ErrorMessage(e.getMessage()), headers, SC_INTERNAL_SERVER_ERROR));
        }
    }
}