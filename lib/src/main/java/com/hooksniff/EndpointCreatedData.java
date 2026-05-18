package com.hooksniff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Typed data payload for endpoint.created events.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EndpointCreatedData {
    @JsonProperty("appId")
    private String appId;
    @JsonProperty("endpointId")
    private String endpointId;
    @JsonProperty("appUid")
    private String appUid;

    public EndpointCreatedData() {}
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getEndpointId() { return endpointId; }
    public void setEndpointId(String endpointId) { this.endpointId = endpointId; }
    public String getAppUid() { return appUid; }
    public void setAppUid(String appUid) { this.appUid = appUid; }
}
