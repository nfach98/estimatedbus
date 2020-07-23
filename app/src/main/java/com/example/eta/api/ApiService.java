package com.example.eta.api;

import com.example.eta.api.model.BusInfo;
import com.example.eta.api.model.User;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("/login")
    Call<List<User>> userLogin(@Query("email") String email, @Query("password") String password);

    @GET("/register")
    Call<List<User>> userRegister(@Query("nama") String nama, @Query("no_telp") String noTelp,
                                  @Query("email") String email, @Query("password") String password);

    @GET("/update")
    Call<Integer> userUpdate(@Query("nama") String nama, @Query("no_telp") String noTelp,
                             @Query("photo") String photo, @Query("email_new") String newEmail,
                             @Query("password") String password, @Query("email") String email);

    @GET("/check")
    Call<Integer> checkEmail(@Query("email") String email);

    @GET("/delete")
    Call<Integer> userDelete(@Query("email") String email);

    @GET("/updatebus")
    Call<Integer> updateBusInfo(@Query("lat") double lat, @Query("lng") double lng,
                                @Query("pos") String pos, @Query("speed") float speed,
                                @Query("time") float time, @Query("message") String message);

    @GET("/bus")
    Call<List<BusInfo>> getBusInfo();
}
