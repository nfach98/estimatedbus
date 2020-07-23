package com.example.eta.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.eta.api.model.User;

public class UserPref {

    static final String KEY_PREF = "user_log";

    public static SharedPreferences getUserPref(Context context){
        return context.getSharedPreferences(KEY_PREF, Context.MODE_PRIVATE);
    }

    public static void insertUser(Context context, User user){
        SharedPreferences.Editor editor = getUserPref(context).edit();
        editor.putString("nama", user.getNama());
        editor.putString("no_telp", user.getNoTelp());
        editor.putString("email", user.getEmail());
        editor.putString("photo", user.getPhoto());
        editor.putString("jenis", user.getJenis());
        editor.apply();
    }

    public static User getUser(Context context){
        SharedPreferences pref = getUserPref(context);
        String nama = pref.getString("nama", "");
        String noTelp = pref.getString("no_telp", "");
        String email = pref.getString("email", "");
        String photo = pref.getString("photo", "");
        String jenis = pref.getString("jenis", "");
        return new User(nama, email, noTelp, photo, jenis);
    }

    public static void deleteUser(Context context){
        SharedPreferences.Editor editor = getUserPref(context).edit();
        editor.clear().apply();
    }

    public static void updateField(Context context, String field, String value){
        SharedPreferences.Editor editor = getUserPref(context).edit();
        editor.putString(field, value).apply();
    }

    public static String getField(Context context, String field){
        SharedPreferences pref = getUserPref(context);
        return pref.getString(field, "");
    }

    public static void deleteField(Context context, String field){
        SharedPreferences.Editor editor = getUserPref(context).edit();
        editor.remove(field).apply();
    }
}
