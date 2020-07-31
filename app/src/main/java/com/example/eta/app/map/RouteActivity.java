package com.example.eta.app.map;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.eta.R;
import com.example.eta.api.ApiClient;
import com.example.eta.api.ApiService;
import com.example.eta.api.model.Halte;
import com.example.eta.util.JSONToList;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.RouteListener;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class RouteActivity extends AppCompatActivity implements OnNavigationReadyCallback,
        NavigationListener, RouteListener, ProgressChangeListener {

    private ApiService service;
    private NavigationView navigationView;
    private Location lastKnownLocation;

    private List<Point> points = new ArrayList<>();
    private List<Halte> haltes = new ArrayList<>();
    private int curHalte = 0;

    private boolean arrive = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        haltes = new JSONToList(this, "halte").getListHalte();
        for(Halte halte: haltes) points.add(Point.fromLngLat(halte.getLongitude(), halte.getLatitude()));

        Mapbox.getInstance(this, getString(R.string.map_token));
        setContentView(R.layout.activity_navigation);
        service = ApiClient.getRetrofitInstance().create(ApiService.class);
        initView(savedInstanceState);
    }

    private void initView(Bundle savedInstanceState){
        Objects.requireNonNull(getSupportActionBar()).hide();
        navigationView = findViewById(R.id.navigationView);
        navigationView.onCreate(savedInstanceState);
        navigationView.initialize(this);
    }

    @Override
    public void onNavigationReady(boolean isRunning) {
        fetchRoute(points.remove(0), points.remove(0));
    }

    @Override
    public void onCancelNavigation() {
        finish();
    }

    @Override
    public void onNavigationFinished() {

    }

    @Override
    public void onNavigationRunning() {

    }

    @Override
    public boolean allowRerouteFrom(Point offRoutePoint) {
        return true;
    }

    @Override
    public void onOffRoute(Point offRoutePoint) {

    }

    @Override
    public void onRerouteAlong(DirectionsRoute directionsRoute) {

    }

    @Override
    public void onFailedReroute(String errorMessage) {

    }

    @Override
    public void onArrival() {
        if (!points.isEmpty() && !arrive) {
            arrive = true;
            Log.d("halte", String.valueOf(arrive));
            fetchRoute(getLastKnownLocation(), points.remove(0));
            curHalte++;
            Toast.makeText(this, haltes.get(curHalte).getNama(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onProgressChange(Location location, RouteProgress routeProgress) {
        arrive = false;
        Log.d("halte", String.valueOf(arrive));
        lastKnownLocation = location;
        Call<Integer> call = service.updateBusInfo(
                location.getLatitude(), location.getLongitude(),
                routeProgress.currentLegProgress().currentStep().name(),
                convertSpeed((int) (location.getSpeed() * 3.6)),
                convertTime(routeProgress.currentLegProgress().durationRemaining()),
                "Bus menuju");

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NotNull Call<Integer> call, @NotNull Response<Integer> response) {
                if(response.body() != null) {
                    if(response.body() != 1) {
                        Timber.d("Gak iso");
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<Integer> call, @NotNull Throwable t) {
                Timber.d("Gagal");
            }
        });
    }

    private void startNavigation(DirectionsRoute directionsRoute) {
        NavigationViewOptions navigationViewOptions = setupOptions(directionsRoute);
        navigationView.startNavigation(navigationViewOptions);
    }

    private void fetchRoute(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(getString(R.string.map_token))
                .origin(origin)
                .destination(destination)
                .alternatives(true)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<DirectionsResponse> call, @NotNull Response<DirectionsResponse> response) {
                        DirectionsResponse directionsResponse = response.body();
                        if (directionsResponse != null && !directionsResponse.routes().isEmpty()) {
                            startNavigation(directionsResponse.routes().get(0));
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<DirectionsResponse> call, @NotNull Throwable t) {
                        Timber.e(t);
                    }
                });
    }

    private NavigationViewOptions setupOptions(DirectionsRoute directionsRoute) {
        NavigationViewOptions.Builder options = NavigationViewOptions.builder();
        options.directionsRoute(directionsRoute)
                .navigationListener(this)
                .progressChangeListener(this)
                .routeListener(this)
                .shouldSimulateRoute(true);
        return options.build();
    }

    private Point getLastKnownLocation() {
        return Point.fromLngLat(lastKnownLocation.getLongitude(), lastKnownLocation.getLatitude());
    }

    private String convertSpeed(Integer speed){
        String result;
        if(speed < 10) result = "0" + speed;
        else result = speed.toString();
        return result + " km/jam";
    }

    public String convertTime(double time){
        String conv = "";
        if(time < 60) {
            if(time < 10) conv = "0" + (int) time + " detik";
            else conv = (int) time + " detik";
        }
        else if(time < 3600){
            int min = (int) (time / 60);
            if(min < 10) conv = "0" + min + " menit";
            else conv =  min + " menit";
        }
        else {
            int hour = (int) (time / 3600);
            if(hour < 10) conv = "0" + hour + " jam";
            else conv = hour  + " jam";
        }
        return conv;
    }

    @Override
    public void onStart() {
        super.onStart();
        navigationView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        navigationView.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        navigationView.onLowMemory();
    }

    @Override
    public void onBackPressed() {
        if (!navigationView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        navigationView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NotNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        navigationView.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        navigationView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        navigationView.onStop();
    }

    @Override
    protected void onDestroy() {
        Call<Integer> call = service.updateBusInfo(
                0, 0,
                "Bus tidak beroperasi",
                "00 km/jam",
                "00 detik",
                "Bus tidak beroperasi");

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NotNull Call<Integer> call, @NotNull Response<Integer> response) {
                if(Objects.requireNonNull(response.body()) != 1) {
                    Timber.d("Gak iso");
                }
            }

            @Override
            public void onFailure(@NotNull Call<Integer> call, @NotNull Throwable t) {
                Timber.d("Gagal");
            }
        });

        super.onDestroy();
        navigationView.onDestroy();
    }
}
