package no.unit.nva.channel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GatewayResponse<T> {

    private T body;
    private Map<String, String> headers;
    private int statusCode;

    public GatewayResponse() {
    }

    public GatewayResponse(final T body, final Map<String, String> headers, final int statusCode) {
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

    public void setBody(T body) {
        this.body = body;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
