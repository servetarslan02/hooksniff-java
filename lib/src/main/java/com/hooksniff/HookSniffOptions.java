package com.hooksniff;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public final class HookSniffOptions {
    public static final String DEFAULT_URL = "https://hooksniff-api-1046140057667.europe-west1.run.app";

    private String serverUrl;
    private final List<Long> retrySchedule = Arrays.asList(1000L, 2000L, 4000L);
    private boolean debug = false;
    private long timeoutMs = 30000;
    private Map<String, String> headers = new HashMap<>();

    public HookSniffOptions serverUrl(String serverUrl) {
        this.serverUrl = serverUrl;
        return this;
    }

    public HookSniffOptions debug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public HookSniffOptions timeoutMs(long timeoutMs) {
        this.timeoutMs = timeoutMs;
        return this;
    }

    public HookSniffOptions header(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    public HookSniffOptions headers(Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }
}
