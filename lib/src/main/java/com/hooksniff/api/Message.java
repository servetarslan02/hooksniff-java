package com.hooksniff.api;

import com.hooksniff.HookSniffHttpClient;
import com.hooksniff.Utils;
import com.hooksniff.exceptions.ApiException;
import com.hooksniff.models.MessageIn;
import com.hooksniff.models.MessageOut;

import okhttp3.Headers;
import okhttp3.HttpUrl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Message (Webhook) API — HookSniff /v1/webhooks
 */
public class Message {
    private final HookSniffHttpClient client;

    public Message(HookSniffHttpClient client) {
        this.client = client;
    }

    public List<MessageOut> list() throws IOException, ApiException {
        return this.list(new MessageListOptions());
    }

    public List<MessageOut> list(final MessageListOptions options)
            throws IOException, ApiException {
        HttpUrl.Builder url =
                this.client.newUrlBuilder().encodedPath("/v1/webhooks");
        if (options.limit != null) {
            url.addQueryParameter("limit", Utils.serializeQueryParam(options.limit));
        }
        if (options.page != null) {
            url.addQueryParameter("page", Utils.serializeQueryParam(options.page));
        }
        if (options.status != null) {
            url.addQueryParameter("status", options.status);
        }
        if (options.endpointId != null) {
            url.addQueryParameter("endpoint_id", options.endpointId);
        }
        return this.client.executeRequest(
                "GET", url.build(), null, null, Utils.getListType(MessageOut.class));
    }

    public MessageOut create(final String endpointId, final String event, final Map<String, Object> data)
            throws IOException, ApiException {
        return this.create(endpointId, event, data, new MessageCreateOptions());
    }

    public MessageOut create(
            final String endpointId,
            final String event,
            final Map<String, Object> data,
            final MessageCreateOptions options)
            throws IOException, ApiException {
        HttpUrl.Builder url =
                this.client.newUrlBuilder().encodedPath("/v1/webhooks");
        Map<String, String> headers = new HashMap<>();
        if (options.idempotencyKey != null) {
            headers.put("idempotency-key", options.idempotencyKey);
        }
        Map<String, Object> body = new HashMap<>();
        body.put("endpoint_id", endpointId);
        body.put("event", event);
        body.put("data", data);
        return this.client.executeRequest(
                "POST", url.build(), Headers.of(headers), body, MessageOut.class);
    }

    public MessageOut get(final String deliveryId) throws IOException, ApiException {
        HttpUrl.Builder url =
                this.client.newUrlBuilder()
                        .encodedPath("/v1/webhooks/" + deliveryId);
        return this.client.executeRequest("GET", url.build(), null, null, MessageOut.class);
    }

    public MessageOut replay(final String deliveryId) throws IOException, ApiException {
        HttpUrl.Builder url =
                this.client.newUrlBuilder()
                        .encodedPath("/v1/webhooks/" + deliveryId + "/replay");
        return this.client.executeRequest("POST", url.build(), null, null, MessageOut.class);
    }

    public Map<String, Object> batch(final List<Map<String, Object>> webhooks)
            throws IOException, ApiException {
        HttpUrl.Builder url =
                this.client.newUrlBuilder().encodedPath("/v1/webhooks/batch");
        Map<String, Object> body = new HashMap<>();
        body.put("webhooks", webhooks);
        return this.client.executeRequest("POST", url.build(), null, body, Map.class);
    }

    public List<Map<String, Object>> getAttempts(final String deliveryId)
            throws IOException, ApiException {
        HttpUrl.Builder url =
                this.client.newUrlBuilder()
                        .encodedPath("/v1/webhooks/" + deliveryId + "/attempts");
        return this.client.executeRequest(
                "GET", url.build(), null, null, Utils.getListType(Map.class));
    }
}
