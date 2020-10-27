package com.alc.echange.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.alc.echange.R;

import java.text.ParseException;
import java.util.Objects;

import faranjit.currency.edittext.CurrencyEditText;

import static com.alc.echange.activities.LoginActivity.DASHBOARD_INTENT;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "DashboardActivity";
    private ConstraintLayout mMainContainer;

    //Phone lock variables
    private static final int INTENT_AUTHENTICATE = 1;
    private Boolean mIsStopped, mCheckPassCode;
    private Context context = this;
    private static final int koboToNaira = 100;
//    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = findViewById(R.id.bottom_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        mMainContainer = findViewById(R.id.main_container);

        // [Phone lock boolean variables initialize ]
        mIsStopped = false;
        mCheckPassCode = true;
        // [Phone lock boolean variables initialize end]

        findViewById(R.id.fund_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fundAccount();
            }
        });

        boolean mIntentReceived = getIntent().getBooleanExtra(DASHBOARD_INTENT, false);
        if (mIntentReceived) {
            //Prompt user to unlock screen
            promptUnlockScreen();
        }
    }

    //Activity on background
    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: called");
        mIsStopped = true;
    }

    //Activity on foreground
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus)
            Log.i(TAG, "onWindowFocusChanged: hasFocus is " + hasFocus);

        if (mIsStopped && mCheckPassCode) {
            mCheckPassCode = false;
            //Prompt user to unlock screen
            promptUnlockScreen();
        }
    }

    private void promptUnlockScreen() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

            if (km.isKeyguardSecure()) {
                mCheckPassCode = false;

                km.inKeyguardRestrictedInputMode();
                Intent authIntent = km.createConfirmDeviceCredentialIntent(getString(R.string.dialog_title_auth), getString(R.string.dialog_msg_auth));
                startActivityForResult(authIntent, INTENT_AUTHENTICATE);

            } else {
                //Handling a phone without a passCode
                mCheckPassCode = true;
            }
        }
    }


    // call back when password is correct or cancel/avoided
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == INTENT_AUTHENTICATE) {
            if (resultCode == RESULT_OK) {
                //do something you want when pass the security
                mMainContainer.setVisibility(View.VISIBLE);
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                finish();
            } else{
                onBackPressed();
            }
        }
    }

    // Check if back button is pressed
    @Override
    public void onBackPressed() {

        if (!mCheckPassCode) { //back button pressed when user tries to cancel/avoid security check.
            mCheckPassCode = true;
            //For security reason app UI is invisible
            mMainContainer.setVisibility(View.INVISIBLE);
        }

        exitOnBackPressed();
    }

    private void exitOnBackPressed() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.overview:
                Toast.makeText(this, "This is the Overview menu", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.history:
                Toast.makeText(this, "This is the History menu", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.fund_account:
                Toast.makeText(this, "This is the fund account menu", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    public void sendCash(View view) {
        startActivity(new Intent(getApplicationContext(), SendCashActivity.class));
        finish();
    }

    public void requestCash(View view) {
        startActivity(new Intent(getApplicationContext(), RequestCashActivity.class));
        finish();
    }

    public void airtimePurchase(View view) {
        startActivity(new Intent(getApplicationContext(), AirtimeActivity.class));
        finish();
    }

    public void buyData(View view) {
    }

//    public void fundWallet(View view) {
//        Toast.makeText(this, "thisss...", Toast.LENGTH_SHORT).show();
//        fundAccount();
//    }

    private void fundAccount() {
        Toast.makeText(this, "thisss333...", Toast.LENGTH_SHORT).show();
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.fund_acct_layout);
        dialog.setTitle("Select a funding medium");

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        dialog.getWindow().setAttributes(lp);

        final CurrencyEditText currencyEditText = dialog.findViewById(R.id.etCurrency2);

                Button btnOk = dialog.findViewById(R.id.ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String gateway = "Paystack";
                    double d = currencyEditText.getCurrencyDouble();
//                            String pay = currencyEditText.getCurrencyText().toString();
//                            double checkMoney = Double.parseDouble(pay);
                    int amount1 = (int) d;
                    amount1 = amount1 * koboToNaira;
                    Intent money = new Intent(DashboardActivity.this, Payment.class);
                    money.putExtra("Amount", amount1 );
                    money.putExtra("gateway", gateway);
                    startActivity(money);
                    Log.d("seg", ""+amount1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        Button btnCloseLayout = dialog.findViewById(R.id.btnCloseLayout);
        btnCloseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}
