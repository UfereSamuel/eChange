package com.alc.echange.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.alc.echange.R;
import com.felix.bottomnavygation.BottomNav;
import com.felix.bottomnavygation.ItemNav;

import java.util.Objects;

public class DashboardActivity extends AppCompatActivity {
    private BottomNav bottomNav;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Toolbar toolbar = findViewById(R.id.bottom_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);


        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.addItemNav(new ItemNav(this, R.drawable.ic_home, "Overview").addColorAtive(R.color.colorYello).addColorInative(R.color.colorWhite));
        bottomNav.addItemNav(new ItemNav(this, R.drawable.ic_history, "History").addColorAtive(R.color.colorYello).addColorInative(R.color.colorWhite));
        bottomNav.addItemNav(new ItemNav(this, R.drawable.ic_fundaccount, "Fund Account").addColorAtive(R.color.colorYello).addColorInative(R.color.colorWhite));
        bottomNav.addItemNav(new ItemNav(this, R.drawable.ic_more, "More").addColorAtive(R.color.colorYello).addColorInative(R.color.colorWhite));
        bottomNav.build();

        bottomNav.setTabSelectedListener(new BottomNav.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int i) {
                Toast.makeText(DashboardActivity.this, "Click position " + i, Toast.LENGTH_SHORT).show();
                if (i == 0) {
//                    intent = new Intent(NewsOne.this, NewsOne.class);
//                    startActivity(intent);
                    return;
                }
                if (i == 1) {
//                    intent = new Intent(NewsOne.this, Team.class);
//                    startActivity(intent);
                    return;
                }
                if (i == 2) {
//                    intent = new Intent(NewsOne.this, LeagueActivity.class);
//                    startActivity(intent);
                    return;
                }
                if (i == 3) {
//                    intent = new Intent(DashboardActivity.this, Team.class);
//                    startActivity(intent);
                    return;
                }
            }

            @Override
            public void onTabLongSelected(int i) {
                Toast.makeText(DashboardActivity.this, "Long position " + i, Toast.LENGTH_SHORT).show();
            }
        });


    }


}
