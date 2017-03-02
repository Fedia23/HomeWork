package com.pineapple.softgroup.DB.model;

public class LastLocation {
    private int id;
    private double latitude;
    private double longitude;

    public LastLocation() {
    }

    public LastLocation(double latitude, double longitude) {
        setLatitude(latitude);
        setLongitude(longitude);
    }

    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    public int getId() { return id; }
    public void setID(int id) { this.id = id; }
}
