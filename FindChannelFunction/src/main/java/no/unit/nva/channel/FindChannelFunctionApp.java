package no.unit.nva.channel;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.channel.exception.NoResultsFoundException;
import no.unit.nva.channel.model.incoming.SearchRequest;
import no.unit.nva.channel.model.outgoing.Channel;
import no.unit.nva.channel.model.outgoing.SearchResponse;
import org.apache.http.HttpHeaders;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemModule;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static org.zalando.problem.Status.NOT_FOUND;

public class FindChannelFunctionApp implements RequestStreamHandler {

    private final transient ObjectMapper objectMapper;
    private final transient ChannelRegistryClient channelRegistryClient;

    public static final String CHANNEL_REGISTRY_URI = "https://api.nsd.no/dbhapitjener/Tabeller/hentJSONTabellData";
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static final String PROBLEM_JSON = "application/problem+json";
    public transient String allowedOrigin;

    public FindChannelFunctionApp() {
        this(createObjectMapper(), new ChannelRegistryClient(createObjectMapper(), HttpClients.createDefault(),
                CHANNEL_REGISTRY_URI), new Environment());
    }

    public FindChannelFunctionApp(ObjectMapper objectMapper, ChannelRegistryClient channelRegistryClient, Environment environment) {
        this.objectMapper = objectMapper;
        this.channelRegistryClient = channelRegistryClient;
        this.allowedOrigin = environment.get("ALLOWED_ORIGIN").orElseThrow(IllegalStateException::new);
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        SearchRequest searchRequest;
        try {
            JsonNode event = objectMapper.readTree(input);
            searchRequest = objectMapper.readValue(event.get("body").asText(), SearchRequest.class);
        } catch (Exception e) {
            writeErrorResponse(output, Problem.valueOf(BAD_REQUEST, e.getMessage(), createProblemInstance(context)),
                    SC_BAD_REQUEST);
            return;
        }

        try {
            List<Channel> channels = channelRegistryClient.fetchChannels(searchRequest.getTableId(),
                    searchRequest.getSearchTerm());
            SearchResponse searchResponse = new SearchResponse(channels);
            writeResponse(output, searchResponse, SC_OK);
        } catch (NoResultsFoundException e) {
            writeErrorResponse(output, Problem.valueOf(NOT_FOUND, e.getMessage(), createProblemInstance(context)),
                    SC_NOT_FOUND);
        } catch (Exception e) {
            System.out.println(e);
            writeErrorResponse(output, Problem.valueOf(INTERNAL_SERVER_ERROR, e.getMessage(),
                    createProblemInstance(context)), SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void writeResponse(OutputStream output, Object body, int statusCode) throws IOException {
        Map<String,String> headers = new ConcurrentHashMap<>();
        headers.put(CONTENT_TYPE, APPLICATION_JSON.getMimeType());
        headers.put(ACCESS_CONTROL_ALLOW_ORIGIN, allowedOrigin);
        headers.put(HttpHeaders.VARY, "Origin");

        objectMapper.writeValue(output, new GatewayResponse<>(objectMapper.writeValueAsString(body), headers,
                statusCode));
    }

    private void writeErrorResponse(OutputStream output, Object body, int statusCode) throws IOException {
        Map<String,String> headers = new ConcurrentHashMap<>();
        headers.put(CONTENT_TYPE, PROBLEM_JSON);
        headers.put(ACCESS_CONTROL_ALLOW_ORIGIN, allowedOrigin);
        headers.put(HttpHeaders.VARY, "Origin");

        objectMapper.writeValue(output, new GatewayResponse<>(objectMapper.writeValueAsString(body), headers,
                statusCode));
    }

    public static ObjectMapper createObjectMapper() {
        return new ObjectMapper().registerModule(new ProblemModule());
    }

    public static URI createProblemInstance(Context context) {
        try {
            return new URIBuilder()
                    .setHost(context.getAwsRequestId())
                    .setPath(UUID.randomUUID().toString())
                    .build();
        } catch (URISyntaxException e) {
            System.out.println(e);
            return null;
        }
    }
}