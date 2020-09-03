package com.example.eta.api.model;

/** kelas untuk menampung data halte
 *  (daftar halte ada pada folder assets/halte.json)
 */

public class Halte {
    private String nama;
    private double latitude;
    private double longitude;

    public Halte(String nama, double latitude, double longitude) {
        this.nama = nama;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }
}
