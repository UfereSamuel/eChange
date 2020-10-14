package com.alc.echange.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.alc.echange.R;

public class SplashScreenActivity extends AppCompatActivity {

    public static final int TIMER = 5000;
    Animation mTopAnim, mBottomAnim;
    ImageView mImageCheck, mImageCurrency;
    TextView mLogo, mSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        mTopAnim = AnimationUtils.loadAnimation(this, R.anim.splash_top_animation);
        mTopAnim.setDuration(2500);
        mBottomAnim = AnimationUtils.loadAnimation(this, R.anim.splash_bottom_animation);
        mBottomAnim.setDuration(2500);

        mImageCheck = findViewById(R.id.image_check);
        mImageCurrency = findViewById(R.id.image_currency);
        mLogo = findViewById(R.id.text_logo);
        mSlogan = findViewById(R.id.text_slogan);

        mLogo.setAnimation(mTopAnim);
        mImageCheck.setAnimation(mTopAnim);
        mSlogan.setAnimation(mTopAnim);
        mImageCurrency.setAnimation(mBottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
//                Toast.makeText(SplashScreenActivity.this, "LoginActivity", Toast.LENGTH_SHORT).show();
            }
        }, TIMER);
    }
}
