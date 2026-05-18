package com.hooksniff.api;

import com.hooksniff.HookSniffHttpClient;
import com.hooksniff.exceptions.ApiException;

import okhttp3.Headers;
import okhttp3.HttpUrl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Authentication {
    private final HookSniffHttpClient client;

    public Authentication(HookSniffHttpClient client) {
        this.client = client;
    }

    /** Get portal access URL for the given app. */
    public Map<String, Object> appPortalAccess(
            final String appId, final AuthenticationAppPortalAccessOptions options)
            throws IOException, ApiException {
        HttpUrl.Builder url =
                this.client
                        .newUrlBuilder()
                        .encodedPath(
                                String.format("/api/v1/auth/portal/%s", appId));
        Map<String, String> headers = new HashMap<>();
        if (options.idempotencyKey != null) {
            headers.put("idempotency-key", options.idempotencyKey);
        }
        return this.client.executeRequest(
                "POST", url.build(), Headers.of(headers), null, Map.class);
    }

    /** Get portal access URL for the given app. */
    public Map<String, Object> appPortalAccess(final String appId)
            throws IOException, ApiException {
        return this.appPortalAccess(appId, new AuthenticationAppPortalAccessOptions());
    }

    /** Expire all auth tokens for the given user. */
    public void expireAll(final String appId, final AuthenticationExpireAllOptions options)
            throws IOException, ApiException {
        HttpUrl.Builder url =
                this.client
                        .newUrlBuilder()
                        .encodedPath(
                                String.format("/api/v1/auth/expire-all/%s", appId));
        Map<String, String> headers = new HashMap<>();
        if (options.idempotencyKey != null) {
            headers.put("idempotency-key", options.idempotencyKey);
        }
        this.client.executeRequest("POST", url.build(), Headers.of(headers), null, null);
    }

    /** Expire all auth tokens for the given user. */
    public void expireAll(final String appId) throws IOException, ApiException {
        this.expireAll(appId, new AuthenticationExpireAllOptions());
    }

    /** Logout the current auth token. */
    public void logout() throws IOException, ApiException {
        this.logout(new AuthenticationLogoutOptions());
    }

    /** Logout the current auth token. */
    public void logout(final AuthenticationLogoutOptions options) throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/api/v1/auth/logout");
        Map<String, String> headers = new HashMap<>();
        if (options.idempotencyKey != null) {
            headers.put("idempotency-key", options.idempotencyKey);
        }
        this.client.executeRequest("POST", url.build(), Headers.of(headers), null, null);
    }
}
