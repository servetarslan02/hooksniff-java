package com.hooksniff.api;

import com.hooksniff.HookSniffHttpClient;
import com.hooksniff.exceptions.ApiException;
import com.hooksniff.models.PollingEndpointMessageOut;
import com.hooksniff.models.PollingEndpointConsumerSeekIn;
import com.hooksniff.models.PollingEndpointConsumerSeekOut;

import okhttp3.HttpUrl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MessagePoller {
    private final HookSniffHttpClient client;

    public MessagePoller(HookSniffHttpClient client) {
        this.client = client;
    }

    /** Poll messages from the message poller. */
    public List<PollingEndpointMessageOut> poll() throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/v1/message-poller/poll");
        return this.client.executeRequest(
                "GET", url.build(), null, null,
                (Class<List<PollingEndpointMessageOut>>) (Class<?>) List.class);
    }

    /** Poll messages with query parameters. */
    public List<PollingEndpointMessageOut> poll(Map<String, String> queryParams) throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/v1/message-poller/poll");
        if (queryParams != null) {
            queryParams.forEach(url::addQueryParameter);
        }
        return this.client.executeRequest(
                "GET", url.build(), null, null,
                (Class<List<PollingEndpointMessageOut>>) (Class<?>) List.class);
    }

    /** Seek the poller cursor to a specific position. */
    public PollingEndpointConsumerSeekOut seek(PollingEndpointConsumerSeekIn body) throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/v1/message-poller/seek");
        return this.client.executeRequest("POST", url.build(), null, body, PollingEndpointConsumerSeekOut.class);
    }

    /** Commit the poller cursor (acknowledge consumed messages). */
    public void commit() throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/v1/message-poller/commit");
        this.client.executeRequest("POST", url.build(), null, null, null);
    }
}
