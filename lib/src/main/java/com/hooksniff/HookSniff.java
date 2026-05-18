package com.hooksniff;

import com.hooksniff.api.*;

import lombok.Getter;

import okhttp3.HttpUrl;

import java.util.HashMap;
import java.util.Map;

@Getter
public class HookSniff {
    private final HookSniffHttpClient httpClient;
    private final Authentication authentication;
    private final Endpoint endpoint;
    private final EventType eventType;
    private final Health health;
    private final Message message;
    private final MessageAttempt messageAttempt;
    private final Statistics statistics;
    private final Environment environment;
    private final BackgroundTask backgroundTask;
    private final OperationalWebhook operationalWebhook;
    private final MessagePoller messagePoller;
    private final Inbound inbound;
    private final Connector connector;
    private final Integration integration;
    private final Stream stream;

    public HookSniff(String token) {
        this(token, new HookSniffOptions());
    }

    public HookSniff(String token, HookSniffOptions options) {
        if (options.getServerUrl() == null) {
            options.setServerUrl(HookSniffOptions.DEFAULT_URL);
        }

        HttpUrl parsedUrl = HttpUrl.parse(options.getServerUrl());
        if (parsedUrl == null) {
            throw new IllegalArgumentException("Invalid base url");
        }

        Map<String, String> defaultHeaders = new HashMap<>();
        String sdkUA = "hooksniff-libs/" + Version.VERSION + "/java";
        defaultHeaders.put("User-Agent", sdkUA);
        defaultHeaders.put("X-HookSniff-SDK", sdkUA);
        defaultHeaders.put("Authorization", "Bearer " + token);

        this.httpClient = new HookSniffHttpClient(parsedUrl, token, defaultHeaders, options.getRetrySchedule(), options.isDebug());

        this.authentication = new Authentication(this.httpClient);
        this.endpoint = new Endpoint(this.httpClient);
        this.eventType = new EventType(this.httpClient);
        this.health = new Health(this.httpClient);
        this.message = new Message(this.httpClient);
        this.messageAttempt = new MessageAttempt(this.httpClient);
        this.statistics = new Statistics(this.httpClient);
        this.environment = new Environment(this.httpClient);
        this.backgroundTask = new BackgroundTask(this.httpClient);
        this.operationalWebhook = new OperationalWebhook(this.httpClient);
        this.messagePoller = new MessagePoller(this.httpClient);
        this.inbound = new Inbound(this.httpClient);
        this.connector = new Connector(this.httpClient);
        this.integration = new Integration(this.httpClient);
        this.stream = new Stream(this.httpClient);
    }

    public Integration integration() { return integration; }
    public Stream stream() { return stream; }
    public Authentication authentication() { return authentication; }
    public Endpoint endpoint() { return endpoint; }
    public EventType eventType() { return eventType; }
    public Health health() { return health; }
    public Message message() { return message; }
    public MessageAttempt messageAttempt() { return messageAttempt; }
    public Statistics statistics() { return statistics; }
    public Environment environment() { return environment; }
    public BackgroundTask backgroundTask() { return backgroundTask; }
    public OperationalWebhook operationalWebhook() { return operationalWebhook; }
    public MessagePoller messagePoller() { return messagePoller; }
    public Inbound inbound() { return inbound; }
    public Connector connector() { return connector; }
}
