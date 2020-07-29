package com.example.eta.app.map;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.eta.R;
import com.example.eta.api.ApiClient;
import com.example.eta.api.ApiService;
import com.example.eta.api.model.BusInfo;
import com.example.eta.api.model.Halte;
import com.example.eta.util.JSONToList;
import com.example.eta.util.UserPref;
import com.example.eta.util.ViewRefreshHandler;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.match;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAnchor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconRotationAlignment;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {

    private MapboxMap mapboxMap;
    private MapView mapView;
    private Button btnStartNav;

    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;

    private static final String SOURCE_ID = "SOURCE_BLUE";
    private static final String ICON_ID_BLUE = "ICON_BLUE";
    private static final String LAYER_ID = "LAYER_BLUE";
    private static final String ICON_ID_BUS = "ICON_BUS";
    private static final String ICON_PROPERTY = "ICON_PROPERTY";

    private ValueAnimator animator;
    private ApiService apiService;
    private LatLng busPoint = new LatLng(-7.279251, 112.790206);
    private List<Feature> listFeature;
    private GeoJsonSource geoJsonSource;

    private MapActivityLocationCallback callback = new MapActivityLocationCallback(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.map_token));
        setContentView(R.layout.activity_map);
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        initView(savedInstanceState);
        ViewRefreshHandler viewRefreshHandler = new ViewRefreshHandler();
        viewRefreshHandler.executePerSecond(new FetchBusRunnable(null));
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        listFeature = new ArrayList<>();
        ArrayList<Halte> list = new JSONToList(this, "halte").getListHalte();
        for(Halte halte: list) {
            Feature feature = Feature.fromGeometry(Point.fromLngLat(halte.getLongitude(), halte.getLatitude()));
            feature.addStringProperty(ICON_PROPERTY, ICON_ID_BLUE);
            listFeature.add(feature);
        }

        Feature feature = Feature.fromGeometry(Point.fromLngLat(112.790206, -7.279251));
        feature.addStringProperty(ICON_PROPERTY, ICON_ID_BUS);
        listFeature.add(feature);

        geoJsonSource = new GeoJsonSource(SOURCE_ID, FeatureCollection.fromFeatures(listFeature));

        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/etabus73/ckc8nsg5e2yp11ipe2djz8xn8")
                        .withImage(ICON_ID_BLUE, BitmapFactory.decodeResource(
                                MapActivity.this.getResources(), R.drawable.ic_halte_blue))
                        .withImage(ICON_ID_BUS, BitmapFactory.decodeResource(
                                MapActivity.this.getResources(), R.drawable.ic_bus_map))
                        .withSource(geoJsonSource)
                        .withLayer(new SymbolLayer(LAYER_ID, SOURCE_ID)
                                .withProperties(
                                        iconImage(match(
                                            get(ICON_PROPERTY), literal(ICON_ID_BLUE),
                                            stop(ICON_ID_BUS, ICON_ID_BUS))),
                                        iconAllowOverlap(true),
                                        iconRotationAlignment(Property.ICON_ROTATION_ALIGNMENT_MAP),
                                        iconAnchor(Property.ICON_ANCHOR_BOTTOM))
                        ), this::enableLocationComponent);
    }

    private void initView(Bundle savedInstanceState){
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        int btnStartVisible = UserPref.getField(this, "jenis").equals("driver") ? View.VISIBLE : View.GONE;
        btnStartNav = findViewById(R.id.startButton);
        btnStartNav.setVisibility(btnStartVisible);
        btnStartNav.setOnClickListener(view -> startActivity(new Intent(MapActivity.this, RouteActivity.class)));

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(getString(R.string.map));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .useDefaultLocationEngine(false)
                            .build();

            locationComponent.activateLocationComponent(locationComponentActivationOptions);
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);
            initLocationEngine();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "Explanation", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            if (mapboxMap.getStyle() != null) enableLocationComponent(mapboxMap.getStyle());
        } else {
            Toast.makeText(this, "Not granted", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private final ValueAnimator.AnimatorUpdateListener animatorUpdateListener = valueAnimator -> {
        LatLng animatedPosition = (LatLng) valueAnimator.getAnimatedValue();

        listFeature.remove(listFeature.size()-1);
        Feature feature = Feature.fromGeometry(Point.fromLngLat(animatedPosition.getLongitude(), animatedPosition.getLatitude()));
        feature.addStringProperty(ICON_PROPERTY, ICON_ID_BUS);
        listFeature.add(feature);

        geoJsonSource.setGeoJson(FeatureCollection.fromFeatures(listFeature));
    };

    private static final TypeEvaluator<LatLng> latLngEvaluator = new TypeEvaluator<LatLng>() {
        private final LatLng latLng = new LatLng();

        @Override
        public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
            latLng.setLatitude(startValue.getLatitude() + ((endValue.getLatitude() - startValue.getLatitude()) * fraction));
            latLng.setLongitude(startValue.getLongitude() + ((endValue.getLongitude() - startValue.getLongitude()) * fraction));
            return latLng;
        }
    };

    private static class MapActivityLocationCallback implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<MapActivity> activityWeakReference;

        MapActivityLocationCallback(MapActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(LocationEngineResult result) {
            MapActivity activity = activityWeakReference.get();
            if (activity != null) {
                Location location = result.getLastLocation();
                if (location == null) return;
                if (activity.mapboxMap != null && result.getLastLocation() != null) {
                    activity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
                }
            }
        }

        @Override
        public void onFailure(@NonNull Exception exception) {
            Timber.d(exception.getLocalizedMessage());
            MapActivity activity = activityWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class FetchBusRunnable extends ViewRefreshHandler.ViewRunnable<View> {

        public FetchBusRunnable(@Nullable Bundle args, View ...view) {
            super(args, view);
        }

        @Override
        protected void run(View[] view, Bundle args) {
            Call<List<BusInfo>> call = apiService.getBusInfo();
            call.enqueue(new Callback<List<BusInfo>>() {
                @Override
                public void onResponse(@NotNull Call<List<BusInfo>> call, @NotNull Response<List<BusInfo>> response) {
                    if(response.body() != null){
                        BusInfo info = response.body().get(0);
                        LatLng point = new LatLng(info.getLat(), info.getLng());

                        if (animator != null && animator.isStarted()) {
                            busPoint = (LatLng) animator.getAnimatedValue();
                            animator.cancel();
                        }

                        animator = ObjectAnimator.ofObject(latLngEvaluator, busPoint, point).setDuration(1000);
                        animator.addUpdateListener(animatorUpdateListener);
                        animator.start();
                        busPoint = point;
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<BusInfo>> call, @NotNull Throwable t) {

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}