package com.hooksniff.api;

import com.hooksniff.HookSniffHttpClient;
import com.hooksniff.Utils;
import com.hooksniff.exceptions.ApiException;
import com.hooksniff.models.*;

import java.io.IOException;
import java.util.List;

public class Integration {
    private final HookSniffHttpClient client;

    public Integration(HookSniffHttpClient client) { this.client = client; }

    public List<IntegrationOut> list() throws IOException, ApiException {
        return client.executeRequest("GET", client.newUrlBuilder().encodedPath("/v1/integrations").build(), null, null, Utils.getListType(IntegrationOut.class));
    }

    public IntegrationOut get(String id) throws IOException, ApiException {
        return client.executeRequest("GET", client.newUrlBuilder().encodedPath("/v1/integrations/" + id).build(), null, null, IntegrationOut.class);
    }

    public IntegrationOut create(IntegrationIn body) throws IOException, ApiException {
        return client.executeRequest("POST", client.newUrlBuilder().encodedPath("/v1/integrations").build(), null, body, IntegrationOut.class);
    }

    public IntegrationOut update(String id, IntegrationUpdate body) throws IOException, ApiException {
        return client.executeRequest("PUT", client.newUrlBuilder().encodedPath("/v1/integrations/" + id).build(), null, body, IntegrationOut.class);
    }

    public void delete(String id) throws IOException, ApiException {
        client.executeRequest("DELETE", client.newUrlBuilder().encodedPath("/v1/integrations/" + id).build(), null, null, null);
    }

    public IntegrationTestResponse test(String id) throws IOException, ApiException {
        return client.executeRequest("POST", client.newUrlBuilder().encodedPath("/v1/integrations/" + id + "/test").build(), null, null, IntegrationTestResponse.class);
    }

    public List<IntegrationEventOut> listEvents(String id) throws IOException, ApiException {
        return client.executeRequest("GET", client.newUrlBuilder().encodedPath("/v1/integrations/" + id + "/events").build(), null, null, Utils.getListType(IntegrationEventOut.class));
    }

    public IntegrationStatsOut getStats(String id) throws IOException, ApiException {
        return client.executeRequest("GET", client.newUrlBuilder().encodedPath("/v1/integrations/" + id + "/stats").build(), null, null, IntegrationStatsOut.class);
    }
}
