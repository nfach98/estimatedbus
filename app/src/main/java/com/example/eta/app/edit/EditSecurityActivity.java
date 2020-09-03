package com.example.eta.app.edit;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.eta.R;
import com.example.eta.api.ApiClient;
import com.example.eta.api.ApiService;
import com.example.eta.util.UserPref;
import com.example.eta.view.ProgressButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/** activity untuk edit data keamanan pengguna
 *  data berupa email dan password
 */

public class EditSecurityActivity extends AppCompatActivity {

    private ProgressButton btnSubmit;
    private EditText etEmail, etPassword, etConPassword;
    private TextInputLayout ilConPassword;
    private Dialog dialog;

    boolean changed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_security);
        initView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            if(!changed) finish();
            else dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(!changed) super.onBackPressed();
        else dialog.show();
    }

    private void initView(){
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(getString(R.string.security));
        actionBar.setDisplayHomeAsUpEnabled(true);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_window);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        TextView message = dialog.findViewById(R.id.tvMessage);
        message.setText(R.string.change_unsaved);

        TextView batal = dialog.findViewById(R.id.tvNo);
        batal.setOnClickListener(view2 -> dialog.dismiss());

        Button keluar = dialog.findViewById(R.id.btnYes);
        keluar.setText(getString(R.string.logout));
        keluar.setOnClickListener(view13 -> finish());

        etEmail = findViewById(R.id.etEmailEdit);
        etPassword = findViewById(R.id.etPasswordEdit);
        etConPassword = findViewById(R.id.etConPasswordEdit);
        ilConPassword = findViewById(R.id.conPassword);
        btnSubmit = findViewById(R.id.btnSubmit);

        etEmail.setText(UserPref.getField(this, "email"));

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!(editable.toString().equals(UserPref.getField(EditSecurityActivity.this, "email")))){
                    enableButton(true);
                }
                else enableButton(false);
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!(editable.toString().equals(""))){
                    enableButton(true);
                }
                else enableButton(false);
            }
        });

        etConPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!(editable.toString().equals(etPassword.getText().toString()))){
                    ilConPassword.setError("Password tidak sama");
                }
                else {
                    ilConPassword.setError(null);
                    ilConPassword.setErrorEnabled(false);
                }
            }
        });

        btnSubmit.setOnClickListener(view -> {
            btnSubmit.setLoading(true);
            update(etEmail.getText().toString(), hash256(etPassword.getText().toString()));
        });
    }

    private void update(String email, String password){
        ApiService service = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<Integer> call = service.userUpdate(null, null, null,email,password,
                UserPref.getField(EditSecurityActivity.this, "email"));

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NotNull Call<Integer> call, @NotNull Response<Integer> response) {
                if(response.body() != null){
                    if(response.body() == 0) Timber.d("Gagal");
                    else if(response.body() == 1){
                        UserPref.updateField(EditSecurityActivity.this , "email", email);
                        finish();
                    }
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

    private String hash256(String password) {
        HashCode hexString = Hashing.sha256().hashString(password, StandardCharsets.UTF_8);
        return hexString.toString();
    }
}