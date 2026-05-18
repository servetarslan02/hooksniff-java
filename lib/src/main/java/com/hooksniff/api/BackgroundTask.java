package com.hooksniff.api;

import com.hooksniff.HookSniffHttpClient;
import com.hooksniff.exceptions.ApiException;
import okhttp3.HttpUrl;
import java.io.IOException;
import java.util.Map;
import java.util.List;

public class BackgroundTask {
    private final HookSniffHttpClient client;
    public BackgroundTask(HookSniffHttpClient client) { this.client = client; }

    public List<Map<String, Object>> list() throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/api/v1/background-tasks");
        return this.client.executeRequest("GET", url.build(), null, null, List.class);
    }

    public Map<String, Object> get(String taskId) throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/api/v1/background-tasks/" + taskId);
        return this.client.executeRequest("GET", url.build(), null, null, Map.class);
    }

    public Map<String, Object> cancel(String taskId) throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/api/v1/background-tasks/" + taskId);
        return this.client.executeRequest("PUT", url.build(), null, null, Map.class);
    }
}
