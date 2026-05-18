package com.hooksniff.api;

import com.hooksniff.HookSniffHttpClient;
import com.hooksniff.exceptions.ApiException;
import okhttp3.HttpUrl;
import java.io.IOException;
import java.util.Map;
import java.util.List;

public class OperationalWebhook {
    private final HookSniffHttpClient client;
    public OperationalWebhook(HookSniffHttpClient client) { this.client = client; }

    public List<Map<String, Object>> list() throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/api/v1/operational-webhooks");
        return this.client.executeRequest("GET", url.build(), null, null, List.class);
    }

    public Map<String, Object> create(Map<String, Object> body) throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/api/v1/operational-webhooks");
        return this.client.executeRequest("POST", url.build(), null, body, Map.class);
    }

    public Map<String, Object> get(String id) throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/api/v1/operational-webhooks/" + id);
        return this.client.executeRequest("GET", url.build(), null, null, Map.class);
    }

    public Map<String, Object> update(String id, Map<String, Object> body) throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/api/v1/operational-webhooks/" + id);
        return this.client.executeRequest("PUT", url.build(), null, body, Map.class);
    }

    public void delete(String id) throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/api/v1/operational-webhooks/" + id);
        this.client.executeRequest("DELETE", url.build(), null, null, null);
    }

    public List<Map<String, Object>> listDeliveries(String id) throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/api/v1/operational-webhooks/" + id + "/deliveries");
        return this.client.executeRequest("GET", url.build(), null, null, List.class);
    }
}
