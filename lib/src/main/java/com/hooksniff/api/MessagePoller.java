package com.hooksniff.api;

import com.hooksniff.HookSniffHttpClient;
import com.hooksniff.exceptions.ApiException;
import okhttp3.HttpUrl;
import java.io.IOException;
import java.util.Map;

public class MessagePoller {
    private final HookSniffHttpClient client;
    public MessagePoller(HookSniffHttpClient client) { this.client = client; }

    public Map<String, Object> get(String pollerId) throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/api/v1/message-poller/" + pollerId);
        return this.client.executeRequest("GET", url.build(), null, null, Map.class);
    }
}
