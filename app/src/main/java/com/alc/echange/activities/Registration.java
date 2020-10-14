package com.alc.echange.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alc.echange.R;
import com.alc.echange.api.RetrofitClient;
import com.alc.echange.model.Users;
import com.google.android.material.textfield.TextInputEditText;


import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registration extends AppCompatActivity {
    TextInputEditText etFname, etLname,etEmail, etPhone, etPassword;
    private Button btnReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //        AndroidNetworking.initialize(getApplicationContext());

        etFname = findViewById(R.id.etFname);
        etLname = findViewById(R.id.etLname);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);

        btnReg = findViewById(R.id.btnReg);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String firstname = etFname.getText().toString().trim();
                final String lastname = etLname.getText().toString().trim();
                final String email = etEmail.getText().toString().trim();
                final String phone = etPhone.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (TextUtils.isEmpty(firstname)) {
                    etFname.setError("first name is required");
                    etFname.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(lastname)) {
                    etLname.setError("last name is required");
                    etLname.requestFocus();
                    return;
                }


                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("Email is required!");
                    etEmail.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    etEmail.setError("Enter a valid email");
                    etEmail.requestFocus();
                    return;
                }
                if (phone.isEmpty()) {
                    etPhone.setError("Enter a Phone Number");
                    etPhone.requestFocus();
                    return;
                }
                if (phone.length() <= 10) {
                    etPhone.setError("Enter a valid Phone number");
                    etPhone.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("Password is required");
                    etPassword.requestFocus();
                    return;

                }
                if (password.length() < 6) {
                    etPassword.setError("Password length is too short");
                    etPassword.requestFocus();

                } else {
                    registerUser(firstname, lastname, email, phone, password);

                }
            }
        });
    }

    public void registerUser(String firstname, String lastname, String email, String phone, String password) {
       //do registration API call
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing Up...");
        progressDialog.show();

        Call<Users> call = RetrofitClient
                .getInstance()
                .getApi()
                .createUser(firstname, lastname, email, phone, password);
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                progressDialog.dismiss();
                Intent intent = new Intent(Registration.this, LoginActivity.class);
                startActivity(intent);
                Toast.makeText(Registration.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(Registration.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("throwing " + t);
            }
        });
    }

    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Want to go back?")
                .setMessage("Are you sure you want to go back?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(Registration.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }).create().show();
    }
}