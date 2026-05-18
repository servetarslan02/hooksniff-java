package com.hooksniff.api;

import com.hooksniff.HookSniffHttpClient;
import com.hooksniff.exceptions.ApiException;
import okhttp3.HttpUrl;
import java.io.IOException;
import java.util.Map;
import java.util.List;

public class Inbound {
    private final HookSniffHttpClient client;
    public Inbound(HookSniffHttpClient client) { this.client = client; }

    public List<Map<String, Object>> listConfigs() throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/api/v1/inbound/configs");
        return this.client.executeRequest("GET", url.build(), null, null, List.class);
    }

    public Map<String, Object> createConfig(Map<String, Object> body) throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/api/v1/inbound/configs");
        return this.client.executeRequest("POST", url.build(), null, body, Map.class);
    }

    public Map<String, Object> handleInbound(Map<String, Object> body) throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/api/v1/inbound");
        return this.client.executeRequest("POST", url.build(), null, body, Map.class);
    }
}
