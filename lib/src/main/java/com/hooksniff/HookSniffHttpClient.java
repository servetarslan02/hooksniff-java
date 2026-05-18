package com.hooksniff;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hooksniff.exceptions.ApiException;

import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.LockSupport;
import java.util.UUID;

public class HookSniffHttpClient {
    private final HttpUrl baseUrl;
    private final Map<String, String> defaultHeaders;
    private final List<Long> retrySchedule;
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public HookSniffHttpClient(
            HttpUrl baseUrl, Map<String, String> defaultHeaders, List<Long> retrySchedule) {
        this.baseUrl = baseUrl;
        this.defaultHeaders = defaultHeaders;
        this.retrySchedule = retrySchedule;
        this.client = new OkHttpClient();

        this.objectMapper = Utils.getObjectMapper();
    }

    public HttpUrl.Builder newUrlBuilder() {
        return new HttpUrl.Builder()
                .scheme(baseUrl.scheme())
                .host(baseUrl.host())
                .port(baseUrl.port());
    }

    public <Req, Res> Res executeRequest(
            String method, HttpUrl url, Headers headers, Req reqBody, Class<Res> responseClass)
            throws ApiException, IOException {
        Request.Builder reqBuilder = new Request.Builder().url(url);

        // Handle request body
        String jsonBody = "";
        if (reqBody != null) {
            jsonBody = objectMapper.writeValueAsString(reqBody);
            RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));
            reqBuilder.method(method, body);
        } else {
            // For methods that require a body (POST, PUT, PATCH), send empty JSON object
            if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "PATCH".equalsIgnoreCase(method)) {
                RequestBody emptyBody = RequestBody.create("{}", MediaType.parse("application/json"));
                reqBuilder.method(method, emptyBody);
            } else {
                reqBuilder.method(method, null);
            }
        }

        // Add default headers
        defaultHeaders.forEach(reqBuilder::addHeader);

        String idempotencyKey = headers == null ? null : headers.get("idempotency-key");
        if ((idempotencyKey == null || idempotencyKey.isEmpty()) && "POST".equals(method.toUpperCase())) {
                reqBuilder.addHeader("idempotency-key", "auto_" + UUID.randomUUID().toString());
        }

        // Add custom headers if present
        if (headers != null) {
            headers.forEach(pair -> reqBuilder.addHeader(pair.getFirst(), pair.getSecond()));
        }

        reqBuilder.addHeader(
                "hooksniff-req-id",
                String.valueOf(ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE)));

        Request request = reqBuilder.build();
        Response response = executeRequestWithRetry(request, jsonBody);

        if (response.body() == null) {
            throw new ApiException("Body is null", response.code(), "");
        }

        String bodyString = response.body().string();

        if (response.code() == 204) {
            return null;
        }

        if (response.code() >= 200 && response.code() < 300) {
            return objectMapper.readValue(bodyString, responseClass);
        }

        throw new ApiException(
                "Non 200 status code: `" + response.code() + "`", response.code(), bodyString);
    }

    private Response executeRequestWithRetry(Request request, String body) throws IOException {
        Response response = client.newCall(request).execute();

        int retryCount = 0;
        while (retryCount < retrySchedule.size()) {
            int statusCode = response.code();

            // Only retry on 429 (rate limit) or 5xx (server error)
            if (statusCode != 429 && statusCode < 500) {
                break;
            }

            response.close();

            long delayMs;
            if (statusCode == 429) {
                // 429 Rate Limit — respect Retry-After header
                String retryAfter = response.header("Retry-After");
                if (retryAfter != null) {
                    try {
                        delayMs = Long.parseLong(retryAfter) * 1000;
                    } catch (NumberFormatException e) {
                        delayMs = retrySchedule.get(retryCount);
                    }
                } else {
                    delayMs = retrySchedule.get(retryCount);
                }
            } else {
                // 5xx Server Error — exponential backoff with jitter
                long baseDelay = retrySchedule.get(retryCount);
                long jitter = ThreadLocalRandom.current().nextLong(0, baseDelay / 2 + 1);
                delayMs = baseDelay + jitter;
            }

            LockSupport.parkNanos(delayMs * 1_000_000); // Convert ms to ns

            Request retryRequest =
                    request.newBuilder()
                            .header("hooksniff-retry-count", String.valueOf(retryCount + 1))
                            .build();
            response = client.newCall(retryRequest).execute();
            retryCount++;
        }
        return response;
    }
}
