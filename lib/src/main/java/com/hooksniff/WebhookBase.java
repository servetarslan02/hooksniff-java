package com.hooksniff;

import com.hooksniff.exceptions.WebhookSigningException;
import com.hooksniff.exceptions.WebhookVerificationException;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Base class containing all shared webhook verification logic.
 * Used by both the Java 8 and Java 11 implementations.
 * Package-private to prevent extension outside this package.
 */
abstract class WebhookBase {
    public static final String SECRET_PREFIX = "whsec_";
    public static final String HOOKSNIFF_MSG_ID_KEY = "hooksniff-id";
    public static final String HOOKSNIFF_MSG_SIGNATURE_KEY = "hooksniff-signature";
    public static final String HOOKSNIFF_MSG_TIMESTAMP_KEY = "hooksniff-timestamp";
    public static final String UNBRANDED_MSG_ID_KEY = "webhook-id";
    public static final String UNBRANDED_MSG_SIGNATURE_KEY = "webhook-signature";
    public static final String UNBRANDED_MSG_TIMESTAMP_KEY = "webhook-timestamp";
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final int TOLERANCE_IN_SECONDS = 5 * 60; // 5 minutes
    private static final long SECOND_IN_MS = 1000L;

    protected final byte[] key;

    protected WebhookBase(final String secret) {
        String sec = secret;
        if (sec.startsWith(WebhookBase.SECRET_PREFIX)) {
            sec = sec.substring(WebhookBase.SECRET_PREFIX.length());
        }
        this.key = Base64.getDecoder().decode(sec);
    }

    protected WebhookBase(final byte[] secret) {
        this.key = secret;
    }

    public void verify(final String payload, final Map<String, List<String>> headers)
            throws WebhookVerificationException {
        String msgId = firstHeader(headers, HOOKSNIFF_MSG_ID_KEY);
        String msgSignature = firstHeader(headers, HOOKSNIFF_MSG_SIGNATURE_KEY);
        String msgTimestamp = firstHeader(headers, HOOKSNIFF_MSG_TIMESTAMP_KEY);

        if (msgId == null || msgSignature == null || msgTimestamp == null) {
            // fallback to unbranded
            msgId = firstHeader(headers, UNBRANDED_MSG_ID_KEY);
            msgSignature = firstHeader(headers, UNBRANDED_MSG_SIGNATURE_KEY);
            msgTimestamp = firstHeader(headers, UNBRANDED_MSG_TIMESTAMP_KEY);
            if (msgId == null || msgSignature == null || msgTimestamp == null) {
                throw new WebhookVerificationException("Missing required headers");
            }
        }

        long timestamp = WebhookBase.verifyTimestamp(msgTimestamp);

        String expectedSignature;
        try {
            expectedSignature = this.sign(msgId, timestamp, payload).split(",")[1];
        } catch (WebhookSigningException e) {
            throw new WebhookVerificationException("Failed to generate expected signature");
        }

        String[] msgSignatures = msgSignature.split(" ");
        for (String versionedSignature : msgSignatures) {
            String[] sigParts = versionedSignature.split(",");
            if (sigParts.length < 2) {
                continue;
            }
            String version = sigParts[0];
            if (!version.equals("v1")) {
                continue;
            }
            String signature = sigParts[1];
            if (MessageDigest.isEqual(signature.getBytes(), expectedSignature.getBytes())) {
                return;
            }
        }
        throw new WebhookVerificationException("No matching signature found");
    }

    /**
     * Verify the webhook signature and parse the payload into a WebhookEvent.
     *
     * @param payload Raw request body
     * @param headers Request headers containing hooksniff-id, hooksniff-timestamp, hooksniff-signature
     * @return Parsed WebhookEvent with typed fields
     * @throws WebhookVerificationException if signature is invalid or timestamp is outside tolerance
     */
    @SuppressWarnings("unchecked")
    public WebhookEvent verifyAndParse(final String payload, final Map<String, List<String>> headers)
            throws WebhookVerificationException {
        verify(payload, headers);

        try {
            // Simple JSON parsing without external dependency
            Map<String, Object> parsed = parseJson(payload);
            String event = parsed.containsKey("event") ? String.valueOf(parsed.get("event")) :
                           parsed.containsKey("eventType") ? String.valueOf(parsed.get("eventType")) : "";
            Map<String, Object> data = parsed.containsKey("data") && parsed.get("data") instanceof Map
                ? (Map<String, Object>) parsed.get("data") : new HashMap<>();
            String timestamp = parsed.containsKey("timestamp") ? String.valueOf(parsed.get("timestamp")) : "";

            return new WebhookEvent(event, data, timestamp);
        } catch (Exception e) {
            throw new WebhookVerificationException("Failed to parse webhook payload: " + e.getMessage());
        }
    }

    /**
     * Verify the webhook signature and parse the payload, using Map headers.
     */
    @SuppressWarnings("unchecked")
    public WebhookEvent verifyAndParse(final String payload, final Map<String, String> headers, boolean simpleHeaders)
            throws WebhookVerificationException {
        // Convert simple map to multi-value map
        Map<String, List<String>> multiHeaders = new HashMap<>();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            multiHeaders.put(entry.getKey(), List.of(entry.getValue()));
        }
        return verifyAndParse(payload, multiHeaders);
    }

    /**
     * Minimal JSON parser for webhook payloads.
     * Parses a JSON object string into a Map.
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> parseJson(String json) {
        // Use Java's built-in Nashorn or simple parsing
        // For production, this should use a proper JSON library
        // But since we're in a library without Jackson/Gson dependency,
        // we'll do a basic parse of the expected structure
        Map<String, Object> result = new HashMap<>();
        json = json.trim();

        if (!json.startsWith("{") || !json.endsWith("}")) {
            return result;
        }

        // Extract "event" field
        String event = extractJsonString(json, "event");
        if (event != null) result.put("event", event);

        // Extract "eventType" field
        String eventType = extractJsonString(json, "eventType");
        if (eventType != null) result.put("eventType", eventType);

        // Extract "timestamp" field
        String timestamp = extractJsonString(json, "timestamp");
        if (timestamp != null) result.put("timestamp", timestamp);

        // Extract "data" field as raw JSON (we'll store it as a map placeholder)
        int dataIndex = json.indexOf("\"data\"");
        if (dataIndex >= 0) {
            int colonIndex = json.indexOf(":", dataIndex + 6);
            if (colonIndex >= 0) {
                int braceStart = json.indexOf("{", colonIndex);
                if (braceStart >= 0) {
                    int depth = 0;
                    int end = braceStart;
                    for (int i = braceStart; i < json.length(); i++) {
                        if (json.charAt(i) == '{') depth++;
                        if (json.charAt(i) == '}') depth--;
                        if (depth == 0) { end = i + 1; break; }
                    }
                    String dataJson = json.substring(braceStart, end);
                    result.put("data", parseNestedJson(dataJson));
                }
            }
        }

        return result;
    }

    private static String extractJsonString(String json, String key) {
        String search = "\"" + key + "\"";
        int index = json.indexOf(search);
        if (index < 0) return null;

        int colonIndex = json.indexOf(":", index + search.length());
        if (colonIndex < 0) return null;

        int start = json.indexOf("\"", colonIndex + 1);
        if (start < 0) return null;
        start++;

        int end = start;
        while (end < json.length()) {
            if (json.charAt(end) == '\\') { end += 2; continue; }
            if (json.charAt(end) == '"') break;
            end++;
        }

        return json.substring(start, end);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> parseNestedJson(String json) {
        Map<String, Object> result = new HashMap<>();
        json = json.trim();
        if (!json.startsWith("{")) return result;

        // Simple key-value extraction for nested objects
        int i = 1; // skip opening {
        while (i < json.length() - 1) {
            // Skip whitespace
            while (i < json.length() && Character.isWhitespace(json.charAt(i))) i++;
            if (i >= json.length() - 1 || json.charAt(i) == '}') break;

            // Read key
            if (json.charAt(i) != '"') { i++; continue; }
            i++;
            int keyStart = i;
            while (i < json.length() && json.charAt(i) != '"') {
                if (json.charAt(i) == '\\') i++;
                i++;
            }
            String key = json.substring(keyStart, i);
            i++; // skip closing "

            // Skip colon
            while (i < json.length() && (json.charAt(i) == ':' || Character.isWhitespace(json.charAt(i)))) i++;

            // Read value
            if (i >= json.length()) break;
            char c = json.charAt(i);
            if (c == '"') {
                // String value
                i++;
                int valStart = i;
                while (i < json.length()) {
                    if (json.charAt(i) == '\\') { i += 2; continue; }
                    if (json.charAt(i) == '"') break;
                    i++;
                }
                result.put(key, json.substring(valStart, i));
                i++;
            } else if (c == '{') {
                // Nested object — store as raw string for simplicity
                int depth = 0;
                int start = i;
                for (; i < json.length(); i++) {
                    if (json.charAt(i) == '{') depth++;
                    if (json.charAt(i) == '}') depth--;
                    if (depth == 0) { i++; break; }
                }
                result.put(key, parseNestedJson(json.substring(start, i)));
            } else if (c == '[') {
                // Array — skip for now
                int depth = 0;
                for (; i < json.length(); i++) {
                    if (json.charAt(i) == '[') depth++;
                    if (json.charAt(i) == ']') depth--;
                    if (depth == 0) { i++; break; }
                }
            } else if (c == 't' || c == 'f') {
                // Boolean
                result.put(key, json.startsWith("true", i));
                i += json.startsWith("true", i) ? 4 : 5;
            } else if (c == 'n') {
                // null
                result.put(key, null);
                i += 4;
            } else {
                // Number
                int start = i;
                while (i < json.length() && ",} ]".indexOf(json.charAt(i)) < 0) i++;
                String numStr = json.substring(start, i).trim();
                try {
                    if (numStr.contains(".")) {
                        result.put(key, Double.parseDouble(numStr));
                    } else {
                        result.put(key, Long.parseLong(numStr));
                    }
                } catch (NumberFormatException e) {
                    result.put(key, numStr);
                }
            }

            // Skip comma
            while (i < json.length() && (json.charAt(i) == ',' || Character.isWhitespace(json.charAt(i)))) i++;
        }

        return result;
    }

    private static String firstHeader(
            final Map<String, List<String>> headers, final String name) {
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(name)) {
                List<String> values = entry.getValue();
                if (values != null && !values.isEmpty()) {
                    return values.get(0);
                }
            }
        }
        return null;
    }

    public String sign(final String msgId, final long timestamp, final String payload)
            throws WebhookSigningException {
        try {
            String toSign = String.format("%s.%s.%s", msgId, timestamp, payload);
            Mac sha512Hmac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec keySpec = new SecretKeySpec(this.key, HMAC_SHA256);
            sha512Hmac.init(keySpec);
            byte[] macData = sha512Hmac.doFinal(toSign.getBytes(StandardCharsets.UTF_8));
            String signature = Base64.getEncoder().encodeToString(macData);
            return String.format("v1,%s", signature);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new WebhookSigningException(e.getMessage());
        }
    }

    private static long verifyTimestamp(final String timestampHeader)
            throws WebhookVerificationException {
        long now = System.currentTimeMillis() / WebhookBase.SECOND_IN_MS;

        long timestamp;
        try {
            timestamp = Long.parseLong(timestampHeader);
        } catch (NumberFormatException e) {
            throw new WebhookVerificationException("Invalid Signature Headers");
        }

        if (timestamp < (now - TOLERANCE_IN_SECONDS)) {
            throw new WebhookVerificationException("Message timestamp too old");
        }
        if (timestamp > (now + TOLERANCE_IN_SECONDS)) {
            throw new WebhookVerificationException("Message timestamp too new");
        }
        return timestamp;
    }
}
