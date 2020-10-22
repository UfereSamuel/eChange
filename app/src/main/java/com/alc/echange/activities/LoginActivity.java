package com.alc.echange.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alc.echange.R;
import com.alc.echange.SessionManagement;
import com.alc.echange.api.RetrofitClient;
import com.alc.echange.model.Users;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    public static final String DASHBOARD_INTENT = "com.alc.echange.activities.LoginActivity.DASHBOARD_INTENT";
    TextInputEditText mPhone, mPassword;
    Button mLogin;
    TextView regLink;
    //Session Management variables
    private SessionManagement sessionManagement;
    private String mPhoneNoEntered;
    private String mPasswordEntered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // [Session management variables initialize with logic start]
        sessionManagement = new SessionManagement(this);

        String savedPhoneNo = sessionManagement.getLoginPhoneNo();
        String savedPassword = sessionManagement.getLoginPassword();

        if (!savedPhoneNo.equals("") && !savedPassword.equals("")) {

            mPhoneNoEntered = savedPhoneNo;
            mPasswordEntered = savedPassword;

            if (savedPhoneNo.equals(mPhoneNoEntered) && savedPassword.equals(mPasswordEntered)) {
                Intent intent = new Intent(this, DashboardActivity.class);
                intent.putExtra(DASHBOARD_INTENT, true);
                startActivity(intent);
                finish();
            } else {
                return;
            }
        }
        // [Session management variables initialize with logic end]

        mPhone = findViewById(R.id.etLoginPhone);

        mPhone.setInputType(InputType.TYPE_NULL);
        mPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhone.setInputType(InputType.TYPE_CLASS_NUMBER);
                mPhone.requestFocus();
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.showSoftInput(mPhone, InputMethodManager.SHOW_FORCED);
                mPhone.setCursorVisible(true);
            }
        });

        mPassword = findViewById(R.id.etLoginPassword);
        mLogin = findViewById(R.id.btnLogin);
        regLink = findViewById(R.id.tvReg);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPhoneNoEntered = mPhone.getText().toString().trim();
                mPasswordEntered = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(mPhoneNoEntered) || TextUtils.isEmpty(mPasswordEntered)) {
                    Toast.makeText(getApplicationContext(), "Fields must not be empty!", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser(mPhoneNoEntered, mPasswordEntered);
                    //login(mPhoneNoEntered, mPasswordEntered); //used for test
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

//    private void login(String mPhoneNoEntered, String mPasswordEntered) {
//        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
//        startActivity(intent);
//        finish();
//
//        //On successful login with phone number and password, credentials will be saved
//        sessionManagement.setLoginPhoneNo(mPhoneNoEntered);
//        sessionManagement.setLoginPassword(mPasswordEntered);
//    }

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
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Login Success!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                    startActivity(intent);

                    //On successful login with phone number and password, credentials will be saved
                    sessionManagement.setLoginPhoneNo(mPhoneNoEntered);
                    sessionManagement.setLoginPassword(mPasswordEntered);

                } else {
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


