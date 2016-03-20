package com.uoft.journey.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;
import android.widget.Toast;

import com.uoft.journey.R;
import com.uoft.journey.entity.Trial;
import com.uoft.journey.service.DataService;
import com.uoft.journey.ui.fragment.PatientTrialsFragment;
import com.uoft.journey.ui.fragment.TrendsFragment;

import java.util.ArrayList;

/**
 * Adapter for tab pages
 */
public class MainPagerAdapter extends FragmentStatePagerAdapter {
    private Context mContext;
    private Fragment[] mFragments = new Fragment[2];
    private int mUserId;
    private String mUsername;
    private ArrayList<Trial> mTrials;

    public MainPagerAdapter(FragmentManager fm, Context context, int userId, String username) {
        super(fm);
        mContext = context;
        mUserId = userId;
        mUsername = username;
        mTrials = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                mFragments[0] = TrendsFragment.newInstance(mUsername);
                return mFragments[0];
            case 1:
                mFragments[1] = PatientTrialsFragment.newInstance(mUserId, mUsername);
                return mFragments[1];
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
                return mContext.getString(R.string.home);
            case 1:
                return mContext.getString(R.string.assessments);
        }

        return "";
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        switch(position) {
            case 0:
                TrendsFragment trendsFragment = (TrendsFragment) super.instantiateItem(container, position);
                mFragments[position] = trendsFragment;
                return trendsFragment;
            case 1:
                PatientTrialsFragment trialsFragment = (PatientTrialsFragment) super.instantiateItem(container, position);
                mFragments[position] = trialsFragment;
                return trialsFragment;
        }
        return super.instantiateItem(container, position);
    }

    public void pageChange() {

        // Reload the data
        if( mFragments[0] != null)
            ((TrendsFragment)mFragments[0]).reloadTrials(mTrials);
        if( mFragments[1] != null)
            ((PatientTrialsFragment)mFragments[1]).reloadTrials(mTrials);
    }

    public void trialsLoaded(Boolean success) {
        if(!success)
            Toast.makeText(mContext, "Failed to update trials from server", Toast.LENGTH_SHORT).show();

        mTrials = DataService.getTrialsForUser(mContext, mUsername);
        pageChange();
    }
}
