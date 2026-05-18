package com.hooksniff;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public class EndpointDisabledData {
    @JsonProperty("appId") private String appId;
    @JsonProperty("endpointId") private String endpointId;
    @JsonProperty("appUid") private String appUid;
    @JsonProperty("failSince") private String failSince;
    @JsonProperty("trigger") private String trigger;
    public EndpointDisabledData() {}
    public String getAppId() { return appId; }
    public void setAppId(String v) { this.appId = v; }
    public String getEndpointId() { return endpointId; }
    public void setEndpointId(String v) { this.endpointId = v; }
    public String getAppUid() { return appUid; }
    public void setAppUid(String v) { this.appUid = v; }
    public String getFailSince() { return failSince; }
    public void setFailSince(String v) { this.failSince = v; }
    public String getTrigger() { return trigger; }
    public void setTrigger(String v) { this.trigger = v; }
}
