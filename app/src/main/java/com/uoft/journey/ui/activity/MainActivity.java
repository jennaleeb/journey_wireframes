package com.uoft.journey.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.baasbox.android.BaasUser;
import com.uoft.journey.Journey;
import com.uoft.journey.R;

public class MainActivity extends AppCompatActivity {

    CountDownTimer mTimer;
    Journey mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mApp=(Journey)getApplicationContext();

        if (BaasUser.current() == null){
            startLoginScreen();
        }
        else {
            ImageView iconArrow = (ImageView) findViewById(R.id.icon_arrow);
            iconArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startUserHomeScreen();
                }
            });

            mTimer = new CountDownTimer(2000, 1000) {

                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    startUserHomeScreen();
                }
            };

            mTimer.start();
        }
    }

    private void startUserHomeScreen() {
        mTimer.cancel();
        BaasUser currUser = BaasUser.current();
        mApp.setUsername(currUser.getName());
        Intent intent;

        if(currUser.getRoles().contains("clinician")){
            mApp.setType("clinician");
            intent = new Intent(MainActivity.this, PatientListActivity.class);
        }else{
            mApp.setType("patient");
            intent = new Intent(MainActivity.this, PatientMainActivity.class);
            intent.putExtra("patient", mApp.getUsername());
        }

        // Prevent navigation back to here
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void startLoginScreen(){
        Intent intent = new Intent(this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onPause(){
        super.onPause();

    }

}
