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
import com.alc.echange.utils.Util;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

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
    private CountryCodePicker mCountryCodePicker;
    private String mCountryCode;
    private String phone;

    private SharedPreferences preference;
    private SharedPreferences.Editor editor;
    public static String PREFS_NAME = "app";
    public static String PREF_PHONE = "phone";

    private int status;
    private String message;
    private String email;
    private String phoneNew;
    private int balance;
    private Users model;
    private Util util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        hideSoftKeyBoard();

        // [Session management variables initialize with logic start]
        sessionManagement = new SessionManagement(this);

        util = new Util();

        preference = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        editor = preference.edit();

        mCountryCodePicker = findViewById(R.id.ccp1);

        mCountryCode = mCountryCodePicker.getSelectedCountryCode();
        mCountryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                mCountryCode = mCountryCodePicker.getSelectedCountryCode();
            }
        });

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
                phone = mPhone.getText().toString().trim();
                mPasswordEntered = mPassword.getText().toString().trim();

                if (util.isNetworkAvailable(getApplicationContext())){
                    if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(mPasswordEntered)) {
                        Toast.makeText(getApplicationContext(), "Fields must not be empty!", Toast.LENGTH_SHORT).show();
                    } else if (phone.startsWith("0")) {
                        mPhoneNoEntered = "+" + mCountryCode + phone.substring(1);
                        Log.d("log2", mPasswordEntered);
//                    loginUser(mPhoneNoEntered, mPasswordEntered);
                        loginUser();
                    } else if (!phone.startsWith("0")) {
                        mPhoneNoEntered = "+" + mCountryCode + phone;
                        Log.d("log21", mPhoneNoEntered);
                        loginUser();
//                    loginUser(mPhoneNoEntered, mPasswordEntered);
                        //login(mPhoneNoEntered, mPasswordEntered); //used for test
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Plese check your network connection...", Toast.LENGTH_SHORT).show();
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

    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if(imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void loginUser() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Authenticating please wait...");
        progressDialog.show();
        AndroidNetworking.post("https://mobile-echange.herokuapp.com/api/v1/login")
                .addBodyParameter("phone", mPhoneNoEntered)
                .addBodyParameter("password", mPasswordEntered)
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        progressDialog.dismiss();
                        Log.d("succ2", response.toString());
                        try {
                            status = response.getInt("status");
                            message = response.getString("message");
                            email = response.getString("email");
                            phoneNew = response.getString("phone");
                            balance = response.getInt("balance");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (status == 200 && message.equalsIgnoreCase("Login Successfully!")) {

                            model = new Users();
                            model.setBalance(balance);
                            model.setEmail(email);
                            model.setPhone(phoneNew);

                            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            editor.putInt("balance", balance);
                            editor.putString("email", email);
                            editor.putString("phone", phoneNew);

                            editor.commit();

                            sessionManagement.setLoginPhoneNo(phoneNew);
                    sessionManagement.setLoginPassword(mPasswordEntered);

                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "Login Failed...", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.d("succ2", error.toString());
                        progressDialog.dismiss();
                    }
                });
    }

    private void login(String mPhoneNoEntered, String mPasswordEntered) {
        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
        startActivity(intent);
        finish();

        //On successful login with phone number and password, credentials will be saved
        sessionManagement.setLoginPhoneNo(mPhoneNoEntered);
        sessionManagement.setLoginPassword(mPasswordEntered);
    }

//    public void loginUser(String phone, String password) {
//        Call<Users> call = RetrofitClient
//                .getInstance()
//                .getApi()
//                .login(phone, password);
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Authenticating please wait...");
//        progressDialog.show();
//        call.enqueue(new Callback<Users>() {
//            @Override
//            public void onResponse(Call<Users> call, Response<Users> response) {
//                Log.d("succ2", ""+response.body());
//                if (response.isSuccessful()) {
//                    progressDialog.dismiss();
//                    Toast.makeText(getApplicationContext(), "Login Success!", Toast.LENGTH_SHORT).show();
//
//                    try {
//                        JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
//                        String msg = jsonObject.getString("message");
//
//                        Log.d("succ2", jsonObject.toString() );
//                        Log.d("succ2", "msg: "+msg );
////                        status = jsonObject.getBoolean("status");
//
////                        msg = jsonObject.getString("msg");
////                        status = jsonObject.getBoolean("status");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
////                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
////                    startActivity(intent);
//
//                    //On successful login with phone number and password, credentials will be saved
////                    sessionManagement.setLoginPhoneNo(mPhoneNoEntered);
////                    sessionManagement.setLoginPassword(mPasswordEntered);
//
//                } else {
//                    progressDialog.dismiss();
//                    Toast.makeText(getApplicationContext(), "Login failed!, Try Again", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Users> call, Throwable t) {
//                progressDialog.dismiss();
//                Log.d("fail2", "failed... "+call + ".... "+t.toString());
//                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

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


