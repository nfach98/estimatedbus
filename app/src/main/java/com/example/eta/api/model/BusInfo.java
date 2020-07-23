package com.example.eta.api.model;

import com.google.gson.annotations.SerializedName;

public class BusInfo {
    @SerializedName("lat")
    private double lat;

    @SerializedName("lng")
    private double lng;

    @SerializedName("pos")
    private String pos;

    @SerializedName("speed")
    private float speed;

    @SerializedName("time")
    private float time;

    @SerializedName("message")
    private String message;

    public BusInfo(double lat, double lng, String pos, float speed, float time, String message) {
        this.lat = lat;
        this.lng = lng;
        this.pos = pos;
        this.speed = speed;
        this.time = time;
        this.message = message;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getPos() {
        return pos;
    }

    public float getSpeed() {
        return speed;
    }

    public float getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }
}
