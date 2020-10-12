package com.alc.echange;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTPVerificationActivity extends AppCompatActivity {

    private static final String TAG = "OTPVerificationActivity";
    EditText mEditText1, mEditText2, mEditText3, mEditText4, mEditText5, mEditText6;
    TextView mTextHeader, mTextBoxTitle;
    ImageView mImageCheckError;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private Button mVerificationButton;
    private FirebaseAuth mAuth;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            //Getting the code sent by SMS
            String code = credential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                mEditText1.setText(code.substring(0,1));
                mEditText2.setText(code.substring(1,2));
                mEditText3.setText(code.substring(2,3));
                mEditText4.setText(code.substring(3,4));
                mEditText5.setText(code.substring(4,5));
                mEditText6.setText(code.substring(5,6));

                verifyPhoneNumberWithCode(mVerificationId, code);

            }
            Toast.makeText(OTPVerificationActivity.this, "Verification completed!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:" + verificationId);

            // Save verification ID and resending token so we can use them later
            mVerificationId = verificationId;
            mResendToken = token;

            // [START_EXCLUDE]
            // Update UI
            //updateUI(STATE_CODE_SENT);
            // [END_EXCLUDE]
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        mAuth = FirebaseAuth.getInstance();
        editTextInitialization();
        mTextHeader = findViewById(R.id.text_header);
        mTextBoxTitle = findViewById(R.id.text_box_title);
        mImageCheckError = findViewById(R.id.image_check_error);

        mVerificationButton = findViewById(R.id.verify_otp);

        startPhoneNumberVerification();
        verificationButtonClick();
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    private void startPhoneNumberVerification() {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+23408067994494",        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            mTextHeader.setText("You are almost there...");
                            mTextBoxTitle.setText("OTP Verified");
                            mTextBoxTitle.setTextColor(getResources().getColor(R.color.colorGreen));
                            mImageCheckError.setImageResource(R.drawable.ic_baseline_check_);
                            mVerificationButton.setText("Next");
                            Toast.makeText(OTPVerificationActivity.this, "Sign in successful", Toast.LENGTH_SHORT).show();

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                mTextHeader.setText("You are almost there...");
                                mTextBoxTitle.setText("OTP Verified");
                                mVerificationButton.setText("Retry");
                                mTextBoxTitle.setTextColor(getResources().getColor(R.color.colorRed));
                                mImageCheckError.setImageResource(R.drawable.ic_baseline_error);
                            }

                        }
                    }
                });
    }


    private void verificationButtonClick() {

        mVerificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String code = mEditText1.getText().toString() + mEditText2.getText().toString() +
                        mEditText2.getText().toString() + mEditText2.getText().toString();

                if (code.isEmpty() || code.length() < 6) {
                    Toast.makeText(OTPVerificationActivity.this, "Enter Valid Code", Toast.LENGTH_SHORT).show();
                    return;

                }

                //verifying the code entered manually
                verifyPhoneNumberWithCode(mVerificationId, code);
            }
        });
    }

    public class GenericKeyEvent implements View.OnKeyListener {


        private EditText currentView, previousView;
        public GenericKeyEvent (EditText currentView, EditText previousView) {
            this.currentView = currentView;
            this.previousView = previousView;
        }

        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_DEL &&
                    currentView.getId() != R.id.edit_text_one && currentView.getText().toString().isEmpty()) {
                //If current is empty then previous EditText's number will also be deleted
                previousView.setText(null);
                previousView.requestFocus();
                return true;
            }
            return false;
        }
    }

    public class GenericTextWatcher implements TextWatcher {

        private View currentView, nextView;

        private GenericTextWatcher(View currentView, View nextView) {
            this.currentView = currentView;
            this.nextView = nextView;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // TODO Auto-generated method stub
            String text = editable.toString();
            switch (currentView.getId()) {
                case R.id.edit_text_one:
                case R.id.edit_text_two:
                case R.id.edit_text_three:
                case R.id.edit_text_four:
                case R.id.edit_text_five:
                    if (text.length() == 1)
                        nextView.requestFocus();
                    break;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }
    }

    private void editTextInitialization() {
        mEditText1 = findViewById(R.id.edit_text_one);
        mEditText2 = findViewById(R.id.edit_text_two);
        mEditText3 = findViewById(R.id.edit_text_three);
        mEditText4 = findViewById(R.id.edit_text_four);
        mEditText5 = findViewById(R.id.edit_text_five);
        mEditText6 = findViewById(R.id.edit_text_six);

        mEditText1.setInputType(InputType.TYPE_NULL);
        mEditText1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditText1.setInputType(InputType.TYPE_CLASS_NUMBER);
                mEditText1.requestFocus();
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.showSoftInput(mEditText1, InputMethodManager.SHOW_FORCED);

            }
        });

        mEditText1.addTextChangedListener(new GenericTextWatcher(mEditText1, mEditText2));
        mEditText2.addTextChangedListener(new GenericTextWatcher(mEditText2, mEditText3));
        mEditText3.addTextChangedListener(new GenericTextWatcher(mEditText3, mEditText4));
        mEditText4.addTextChangedListener(new GenericTextWatcher(mEditText4, mEditText5));
        mEditText5.addTextChangedListener(new GenericTextWatcher(mEditText5, mEditText6));
        mEditText6.addTextChangedListener(new GenericTextWatcher(mEditText6, null));

        //GenericKeyEvent here works for deleting the element and to switch back to previous EditText
        //first parameter is the current EditText and second parameter is previous EditText
        mEditText1.setOnKeyListener(new GenericKeyEvent(mEditText1, null));
        mEditText2.setOnKeyListener(new GenericKeyEvent(mEditText2, mEditText1));
        mEditText3.setOnKeyListener(new GenericKeyEvent(mEditText3, mEditText2));
        mEditText4.setOnKeyListener(new GenericKeyEvent(mEditText4, mEditText3));
        mEditText5.setOnKeyListener(new GenericKeyEvent(mEditText5, mEditText4));
        mEditText6.setOnKeyListener(new GenericKeyEvent(mEditText6, mEditText5));
    }
}