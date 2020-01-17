package no.unit.nva.channel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GatewayResponse<T> {

    private final T body;
    private final Map<String, String> headers;
    private final int statusCode;

    @JsonCreator
    public GatewayResponse(
            @JsonProperty("body") final T body,
            @JsonProperty("headers") final Map<String, String> headers,
            @JsonProperty("statusCode") final int statusCode) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = Collections.unmodifiableMap(new HashMap<>(headers));
    }

    public T getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
