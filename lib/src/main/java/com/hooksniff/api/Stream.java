package com.hooksniff.api;

import com.hooksniff.HookSniffHttpClient;
import com.hooksniff.Utils;
import com.hooksniff.exceptions.ApiException;
import com.hooksniff.models.*;

import java.io.IOException;
import java.util.List;

public class Stream {
    private final HookSniffHttpClient client;

    public Stream(HookSniffHttpClient client) { this.client = client; }

    public List<StreamChannelOut> listChannels() throws IOException, ApiException {
        return client.executeRequest("GET", client.newUrlBuilder().encodedPath("/v1/stream/channels").build(), null, null, Utils.getListType(StreamChannelOut.class));
    }

    public StreamChannelDetailOut getChannel(String id) throws IOException, ApiException {
        return client.executeRequest("GET", client.newUrlBuilder().encodedPath("/v1/stream/channels/" + id).build(), null, null, StreamChannelDetailOut.class);
    }

    public StreamChannelOut createChannel(StreamChannelIn body) throws IOException, ApiException {
        return client.executeRequest("POST", client.newUrlBuilder().encodedPath("/v1/stream/channels").build(), null, body, StreamChannelOut.class);
    }

    public StreamChannelOut updateChannel(String id, StreamChannelUpdate body) throws IOException, ApiException {
        return client.executeRequest("PUT", client.newUrlBuilder().encodedPath("/v1/stream/channels/" + id).build(), null, body, StreamChannelOut.class);
    }

    public void deleteChannel(String id) throws IOException, ApiException {
        client.executeRequest("DELETE", client.newUrlBuilder().encodedPath("/v1/stream/channels/" + id).build(), null, null, null);
    }

    public List<StreamMessageOut> listMessages(String channelId) throws IOException, ApiException {
        return client.executeRequest("GET", client.newUrlBuilder().encodedPath("/v1/stream/channels/" + channelId + "/messages").build(), null, null, Utils.getListType(StreamMessageOut.class));
    }

    public List<StreamSubscriptionOut> listSubscriptions() throws IOException, ApiException {
        return client.executeRequest("GET", client.newUrlBuilder().encodedPath("/v1/stream/subscriptions").build(), null, null, Utils.getListType(StreamSubscriptionOut.class));
    }

    public void deleteSubscription(String id) throws IOException, ApiException {
        client.executeRequest("DELETE", client.newUrlBuilder().encodedPath("/v1/stream/subscriptions/" + id).build(), null, null, null);
    }

    public StreamMessageOut publishEvent(PublishEventIn body) throws IOException, ApiException {
        return client.executeRequest("POST", client.newUrlBuilder().encodedPath("/v1/stream/events").build(), null, body, StreamMessageOut.class);
    }

    /**
     * Subscribe to real-time events via SSE on a channel.
     * Calls the callback for each event received.
     *
     * @param channelId Channel ID to subscribe to
     * @param onEvent Callback for each event
     * @throws IOException on connection error
     * @throws ApiException on API error
     */
    public void subscribe(String channelId, java.util.function.Consumer<String> onEvent) throws IOException, ApiException {
        String url = client.getBaseUrl() + "/v1/stream/channels/" + channelId + "/subscribe";
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + client.getApiKey());
        conn.setRequestProperty("Accept", "text/event-stream");
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(0);

        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()))) {
            String eventType = "";
            StringBuilder data = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("event:")) {
                    eventType = line.substring(6).trim();
                } else if (line.startsWith("data:")) {
                    data.append(line.substring(5).trim());
                } else if (line.isEmpty() && data.length() > 0) {
                    onEvent.accept(data.toString());
                    eventType = "";
                    data.setLength(0);
                }
            }
        } finally {
            conn.disconnect();
        }
    }
}
