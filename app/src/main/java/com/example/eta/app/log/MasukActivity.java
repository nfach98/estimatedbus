package com.example.eta.app.log;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eta.R;
import com.example.eta.api.ApiClient;
import com.example.eta.api.ApiService;
import com.example.eta.api.model.User;
import com.example.eta.app.main.MainActivity;
import com.example.eta.view.ProgressButton;
import com.example.eta.util.UserPref;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/** activity untuk fungsi login pengguna
 *  terdapat input email pengguna dan kata sandi
 */

public class MasukActivity extends AppCompatActivity {

    ProgressButton btnMasuk;
    TextView tvRegister;
    ImageSwitcher wallpaper;

    EditText etUsername, etPassword;

    int imageIndex;
    int[] images = {R.drawable.wallpaper1, R.drawable.wallpaper2, R.drawable.wallpaper3, R.drawable.wallpaper4, R.drawable.wallpaper5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masuk);
        initView();
    }

    private void initView(){
        //menyembunyikan actionbar
        Objects.requireNonNull(getSupportActionBar()).hide();

        //inisialisasi variabel untuk setiap view
        btnMasuk = findViewById(R.id.btnMasuk);
        tvRegister = findViewById(R.id.tvRegister);
        wallpaper = findViewById(R.id.wallpaper);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        //mengganti teks tombol masuk menjadi masuk
        btnMasuk.setText(getString(R.string.login));
        //set tombol masuk untuk mengeksekusi fungsi login dan animasi loading ketika ditekan
        btnMasuk.setOnClickListener(view ->{
            btnMasuk.setLoading(true);
            login(etUsername.getText().toString(), hash256(etPassword.getText().toString()));
        });

        //set teks daftar untuk memunculkan activity daftar loading ketika ditekan
        tvRegister.setOnClickListener(view -> startActivity(new Intent(MasukActivity.this, DaftarActivity.class)));

        // mengatur wallpaper berganti otomatis dan animasinya
        wallpaper.setInAnimation( AnimationUtils.loadAnimation(this, R.anim.fade_in));
        wallpaper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));

        wallpaper.setFactory(() -> {
            ImageView iv = new ImageView(getApplicationContext());
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImageSwitcher.LayoutParams params = new ImageSwitcher.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            iv.setLayoutParams(params);
            return iv;
        });

        imageIndex = 0;
        showImage(imageIndex);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> nextImage());
            }
        }, 0, 5000);

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Timber.d(hash256(etPassword.getText().toString()));
            }
        });
    }

    private void showImage(int index){
        int resId = images[index];
        wallpaper.setImageResource(resId);
    }

    private void nextImage(){
        if(imageIndex < images.length-1) imageIndex++;
        else if(imageIndex == images.length-1) imageIndex = 0;
        showImage(imageIndex);
    }

    private String hash256(String password) {
        HashCode hexString = Hashing.sha256().hashString(password, StandardCharsets.UTF_8);
        return hexString.toString();
    }

    private void login(String username, String password){
        ApiService service = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<List<User>> call = service.userLogin(username, password);

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NotNull Call<List<User>> call, @NotNull Response<List<User>> response) {
                if(response.body() != null && response.body().size() > 0) {
                    User user = response.body().get(0);
                    UserPref.insertUser(MasukActivity.this, user);
                    startActivity(new Intent(MasukActivity.this, MainActivity.class));
                    finish();
                }
                else{
                    Timber.d("Gak iso");
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<User>> call, @NotNull Throwable t) {
                Timber.d("Gagal");
            }
        });
    }
}