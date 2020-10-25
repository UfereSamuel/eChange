package com.alc.echange.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alc.echange.OTPVerificationActivity;
import com.alc.echange.R;
import com.hbb20.CountryCodePicker;

public class PhoneAuthActivity extends AppCompatActivity {
    private static final String TAG = "PhoneAuthActivity";
    public static final String USER_PHONE_NUMBER = "com.alc.echange.activities.USER_PHONE_NUMBER";
    private EditText mEditTextPhoneNo;
    private CountryCodePicker mCountryCodePicker;
    private String mCountryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_phone);

        mEditTextPhoneNo = findViewById(R.id.edit_text_mobile_number);
        mCountryCodePicker = findViewById(R.id.ccp);

        //Default country code set to Nigeria
        mCountryCodePicker.setCountryForPhoneCode(234);
        Log.i(TAG, "onCreate: Country Phone Code: " + mCountryCodePicker.getSelectedCountryCode());

        mCountryCode = mCountryCodePicker.getSelectedCountryCode();
        mCountryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                mCountryCode = mCountryCodePicker.getSelectedCountryCode();
            }
        });

        Button mContinue = findViewById(R.id.button_continue);

        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //OTP request
                String mobileNo = mEditTextPhoneNo.getText().toString().trim();

                if (mobileNo.isEmpty() || mobileNo.length() < 10) {
                    mEditTextPhoneNo.setError("Valid number is required");
                    mEditTextPhoneNo.requestFocus();

                } else if (mobileNo.startsWith("0")) {
                    String phoneNo = "+" + mCountryCode + mobileNo.substring(1);
                    Log.i(TAG, "onClick: " + phoneNo);

                    otpIntent(phoneNo);

                } else if (!mobileNo.startsWith("0")){
                    String phoneNo = "+" + mCountryCode + mobileNo;
                    Log.i(TAG, "onClick: " + phoneNo);

                    otpIntent(phoneNo);
                }
            }
        });
    }

    private void otpIntent(String phoneNo) {
        Intent intent = new Intent(getApplicationContext(), OTPVerificationActivity.class);
        intent.putExtra(USER_PHONE_NUMBER, phoneNo);
        startActivity(intent);
    }
}