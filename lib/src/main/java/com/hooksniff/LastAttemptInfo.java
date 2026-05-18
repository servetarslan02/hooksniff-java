package com.hooksniff;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public class LastAttemptInfo {
    @JsonProperty("id") private String id;
    @JsonProperty("timestamp") private String timestamp;
    @JsonProperty("responseStatusCode") private int responseStatusCode;
    public LastAttemptInfo() {}
    public String getId() { return id; }
    public void setId(String v) { this.id = v; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String v) { this.timestamp = v; }
    public int getResponseStatusCode() { return responseStatusCode; }
    public void setResponseStatusCode(int v) { this.responseStatusCode = v; }
}
