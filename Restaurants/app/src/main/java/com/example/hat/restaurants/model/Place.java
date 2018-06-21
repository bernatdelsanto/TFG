package com.example.hat.restaurants.model;

import com.google.android.gms.maps.model.LatLng;

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