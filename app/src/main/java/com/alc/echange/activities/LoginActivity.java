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
import android.widget.TextView;
import android.widget.Toast;

import com.alc.echange.R;
import com.alc.echange.api.RetrofitClient;
import com.alc.echange.model.Users;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText mPhone, mPassword;
    Button mLogin;
    TextView regLink;
    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mPhone = findViewById(R.id.etLoginPhone);
        mPassword = findViewById(R.id.etLoginPassword);
        mLogin = findViewById(R.id.btnLogin);
        regLink = findViewById(R.id.tvReg);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String phone = mPhone.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Fields must not be empty!", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser(phone, password);
                }
            }
        });

        regLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registration();
            }
        });
    }

    public void loginUser(String phone, String password) {
        Call<Users> call = RetrofitClient
                .getInstance()
                .getApi()
                .login(phone, password);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Authenticating please wait...");
        progressDialog.show();
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful()) {
                    System.out.println("Calling ::" + call);
                    System.out.println("Systems :: "+ response);
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Login Success!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                    startActivity(intent);
                } else {
                    System.out.println("Printing : " + response);
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Login failed!, Try Again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registration() {
        Intent regIntent = new Intent(getApplicationContext(), PhoneAuthActivity.class);
        startActivity(regIntent);
    }

    @Override
    public void onBackPressed() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to Exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}


