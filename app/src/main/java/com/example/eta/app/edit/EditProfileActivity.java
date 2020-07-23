package com.example.eta.app.edit;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.eta.R;
import com.example.eta.api.ApiClient;
import com.example.eta.api.ApiService;
import com.example.eta.api.model.User;
import com.example.eta.util.UserPref;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class EditProfileActivity extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 42;

    Button btnSubmit;
    EditText etNama, etTelp;
    LinearLayout changePhoto;

    String userPhoto;
    boolean changed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri;
            if (data != null) {
                uri = data.getData();
                enableButton(true);
            }
        }
    }

    private void initView(){
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(getString(R.string.profile));
        actionBar.setDisplayHomeAsUpEnabled(true);

        etNama = findViewById(R.id.etNamaEdit);
        etNama.setText(UserPref.getField(this, "nama"));
        etNama.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!(editable.toString().equals(UserPref.getField(EditProfileActivity.this, "nama")))){
                    enableButton(true);
                }
                else enableButton(false);
            }
        });

        etTelp = findViewById(R.id.etTelpEdit);
        etTelp.setText(UserPref.getField(this, "no_telp"));
        etTelp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!(editable.toString().equals(UserPref.getField(EditProfileActivity.this, "no_telp")))){
                    enableButton(true);
                }
                else enableButton(false);
            }
        });

        changePhoto = findViewById(R.id.changePhoto);
        changePhoto.setOnClickListener(view -> {
            Intent openBrowser = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            openBrowser.addCategory(Intent.CATEGORY_OPENABLE);
            openBrowser.setType("image/*");
            startActivityForResult(openBrowser, READ_REQUEST_CODE);
        });

        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(view -> update(etNama.getText().toString(), etTelp.getText().toString(), userPhoto));
    }

    private void update(String nama, String telp, String photo){
        ApiService service = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<Integer> call = service.userUpdate(nama, telp, photo,null,null,
                UserPref.getField(EditProfileActivity.this, "email"));

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NotNull Call<Integer> call, @NotNull Response<Integer> response) {
                if(response.body() != null){
                    if(response.body() == 0) Timber.d("Gagal");
                    else if(response.body() == 1) finish();
                }
            }

            @Override
            public void onFailure(@NotNull Call<Integer> call, @NotNull Throwable t) {

            }
        });
    }

    private void enableButton(boolean state){
        btnSubmit.setEnabled(state);
        changed = state;
        if(state) btnSubmit.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent)));
        else btnSubmit.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.darker_gray)));
    }
}