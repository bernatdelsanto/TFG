package com.ub.bernat.listitin.model;

/*
A place with all information needed for displaying it and relating to Firebase's database
 */
public class Place {
    private String id;
    private String name;
    private float latitude;

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    private float longitude;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Place(String id, String name, float latitude, float longitude) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;

    }

    public void setId(String id) {
        this.id = id;
    }

}