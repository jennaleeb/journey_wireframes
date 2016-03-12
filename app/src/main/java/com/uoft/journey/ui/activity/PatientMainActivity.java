package com.uoft.journey.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.uoft.journey.Journey;
import com.uoft.journey.R;
import com.uoft.journey.data.DownloadTrials;
import com.uoft.journey.data.LocalDatabaseAccess;
import com.uoft.journey.data.ServerAccess;
import com.uoft.journey.entity.Patient;
import com.uoft.journey.service.DataService;
import com.uoft.journey.ui.adapter.MainPagerAdapter;

public class PatientMainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private Patient mPatient;
    private MainPagerAdapter mPagerAdapter;
    private Toolbar mToolbar;
    private Journey mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_main);

        Intent intent = getIntent();
        String name = intent.getStringExtra("patient");
        mPatient = LocalDatabaseAccess.getTestUser(this, name);
        mApp = ((Journey)getApplicationContext());
        mApp.setUserID(mPatient.getID());


        //set title of patient page to patient's name
        setTitle(mPatient.getName());
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Set up the viewpager which shows the tabs
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.addOnPageChangeListener(this);
        mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), this, mPatient.getID(), mPatient.getName());
        viewPager.setAdapter(mPagerAdapter);
        TabLayout tab = (TabLayout)findViewById(R.id.tabs);
        tab.setTabGravity(TabLayout.GRAVITY_FILL);
        tab.setTabMode(TabLayout.MODE_FIXED);
        tab.setupWithViewPager(viewPager);

        DownloadTrials task = new DownloadTrials(getApplicationContext(), mPatient.getName(), mPatient.getID(), mPagerAdapter);
        task.execute();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        // Reload the trial data in fragments
        mPagerAdapter.pageChange(0);
        mPagerAdapter.pageChange(1);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // Do nothing
    }

    @Override
    public void onPageSelected(int position) {
        // WHen tab changed
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // Do nothing
    }
}
