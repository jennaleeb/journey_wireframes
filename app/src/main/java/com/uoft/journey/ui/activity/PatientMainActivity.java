package com.uoft.journey.ui.activity;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.uoft.journey.Journey;
import com.uoft.journey.R;
import com.uoft.journey.data.DownloadTrials;
import com.uoft.journey.data.LocalDatabaseAccess;
import com.uoft.journey.entity.Patient;
import com.uoft.journey.entity.Trial;
import com.uoft.journey.ui.adapter.MainPagerAdapter;
import com.uoft.journey.ui.custom.ViewPagerFixed;

import java.util.ArrayList;

public class PatientMainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private Patient mPatient;
    private MainPagerAdapter mPagerAdapter;
    private Toolbar mToolbar;
    private Journey mApp;
    private ArrayList<Trial> mTrials;
    private int mActivePointerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_main);

        Intent intent = getIntent();
        String name = intent.getStringExtra("patient");
        System.out.println("PATIENTMAINACTIIVTY THE name IS: " + name);

        mPatient = LocalDatabaseAccess.getTestUser(this, name);
        /*mApp = ((Journey)getApplicationContext());
        mApp.setUserID(mPatient.getID());*/

        //set title of patient page to patient's name
        setTitle(mPatient.getactualName());
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Set up the viewpager which shows the tabs
        ViewPagerFixed viewPager = (ViewPagerFixed)findViewById(R.id.viewpager);
        viewPager.addOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(3);
        System.out.println("PATIENTMAINACTIIVTY THE USERNAME IS: " + mPatient.getuserName());

        mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), this, mPatient.getID(), mPatient.getuserName());
        viewPager.setAdapter(mPagerAdapter);
        TabLayout tab = (TabLayout)findViewById(R.id.tabs);
        tab.setTabGravity(TabLayout.GRAVITY_FILL);
        tab.setTabMode(TabLayout.MODE_FIXED);
        tab.setupWithViewPager(viewPager);

        DownloadTrials task = new DownloadTrials(getApplicationContext(), mPatient.getuserName(), mPatient.getID(), mPagerAdapter);
        task.execute();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        // Reload the trial data in fragments
        mPagerAdapter.trialsLoaded(true);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // Do nothing
    }

    @Override
    public void onPageSelected(int position) {
        mPagerAdapter.pageChange();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // Do nothing
    }

    @Override
    public void onPause(){
        super.onPause();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Menu with options icon.
        getMenuInflater().inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        View options = findViewById(R.id.options);
        // Handle menu item clicks, only one option in this case
        if (id == R.id.options) {
            PopupMenu dropDownMenu = new PopupMenu(getApplicationContext(), options);
            dropDownMenu.getMenuInflater().inflate(R.menu.logout, dropDownMenu.getMenu());

            dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    // Logout stuff
                    if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                        ((ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE))
                                .clearApplicationUserData(); // note: it has a return value!

                    } else {

                    }

                    return true;
                }
            });
            dropDownMenu.show();
        }

        return super.onOptionsItemSelected(item);
    }
}
