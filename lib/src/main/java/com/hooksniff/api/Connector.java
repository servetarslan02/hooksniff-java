package com.hooksniff.api;

import com.hooksniff.HookSniffHttpClient;
import com.hooksniff.exceptions.ApiException;
import okhttp3.HttpUrl;
import java.io.IOException;
import java.util.Map;
import java.util.List;

public class Connector {
    private final HookSniffHttpClient client;
    public Connector(HookSniffHttpClient client) { this.client = client; }

    public List<Map<String, Object>> list() throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/v1/connectors");
        return this.client.executeRequest("GET", url.build(), null, null, List.class);
    }

    public Map<String, Object> get(String id) throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/v1/connectors/" + id);
        return this.client.executeRequest("GET", url.build(), null, null, Map.class);
    }

    public List<Map<String, Object>> listConfigs() throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/v1/connectors/configs");
        return this.client.executeRequest("GET", url.build(), null, null, List.class);
    }

    public Map<String, Object> createConfig(Map<String, Object> body) throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/v1/connectors/configs");
        return this.client.executeRequest("POST", url.build(), null, body, Map.class);
    }

    public Map<String, Object> updateConfig(String id, Map<String, Object> body) throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/v1/connectors/configs/" + id);
        return this.client.executeRequest("PUT", url.build(), null, body, Map.class);
    }

    public void deleteConfig(String id) throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/v1/connectors/configs/" + id);
        this.client.executeRequest("DELETE", url.build(), null, null, null);
    }
}
