package com.example.eta.util;

public class TimeConverter {

    private double time;

    public TimeConverter(double time) {
        this.time = time;
    }

    public String getConverted(){
        String conv;
        if(time < 60) conv = (int) time + " detik"; //detik
        else if(time < 3600) conv = (int) (time / 60)  + " menit"; //menit
        else conv = (int) (time / 3600)  + " jam"; //jam
        return conv;
    }
}
