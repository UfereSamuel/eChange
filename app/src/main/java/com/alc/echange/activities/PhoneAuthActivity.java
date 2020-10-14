package com.alc.echange.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.alc.echange.R;
import com.google.android.material.textfield.TextInputEditText;

public class PhoneAuthActivity extends AppCompatActivity {
    TextInputEditText mOtpPhone;
    Button mContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_phone);

        mOtpPhone = findViewById(R.id.otpPhone);
        mContinue = findViewById(R.id.cont);

        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //OTP request
                String otp = mOtpPhone.getText().toString().trim();
                if (TextUtils.isEmpty(otp)) {
                    mOtpPhone.setError("Enter Phone number!");
                    mOtpPhone.requestFocus();
                } else {

                Intent otpIntent = new Intent(getApplicationContext(), OTPVerificationActivity.class);
                startActivity(otpIntent);
                }
            }
        });
    }
}