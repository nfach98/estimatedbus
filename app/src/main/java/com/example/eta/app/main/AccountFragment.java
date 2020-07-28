package com.example.eta.app.main;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eta.R;
import com.example.eta.api.ApiClient;
import com.example.eta.api.ApiService;
import com.example.eta.app.edit.EditProfileActivity;
import com.example.eta.app.edit.EditSecurityActivity;
import com.example.eta.app.log.MasukActivity;
import com.example.eta.util.UserPref;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class AccountFragment extends Fragment {

    CircleImageView ivPhoto;
    TextView tvNama, tvTelp, tvEmail;

    public AccountFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout profile = view.findViewById(R.id.profile);
        LinearLayout security = view.findViewById(R.id.security);
        LinearLayout logout = view.findViewById(R.id.logout);
        LinearLayout delete = view.findViewById(R.id.delete);

        ivPhoto = view.findViewById(R.id.photo);
        tvNama = view.findViewById(R.id.nama);
        tvTelp = view.findViewById(R.id.telp);
        tvEmail = view.findViewById(R.id.email);

        Dialog diaLogout = getPopup();

        profile.setOnClickListener(view14 -> startActivity(new Intent(getActivity(), EditProfileActivity.class)));

        security.setOnClickListener(view14 -> startActivity(new Intent(getActivity(), EditSecurityActivity.class)));

        logout.setOnClickListener(view1 -> {
            TextView message = diaLogout.findViewById(R.id.tvMessage);
            message.setText(getString(R.string.popup_logout));

            TextView batal = diaLogout.findViewById(R.id.tvNo);
            batal.setOnClickListener(view2 -> diaLogout.dismiss());

            Button hapus = diaLogout.findViewById(R.id.btnYes);
            hapus.setText(getString(R.string.logout));
            hapus.setOnClickListener(view22 -> {
                UserPref.deleteUser(getActivity());
                Intent intent = new Intent(getActivity(), MasukActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Objects.requireNonNull(getActivity()).finish();
            });

            diaLogout.show();
        });

        delete.setOnClickListener(view12 -> {
            TextView message = diaLogout.findViewById(R.id.tvMessage);
            message.setText(getString(R.string.popup_delete));

            TextView batal = diaLogout.findViewById(R.id.tvNo);
            batal.setOnClickListener(view2 -> diaLogout.dismiss());

            Button hapus = diaLogout.findViewById(R.id.btnYes);
            hapus.setText(getString(R.string.delete));
            hapus.setOnClickListener(view13 -> deleteUser(UserPref.getField(getActivity(), "email")));

            diaLogout.show();
        });

        setUserData();
    }

    @Override
    public void onResume() {
        super.onResume();
        setUserData();
    }

    private void setUserData(){
        Context context = getActivity();

        tvNama.setText(UserPref.getField(context, "nama"));
        tvTelp.setText(UserPref.getField(context, "no_telp"));
        tvEmail.setText(UserPref.getField(context, "email"));
        Glide.with(context)
                .load(UserPref.getField(context, "photo"))
                .placeholder(R.drawable.ic_person_12)
                .error(R.drawable.ic_person_12)
                .into(ivPhoto);
    }

    private void deleteUser(String username){
        ApiService service = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<Integer> call = service.userDelete(username);

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NotNull Call<Integer> call, @NotNull Response<Integer> response) {
                if(Objects.requireNonNull(response.body()) == 1) {
                    UserPref.deleteUser(getActivity());
                    Intent intent = new Intent(getActivity(), MasukActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    Objects.requireNonNull(getActivity()).finish();
                }
                else{
                    Timber.d("Gak iso");
                }
            }

            @Override
            public void onFailure(@NotNull Call<Integer> call, @NotNull Throwable t) {
                Timber.d("Gagal");
            }
        });
    }

    private Dialog getPopup(){
        Dialog dialog = new Dialog(Objects.requireNonNull(getActivity()));
        dialog.setContentView(R.layout.layout_window);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }
}