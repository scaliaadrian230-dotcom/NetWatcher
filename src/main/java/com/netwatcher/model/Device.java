package com.netwatcher.model;

public class Device {
    private int id;
    private String name, ipAddress, macAddress, type, notes;
    public Device() { }
    public Device(int id, String name, String ipAddress, String macAddress, String type, String notes) {
        this.id=id; this.name=name; this.ipAddress=ipAddress; this.macAddress=macAddress; this.type=type; this.notes=notes;
    }
    public int getId() { return id; } public void setId(int id) { this.id=id; }
    public String getName() { return name; } public void setName(String v) { name=v; }
    public String getIpAddress() { return ipAddress; } public void setIpAddress(String v) { ipAddress=v; }
    public String getMacAddress() { return macAddress; } public void setMacAddress(String v) { macAddress=v; }
    public String getType() { return type; } public void setType(String v) { type=v; }
    public String getNotes() { return notes; } public void setNotes(String v) { notes=v; }
    @Override public String toString() { return name + " (" + ipAddress + ")"; }
}
