package com.hooksniff.api;

import com.hooksniff.HookSniffHttpClient;
import com.hooksniff.Utils;
import com.hooksniff.exceptions.ApiException;
import com.hooksniff.models.MessageAttemptOut;

import okhttp3.Headers;
import okhttp3.HttpUrl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MessageAttempt API — HookSniff /v1/webhooks/{id}/attempts
 */
public class MessageAttempt {
    private final HookSniffHttpClient client;

    public MessageAttempt(HookSniffHttpClient client) {
        this.client = client;
    }

    public List<MessageAttemptOut> listByDelivery(final String deliveryId)
            throws IOException, ApiException {
        return this.listByDelivery(deliveryId, new MessageAttemptListByEndpointOptions());
    }

    public List<MessageAttemptOut> listByDelivery(
            final String deliveryId, final MessageAttemptListByEndpointOptions options)
            throws IOException, ApiException {
        HttpUrl.Builder url =
                this.client.newUrlBuilder()
                        .encodedPath("/v1/webhooks/" + deliveryId + "/attempts");
        if (options.limit != null) {
            url.addQueryParameter("limit", Utils.serializeQueryParam(options.limit));
        }
        if (options.status != null) {
            url.addQueryParameter("status", Utils.serializeQueryParam(options.status));
        }
        return this.client.executeRequest(
                "GET", url.build(), null, null, Utils.getListType(MessageAttemptOut.class));
    }

    public MessageAttemptOut get(final String deliveryId, final String attemptId)
            throws IOException, ApiException {
        HttpUrl.Builder url =
                this.client.newUrlBuilder()
                        .encodedPath("/v1/webhooks/" + deliveryId + "/attempts/" + attemptId);
        return this.client.executeRequest(
                "GET", url.build(), null, null, MessageAttemptOut.class);
    }

    public void resend(final String deliveryId, final String endpointId)
            throws IOException, ApiException {
        this.resend(deliveryId, endpointId, new MessageAttemptResendOptions());
    }

    public void resend(
            final String deliveryId,
            final String endpointId,
            final MessageAttemptResendOptions options)
            throws IOException, ApiException {
        HttpUrl.Builder url =
                this.client.newUrlBuilder()
                        .encodedPath("/v1/webhooks/" + deliveryId + "/endpoint/" + endpointId + "/resend");
        Map<String, String> headers = new HashMap<>();
        if (options.idempotencyKey != null) {
            headers.put("idempotency-key", options.idempotencyKey);
        }
        this.client.executeRequest("POST", url.build(), Headers.of(headers), null, null);
    }
}
