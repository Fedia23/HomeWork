package com.pineapple.softgroup.DB.model;

public class Marker {

    private int id;
    private double latitude;
    private double longitude;
    private String name;
    private String description;

    public Marker () {
    }
    public Marker (double latitude, double longitude) {
        setLatitude(latitude);
        setLongitude(longitude);
    }
    public Marker (String name, String description, double latitude, double longitude) {
        setName(name);
        setDescription(description);
        setLatitude(latitude);
        setLongitude(longitude);
    }

    public void setName (String name) { this.name = name; }
    public void setDescription (String description) { this.description = description; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }


    public int getId() { return id; }
    public void setID(int id) { this.id = id; }



}
