package com.example.locationsharing.models;

public class LocationData {
    private String userId;
    private double latitude;
    private double longitude;
    private long timestamp;
    private boolean isSharing;

    public LocationData() {}

    public LocationData(String userId, double latitude, double longitude, long timestamp, boolean isSharing) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.isSharing = isSharing;
    }

    public String getUserId() { return userId; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public long getTimestamp() { return timestamp; }
    public boolean isSharing() { return isSharing; }
}
