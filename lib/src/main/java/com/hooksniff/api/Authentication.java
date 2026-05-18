package com.hooksniff.api;

import com.hooksniff.HookSniffHttpClient;
import com.hooksniff.exceptions.ApiException;

import okhttp3.Headers;
import okhttp3.HttpUrl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Authentication API — HookSniff /v1/auth
 */
public class Authentication {
    private final HookSniffHttpClient client;

    public Authentication(HookSniffHttpClient client) {
        this.client = client;
    }

    /** Login with email and password. Returns JWT token. */
    public Map<String, Object> login(final String email, final String password)
            throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/v1/auth/login");
        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        return this.client.executeRequest("POST", url.build(), null, body, Map.class);
    }

    /** Register a new account. */
    public Map<String, Object> register(final String email, final String password, final String name)
            throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/v1/auth/register");
        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        if (name != null) {
            body.put("name", name);
        }
        return this.client.executeRequest("POST", url.build(), null, body, Map.class);
    }

    /** Get current user profile. */
    public Map<String, Object> getMe() throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/v1/auth/me");
        return this.client.executeRequest("GET", url.build(), null, null, Map.class);
    }

    /** Logout (invalidate refresh token). */
    public void logout() throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/v1/auth/logout");
        this.client.executeRequest("POST", url.build(), null, null, null);
    }

    /** Export user data (GDPR). */
    public Map<String, Object> exportData() throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/v1/auth/export");
        return this.client.executeRequest("GET", url.build(), null, null, Map.class);
    }

    /** Delete account (GDPR). */
    public void deleteAccount() throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/v1/auth/account");
        this.client.executeRequest("DELETE", url.build(), null, null, null);
    }

    /** Change password. */
    public void changePassword(final String currentPassword, final String newPassword)
            throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/v1/auth/password");
        Map<String, Object> body = new HashMap<>();
        body.put("current_password", currentPassword);
        body.put("new_password", newPassword);
        this.client.executeRequest("PUT", url.build(), null, body, null);
    }

    /** Request password reset email. */
    public void forgotPassword(final String email) throws IOException, ApiException {
        HttpUrl.Builder url = this.client.newUrlBuilder().encodedPath("/v1/auth/forgot-password");
        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        this.client.executeRequest("POST", url.build(), null, body, null);
    }
}
