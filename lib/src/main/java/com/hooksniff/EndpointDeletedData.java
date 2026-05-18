package com.hooksniff;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public class EndpointDeletedData {
    @JsonProperty("appId") private String appId;
    @JsonProperty("endpointId") private String endpointId;
    @JsonProperty("appUid") private String appUid;
    public EndpointDeletedData() {}
    public String getAppId() { return appId; }
    public void setAppId(String v) { this.appId = v; }
    public String getEndpointId() { return endpointId; }
    public void setEndpointId(String v) { this.endpointId = v; }
    public String getAppUid() { return appUid; }
    public void setAppUid(String v) { this.appUid = v; }
}
