package com.hooksniff;

import com.hooksniff.exceptions.ApiException;
import com.hooksniff.exceptions.WebhookVerificationException;
import com.hooksniff.exceptions.WebhookSigningException;

import org.junit.Test;
import static org.junit.Assert.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HookSniffTest {

    private static final String SECRET = "whsec_dGVzdA==";
    private static final String MSG_ID = "msg_test123";
    private static final long TIMESTAMP = System.currentTimeMillis() / 1000;
    private static final String PAYLOAD = "{\"event\":\"test\"}";

    private String sign(String secret, String msgId, long timestamp, String payload) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(secret.replace("whsec_", ""));
        String toSign = msgId + "." + timestamp + "." + payload;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(decoded, "HmacSHA256"));
        byte[] sig = mac.doFinal(toSign.getBytes(StandardCharsets.UTF_8));
        return "v1," + Base64.getEncoder().encodeToString(sig);
    }

    @Test
    public void testVerifyValidSignature() throws Exception {
        Webhook wh = new Webhook(SECRET);
        String sig = sign(SECRET, MSG_ID, TIMESTAMP, PAYLOAD);
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("webhook-id", Collections.singletonList(MSG_ID));
        headers.put("webhook-timestamp", Collections.singletonList(String.valueOf(TIMESTAMP)));
        headers.put("webhook-signature", Collections.singletonList(sig));

        wh.verify(PAYLOAD, headers); // Should not throw
    }

    @Test(expected = WebhookVerificationException.class)
    public void testRejectInvalidSignature() throws Exception {
        Webhook wh = new Webhook(SECRET);
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("webhook-id", Collections.singletonList(MSG_ID));
        headers.put("webhook-timestamp", Collections.singletonList(String.valueOf(TIMESTAMP)));
        headers.put("webhook-signature", Collections.singletonList("v1,invalid"));

        wh.verify(PAYLOAD, headers);
    }

    @Test(expected = WebhookVerificationException.class)
    public void testRejectOldTimestamp() throws Exception {
        Webhook wh = new Webhook(SECRET);
        long oldTs = System.currentTimeMillis() / 1000 - 600;
        String sig = sign(SECRET, MSG_ID, oldTs, PAYLOAD);
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("webhook-id", Collections.singletonList(MSG_ID));
        headers.put("webhook-timestamp", Collections.singletonList(String.valueOf(oldTs)));
        headers.put("webhook-signature", Collections.singletonList(sig));

        wh.verify(PAYLOAD, headers);
    }

    @Test
    public void testHooksniffBrandedHeaders() throws Exception {
        Webhook wh = new Webhook(SECRET);
        String sig = sign(SECRET, MSG_ID, TIMESTAMP, PAYLOAD);
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("hooksniff-id", Collections.singletonList(MSG_ID));
        headers.put("hooksniff-timestamp", Collections.singletonList(String.valueOf(TIMESTAMP)));
        headers.put("hooksniff-signature", Collections.singletonList(sig));

        wh.verify(PAYLOAD, headers); // Should not throw
    }

    @Test
    public void testWebhookSign() throws WebhookSigningException {
        Webhook wh = new Webhook(SECRET);
        String signature = wh.sign(MSG_ID, TIMESTAMP, PAYLOAD);
        assertNotNull(signature);
        assertTrue(signature.startsWith("v1,"));
    }

    @Test(expected = WebhookVerificationException.class)
    public void testRejectMissingHeaders() throws Exception {
        Webhook wh = new Webhook(SECRET);
        Map<String, List<String>> headers = new HashMap<>();
        wh.verify(PAYLOAD, headers);
    }

    @Test
    public void testHookSniffOptionsDefaults() {
        HookSniffOptions options = new HookSniffOptions();
        assertNull(options.getServerUrl());
        assertFalse(options.isDebug());
        assertNotNull(options.getRetrySchedule());
        assertEquals(3, options.getRetrySchedule().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidBaseUrl() {
        HookSniffOptions opts = new HookSniffOptions();
        opts.setServerUrl("not a valid url");
        new HookSniff("test-token", opts);
    }

    @Test
    public void testVersion() {
        assertNotNull(Version.VERSION);
        assertFalse(Version.VERSION.isEmpty());
    }
}
