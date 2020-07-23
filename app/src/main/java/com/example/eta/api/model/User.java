package com.example.eta.api.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("nama")
    private String nama;

    @SerializedName("email")
    private String email;

    @SerializedName("no_telp")
    private String noTelp;

    @SerializedName("photo")
    private String photo;

    @SerializedName("jenis")
    private String jenis;

    public User(String nama, String email, String noTelp, String photo, String jenis) {
        this.nama = nama;
        this.email = email;
        this.noTelp = noTelp;
        this.photo = photo;
        this.jenis = jenis;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }
}
