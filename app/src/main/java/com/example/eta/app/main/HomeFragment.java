package com.example.eta.app.main;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.eta.R;
import com.example.eta.api.ApiClient;
import com.example.eta.api.ApiService;
import com.example.eta.api.model.BusInfo;
import com.example.eta.util.TimeConverter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private ConstraintLayout layoutInfo;
    private TextView tvLat, tvLng, tvPos, tvSpeed, tvEta, tvMessage;
    private ApiService apiService;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        fetchBus();
    }

    private void initView(View view){
        layoutInfo = view.findViewById(R.id.infoActive);

        tvLat = view.findViewById(R.id.lat);
        tvLng = view.findViewById(R.id.lng);
        tvPos = view.findViewById(R.id.pos);
        tvSpeed = view.findViewById(R.id.speed);
        tvEta = view.findViewById(R.id.eta);
        tvMessage = view.findViewById(R.id.message);
    }

    private void fetchBus(){
        Call<List<BusInfo>> call = apiService.getBusInfo();
        call.enqueue(new Callback<List<BusInfo>>() {
            @Override
            public void onResponse(@NotNull Call<List<BusInfo>> call, @NotNull Response<List<BusInfo>> response) {
                if(response.body() != null){
                    BusInfo info = response.body().get(0);
                    if(info.getMessage().equals("Bus tidak beroperasi")){
                        layoutInfo.setVisibility(View.GONE);
                    }
                    else {
                        layoutInfo.setVisibility(View.VISIBLE);
                        tvLat.setText(String.valueOf(info.getLat()));
                        tvLng.setText(String.valueOf(info.getLng()));
                        tvPos.setText(info.getPos());
                        tvSpeed.setText(String.format("%s km/jam", info.getSpeed()));
                        tvEta.setText(new TimeConverter(info.getTime()).getConverted());
                    }

                    tvMessage.setText(info.getMessage());
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<BusInfo>> call, @NotNull Throwable t) {

            }
        });

        refresh();
    }

    private void refresh(){
        final Handler handler = new Handler();
        handler.postDelayed(this::fetchBus, 1000);
    }

}