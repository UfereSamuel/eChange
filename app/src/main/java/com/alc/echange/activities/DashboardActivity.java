package com.alc.echange.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.KeyguardManager;
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
import android.widget.Toast;

import com.alc.echange.R;
import com.google.android.material.navigation.NavigationView;


import java.util.Objects;

import static com.alc.echange.activities.LoginActivity.DASHBOARD_INTENT;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = "DashboardActivity";
    private ConstraintLayout mMainContainer;
    DrawerLayout drawer;
    NavigationView navigationView;

    //Phone lock variables
    private static final int INTENT_AUTHENTICATE = 1;
    private Boolean mIsStopped, mCheckPassCode;

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

        boolean mIntentReceived = getIntent().getBooleanExtra(DASHBOARD_INTENT, false);
        if (mIntentReceived) {
            //Prompt user to unlock screen
            promptUnlockScreen();
        }

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open,
        R.string.navigation_drawer_close);
        if (drawer != null) {
        drawer.addDrawerListener(toggle);
        }
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
        navigationView.setNavigationItemSelectedListener(this);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    public void requestCash(View view) {
        Intent intent = new Intent(this, RequestCashActivity.class);
        startActivity(intent);
    }
}
