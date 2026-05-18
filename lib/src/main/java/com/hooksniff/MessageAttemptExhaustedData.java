package com.hooksniff;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageAttemptExhaustedData {
    @JsonProperty("appId") private String appId;
    @JsonProperty("msgId") private String msgId;
    @JsonProperty("lastAttempt") private LastAttemptInfo lastAttempt;
    @JsonProperty("appUid") private String appUid;
    public MessageAttemptExhaustedData() {}
    public String getAppId() { return appId; }
    public void setAppId(String v) { this.appId = v; }
    public String getMsgId() { return msgId; }
    public void setMsgId(String v) { this.msgId = v; }
    public LastAttemptInfo getLastAttempt() { return lastAttempt; }
    public void setLastAttempt(LastAttemptInfo v) { this.lastAttempt = v; }
    public String getAppUid() { return appUid; }
    public void setAppUid(String v) { this.appUid = v; }
}
