package com.hooksniff.api;

import com.hooksniff.HookSniffHttpClient;
import com.hooksniff.Utils;
import com.hooksniff.exceptions.ApiException;

import okhttp3.Headers;
import okhttp3.HttpUrl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Statistics {
    private final HookSniffHttpClient client;

    public Statistics(HookSniffHttpClient client) {
        this.client = client;
    }

    /** Creates the application event statistics. */
    public Map<String, Object> aggregateAppStats(
            final StatisticsAggregateAppStatsOptions options) throws IOException, ApiException {
        HttpUrl.Builder url =
                this.client
                        .newUrlBuilder()
                        .encodedPath("/api/v1/stats/usage/app");
        Map<String, String> headers = new HashMap<>();
        if (options.idempotencyKey != null) {
            headers.put("idempotency-key", options.idempotencyKey);
        }
        return this.client.executeRequest(
                "POST", url.build(), Headers.of(headers), null, Map.class);
    }

    /** Creates the application event statistics. */
    public Map<String, Object> aggregateAppStats() throws IOException, ApiException {
        return this.aggregateAppStats(new StatisticsAggregateAppStatsOptions());
    }

    /** Get endpoint stats for the given app. */
    public Map<String, Object> getAppEndpointStats(final String appId) throws IOException, ApiException {
        HttpUrl.Builder url =
                this.client
                        .newUrlBuilder()
                        .encodedPath(
                                String.format("/api/v1/stats/app/%s/endpoint", appId));
        return this.client.executeRequest("GET", url.build(), null, null, Map.class);
    }
}
