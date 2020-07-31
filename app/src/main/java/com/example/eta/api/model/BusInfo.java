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
    private String speed;

    @SerializedName("time")
    private String time;

    @SerializedName("message")
    private String message;

    public BusInfo(double lat, double lng, String pos, String speed, String time, String message) {
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

    public String getSpeed() {
        return speed;
    }

    public String getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }
}
