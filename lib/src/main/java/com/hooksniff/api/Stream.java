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
        return client.executeRequest("GET", client.newUrlBuilder().encodedPath("/api/v1/stream/channels").build(), null, null, Utils.getListType(StreamChannelOut.class));
    }

    public StreamChannelDetailOut getChannel(String id) throws IOException, ApiException {
        return client.executeRequest("GET", client.newUrlBuilder().encodedPath("/api/v1/stream/channels/" + id).build(), null, null, StreamChannelDetailOut.class);
    }

    public StreamChannelOut createChannel(StreamChannelIn body) throws IOException, ApiException {
        return client.executeRequest("POST", client.newUrlBuilder().encodedPath("/api/v1/stream/channels").build(), null, body, StreamChannelOut.class);
    }

    public StreamChannelOut updateChannel(String id, StreamChannelUpdate body) throws IOException, ApiException {
        return client.executeRequest("PUT", client.newUrlBuilder().encodedPath("/api/v1/stream/channels/" + id).build(), null, body, StreamChannelOut.class);
    }

    public void deleteChannel(String id) throws IOException, ApiException {
        client.executeRequest("DELETE", client.newUrlBuilder().encodedPath("/api/v1/stream/channels/" + id).build(), null, null, null);
    }

    public List<StreamMessageOut> listMessages(String channelId) throws IOException, ApiException {
        return client.executeRequest("GET", client.newUrlBuilder().encodedPath("/api/v1/stream/channels/" + channelId + "/messages").build(), null, null, Utils.getListType(StreamMessageOut.class));
    }

    public List<StreamSubscriptionOut> listSubscriptions() throws IOException, ApiException {
        return client.executeRequest("GET", client.newUrlBuilder().encodedPath("/api/v1/stream/subscriptions").build(), null, null, Utils.getListType(StreamSubscriptionOut.class));
    }

    public void deleteSubscription(String id) throws IOException, ApiException {
        client.executeRequest("DELETE", client.newUrlBuilder().encodedPath("/api/v1/stream/subscriptions/" + id).build(), null, null, null);
    }

    public StreamMessageOut publishEvent(PublishEventIn body) throws IOException, ApiException {
        return client.executeRequest("POST", client.newUrlBuilder().encodedPath("/api/v1/stream/events").build(), null, body, StreamMessageOut.class);
    }
}
