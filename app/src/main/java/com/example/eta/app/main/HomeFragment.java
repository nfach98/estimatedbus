package com.example.eta.app.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.eta.R;
import com.example.eta.api.ApiClient;
import com.example.eta.api.ApiService;
import com.example.eta.api.model.BusInfo;
import com.example.eta.util.TimeConverter;
import com.example.eta.util.ViewRefreshHandler;

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
    private ProgressBar progressBar;

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
        ViewRefreshHandler viewRefreshHandler = new ViewRefreshHandler();
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        viewRefreshHandler.executePerSecond(new FetchBusRunnable(null, layoutInfo, tvLat, tvLng, tvPos, tvSpeed, tvEta, tvMessage, progressBar));
    }

    private void initView(View view){
        layoutInfo = view.findViewById(R.id.infoActive);

        tvLat = view.findViewById(R.id.lat);
        tvLng = view.findViewById(R.id.lng);
        tvPos = view.findViewById(R.id.pos);
        tvSpeed = view.findViewById(R.id.speed);
        tvEta = view.findViewById(R.id.eta);
        tvMessage = view.findViewById(R.id.message);

        progressBar = view.findViewById(R.id.progress);
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
                        view[7].setVisibility(View.GONE);
                        if(info.getMessage().equals("Bus tidak beroperasi")){
                            view[0].setVisibility(View.GONE);
                        }
                        else {
                            view[0].setVisibility(View.VISIBLE);
                            ((TextView) view[1]).setText(String.valueOf(info.getLat()));
                            ((TextView) view[2]).setText(String.valueOf(info.getLng()));
                            ((TextView) view[3]).setText(info.getPos());
                            ((TextView) view[4]).setText(String.format("%s km/jam", info.getSpeed()));
                            ((TextView) view[5]).setText(new TimeConverter(info.getTime()).getConverted());
                        }

                        ((TextView) view[6]).setText(info.getMessage());
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<BusInfo>> call, @NotNull Throwable t) {

                }
            });
        }
    }

}