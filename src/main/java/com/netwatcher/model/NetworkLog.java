package com.netwatcher.model;

public class NetworkLog {
    private int id, deviceId, severity;
    private String date, issueType, resolution, deviceName;
    public NetworkLog() { }
    public NetworkLog(int id, int deviceId, String date, String issueType, String resolution, int severity, String deviceName) {
        this.id=id; this.deviceId=deviceId; this.date=date; this.issueType=issueType; this.resolution=resolution; this.severity=severity; this.deviceName=deviceName;
    }
    public int getId() { return id; } public void setId(int v) { id=v; }
    public int getDeviceId() { return deviceId; } public void setDeviceId(int v) { deviceId=v; }
    public String getDate() { return date; } public void setDate(String v) { date=v; }
    public String getIssueType() { return issueType; } public void setIssueType(String v) { issueType=v; }
    public String getResolution() { return resolution; } public void setResolution(String v) { resolution=v; }
    public int getSeverity() { return severity; } public void setSeverity(int v) { severity=v; }
    public String getDeviceName() { return deviceName; } public void setDeviceName(String v) { deviceName=v; }
}
