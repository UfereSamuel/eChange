package com.alc.echange.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.alc.echange.R;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = findViewById(R.id.bottom_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                drawer.closeDrawer(GravityCompat.START);
                displayToast(getString(R.string.home));
                return true;
            case R.id.request_cash:
                drawer.closeDrawer(GravityCompat.START);
                displayToast(getString(R.string.request_cas));
                return true;
            case R.id.send_cash:
                drawer.closeDrawer(GravityCompat.START);
                displayToast(getString(R.string.send_cash));
                return true;
            case R.id.buy_airtime:
                drawer.closeDrawer(GravityCompat.START);
                displayToast(getString(R.string.buy_airtime));
                return true;
            case R.id.buy_data:
                drawer.closeDrawer(GravityCompat.START);
                displayToast(getString(R.string.buy_data));
                return true;
            case R.id.pay_with_qr:
                drawer.closeDrawer(GravityCompat.START);
                displayToast(getString(R.string.pay_with_qr));
                return true;
            default:
                return false;
        }

    }

    public void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void payWithQr(View view) {
        displayToast("Pay with QR here");
    }

    public void buyData(View view) {
        displayToast("Buy Data here");
    }

    public void buyAirtime(View view) {
        displayToast("Buy Airtime here");
    }

    public void sendCash(View view) {
        displayToast("Send cash here");
    }

    public void requestCash(View view) {
        displayToast("Request cash here");
    }

    public void fundAccount(View view) {
        displayToast("Fund account here");
    }
}
