package com.example.eta.util;

import android.content.Context;

import com.example.eta.api.model.Halte;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JSONToList {

    private Context context;
    private String filename;
    private String json = null;

    public JSONToList(Context context, String filename){
        this.context = context;
        this.filename = filename;

        loadJSONFromAsset();
    }

    public ArrayList<Halte> getListHalte(){
        ArrayList<Halte> listHalte = new ArrayList<>();

        try {
            JSONObject obj = new JSONObject(json);
            JSONArray array = obj.getJSONArray("haltes");

            for (int i = 0; i < array.length(); i++) {
                JSONObject halte = array.getJSONObject(i);
                String halteName = halte.getString("name");
                double halteLat = halte.getDouble("lat");
                double halteLng = halte.getDouble("lng");

                listHalte.add(new Halte(halteName, halteLat, halteLng));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return listHalte;
    }


    public void loadJSONFromAsset() {
        try {
            InputStream is = context.getAssets().open(filename + ".json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
