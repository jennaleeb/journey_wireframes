package com.uoft.journey.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.baasbox.android.BaasUser;
import com.uoft.journey.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (BaasUser.current() == null){
            startLoginScreen();
            return;
        }
        else {
            ImageView iconArrow = (ImageView) findViewById(R.id.icon_arrow);
            iconArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startUserHomeScreen();
                }
            });

            new CountDownTimer(2000, 1000) {

                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    startUserHomeScreen();
                }
            }.start();
        }
    }

    private void startUserHomeScreen() {
        Intent intent = new Intent(MainActivity.this, PatientMainActivity.class);
        startActivity(intent);
    }

    private void startLoginScreen(){
        Intent intent = new Intent(this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}
