package com.uoft.journey.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.uoft.journey.R;
import com.uoft.journey.ui.fragment.PatientTrialsFragment;
import com.uoft.journey.ui.fragment.TrendsFragment;

/**
 * Adapter for tab pages
 */
public class MainPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private PatientTrialsFragment mPatientTrialsFragment;
    private TrendsFragment mTrendsFragment;
    private int mUserId;
    private String mUsername;

    public MainPagerAdapter(FragmentManager fm, Context context, int userId, String username) {
        super(fm);
        mContext = context;
        mUserId = userId;
        mUsername = username;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                mPatientTrialsFragment = PatientTrialsFragment.newInstance(mUserId, mUsername);
                return mPatientTrialsFragment;
            case 1:
                mTrendsFragment = TrendsFragment.newInstance(mUserId, mUsername);
                return mTrendsFragment;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Get the tab titles
        switch(position) {
            case 0:
                return mContext.getString(R.string.assessments);
            case 1:
                return mContext.getString(R.string.trends);
        }

        return "";
    }

    public void pageChange(int position) {
        if(mPatientTrialsFragment != null && position == 0) {
            mPatientTrialsFragment.reloadTrials();
        }
        if(mTrendsFragment != null && position == 1) {
            mTrendsFragment.reloadTrials();
        }
    }
}
