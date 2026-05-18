package com.hooksniff;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Response metadata from the last API request.
 *
 * <pre>{@code
 * List<EndpointOut> endpoints = client.getEndpoint().list();
 * ResponseMetadata meta = client.getLastResponse();
 * System.out.println(meta.getRequestId());
 * System.out.println(meta.getRateLimitRemaining());
 * }</pre>
 */
public class ResponseMetadata {
    private final int statusCode;
    private final String requestId;
    private final Integer rateLimitRemaining;
    private final Integer rateLimitReset;
    private final Map<String, List<String>> headers;

    public ResponseMetadata(int statusCode, String requestId, Integer rateLimitRemaining,
                            Integer rateLimitReset, Map<String, List<String>> headers) {
        this.statusCode = statusCode;
        this.requestId = requestId;
        this.rateLimitRemaining = rateLimitRemaining;
        this.rateLimitReset = rateLimitReset;
        this.headers = headers != null ? headers : Collections.emptyMap();
    }

    public int getStatusCode() { return statusCode; }
    public String getRequestId() { return requestId; }
    public Integer getRateLimitRemaining() { return rateLimitRemaining; }
    public Integer getRateLimitReset() { return rateLimitReset; }
    public Map<String, List<String>> getHeaders() { return headers; }

    /** Get first value for a header name (case-insensitive). */
    public String getHeader(String name) {
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(name) && !entry.getValue().isEmpty()) {
                return entry.getValue().get(0);
            }
        }
        return null;
    }
}
