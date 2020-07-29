package com.example.eta.app.log;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.example.eta.R;
import com.example.eta.api.ApiClient;
import com.example.eta.api.ApiService;
import com.example.eta.api.model.User;
import com.example.eta.app.main.MainActivity;
import com.example.eta.util.UserPref;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DaftarActivity extends AppCompatActivity {

    private EditText etNama, etNoTelp, etEmail, etPassword;
    private TextInputLayout ilEmail;
    private Button btnDaftar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);
        initView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView(){
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(getString(R.string.register));
        actionBar.setDisplayHomeAsUpEnabled(true);

        etNama = findViewById(R.id.etNamaDaftar);
        etNoTelp = findViewById(R.id.etTelpDaftar);
        etEmail = findViewById(R.id.etEmailDaftar);
        etPassword = findViewById(R.id.etPasswordDaftar);
        ilEmail = findViewById(R.id.email);
        btnDaftar = findViewById(R.id.btnDaftar);

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkEmail(editable.toString());
            }
        });

        btnDaftar.setOnClickListener(view ->
                register(getString(etNama), getString(etNoTelp), getString(etEmail), hash256(getString(etPassword))));
    }

    private String getString(EditText editText){
        return editText.getText().toString();
    }

    private void checkEmail(String email){
        ApiService service = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<Integer> call = service.checkEmail(email);

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NotNull Call<Integer> call, @NotNull Response<Integer> response) {
                if(response.body() != null){
                    ilEmail.setError("Email sudah digunakan");
                }
                else{
                    ilEmail.setError(null);
                    ilEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void onFailure(@NotNull Call<Integer> call, @NotNull Throwable t) {

            }
        });
    }

    private void register(String nama, String noTelp, String email, String password){
        ApiService service = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<List<User>> call = service.userRegister(nama, noTelp, email, password);

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NotNull Call<List<User>> call, @NotNull Response<List<User>> response) {
                if(response.body() != null) {
                    User user = response.body().get(0);
                    UserPref.insertUser(DaftarActivity.this, user);
                    startActivity(new Intent(DaftarActivity.this, MainActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<User>> call, @NotNull Throwable t) {

            }
        });
    }

    private String hash256(String password) {
        HashCode hexString = Hashing.sha256().hashString(password, StandardCharsets.UTF_8);
        return hexString.toString();
    }
}