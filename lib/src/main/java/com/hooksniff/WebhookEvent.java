package com.hooksniff;

import java.util.Map;

/**
 * Represents a parsed webhook event from HookSniff.
 *
 * <p>Contains the event type, payload data, and timestamp from a verified webhook delivery.</p>
 *
 * <pre>{@code
 * Webhook wh = new Webhook("whsec_...");
 * WebhookEvent event = wh.verifyAndParse(payload, headers);
 * System.out.println(event.getEvent());     // "endpoint.created"
 * System.out.println(event.getData());      // {endpointId=ep_123, ...}
 * System.out.println(event.getTimestamp()); // "2026-05-19T02:33:00Z"
 * }</pre>
 */
public class WebhookEvent {
    private final String event;
    private final Map<String, Object> data;
    private final String timestamp;

    public WebhookEvent(String event, Map<String, Object> data, String timestamp) {
        this.event = event;
        this.data = data;
        this.timestamp = timestamp;
    }

    /**
     * Get the event type name (e.g., "endpoint.created").
     * @return event type string
     */
    public String getEvent() {
        return event;
    }

    /**
     * Get the event payload data.
     * @return data map
     */
    public Map<String, Object> getData() {
        return data;
    }

    /**
     * Get the ISO 8601 timestamp string.
     * @return timestamp string
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Get a value from the data map by key.
     * @param key the data key
     * @return the value, or null if not found
     */
    public Object get(String key) {
        return data != null ? data.get(key) : null;
    }

    @Override
    public String toString() {
        return "WebhookEvent{event='" + event + "', timestamp='" + timestamp + "'}";
    }
}
