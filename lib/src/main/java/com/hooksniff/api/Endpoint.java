package com.hooksniff.api;

import com.hooksniff.HookSniffHttpClient;
import com.hooksniff.Utils;
import com.hooksniff.exceptions.ApiException;
import com.hooksniff.models.EndpointIn;
import com.hooksniff.models.EndpointOut;
import com.hooksniff.models.EndpointPatch;
import com.hooksniff.models.EndpointUpdate;

import okhttp3.Headers;
import okhttp3.HttpUrl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Endpoint API — HookSniff /v1/endpoints
 * Note: HookSniff uses JWT auth (no app_id needed).
 */
public class Endpoint {
    private final HookSniffHttpClient client;

    public Endpoint(HookSniffHttpClient client) {
        this.client = client;
    }

    public List<EndpointOut> list() throws IOException, ApiException {
        return this.list(new EndpointListOptions());
    }

    public List<EndpointOut> list(final EndpointListOptions options)
            throws IOException, ApiException {
        HttpUrl.Builder url =
                this.client.newUrlBuilder().encodedPath("/v1/endpoints");
        if (options.limit != null) {
            url.addQueryParameter("limit", Utils.serializeQueryParam(options.limit));
        }
        if (options.iterator != null) {
            url.addQueryParameter("iterator", options.iterator);
        }
        return this.client.executeRequest(
                "GET", url.build(), null, null, Utils.getListType(EndpointOut.class));
    }

    public EndpointOut create(final EndpointIn endpointIn)
            throws IOException, ApiException {
        return this.create(endpointIn, new EndpointCreateOptions());
    }

    public EndpointOut create(final EndpointIn endpointIn, final EndpointCreateOptions options)
            throws IOException, ApiException {
        HttpUrl.Builder url =
                this.client.newUrlBuilder().encodedPath("/v1/endpoints");
        Map<String, String> headers = new HashMap<>();
        if (options.idempotencyKey != null) {
            headers.put("idempotency-key", options.idempotencyKey);
        }
        return this.client.executeRequest(
                "POST", url.build(), Headers.of(headers), endpointIn, EndpointOut.class);
    }

    public EndpointOut get(final String endpointId) throws IOException, ApiException {
        HttpUrl.Builder url =
                this.client.newUrlBuilder()
                        .encodedPath("/v1/endpoints/" + endpointId);
        return this.client.executeRequest("GET", url.build(), null, null, EndpointOut.class);
    }

    public EndpointOut update(final String endpointId, final EndpointUpdate endpointUpdate)
            throws IOException, ApiException {
        HttpUrl.Builder url =
                this.client.newUrlBuilder()
                        .encodedPath("/v1/endpoints/" + endpointId);
        return this.client.executeRequest(
                "PUT", url.build(), null, endpointUpdate, EndpointOut.class);
    }

    public void delete(final String endpointId) throws IOException, ApiException {
        HttpUrl.Builder url =
                this.client.newUrlBuilder()
                        .encodedPath("/v1/endpoints/" + endpointId);
        this.client.executeRequest("DELETE", url.build(), null, null, null);
    }

    public EndpointOut patch(final String endpointId, final EndpointPatch endpointPatch)
            throws IOException, ApiException {
        HttpUrl.Builder url =
                this.client.newUrlBuilder()
                        .encodedPath("/v1/endpoints/" + endpointId);
        return this.client.executeRequest(
                "PATCH", url.build(), null, endpointPatch, EndpointOut.class);
    }

    public Map<String, Object> rotateSecret(final String endpointId)
            throws IOException, ApiException {
        HttpUrl.Builder url =
                this.client.newUrlBuilder()
                        .encodedPath("/v1/endpoints/" + endpointId + "/rotate-secret");
        return this.client.executeRequest("POST", url.build(), null, null, Map.class);
    }

    public Map<String, Object> getStats(final String endpointId)
            throws IOException, ApiException {
        return this.getStats(endpointId, new EndpointGetStatsOptions());
    }

    public Map<String, Object> getStats(final String endpointId, final EndpointGetStatsOptions options)
            throws IOException, ApiException {
        HttpUrl.Builder url =
                this.client.newUrlBuilder()
                        .encodedPath("/v1/endpoints/" + endpointId + "/stats");
        if (options.since != null) {
            url.addQueryParameter("since", Utils.serializeQueryParam(options.since));
        }
        if (options.until != null) {
            url.addQueryParameter("until", Utils.serializeQueryParam(options.until));
        }
        return this.client.executeRequest("GET", url.build(), null, null, Map.class);
    }
}
