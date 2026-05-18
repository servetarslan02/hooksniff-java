package com.hooksniff;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageAttemptRecoveredData {
    @JsonProperty("appId") private String appId;
    @JsonProperty("msgId") private String msgId;
    @JsonProperty("attempt") private AttemptInfo attempt;
    @JsonProperty("appUid") private String appUid;
    public MessageAttemptRecoveredData() {}
    public String getAppId() { return appId; }
    public void setAppId(String v) { this.appId = v; }
    public String getMsgId() { return msgId; }
    public void setMsgId(String v) { this.msgId = v; }
    public AttemptInfo getAttempt() { return attempt; }
    public void setAttempt(AttemptInfo v) { this.attempt = v; }
    public String getAppUid() { return appUid; }
    public void setAppUid(String v) { this.appUid = v; }
}
