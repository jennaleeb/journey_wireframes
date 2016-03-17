package com.uoft.journey.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uoft.journey.R;
import com.uoft.journey.entity.Trial;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Home page for patients
 */
public class PatientHomeFragment extends Fragment {
    private static final String ARG_PARAM_USER = "username";
    private String mUsername;
    private ArrayList<Trial> mTrials = new ArrayList<>();
    private TextView mLastDate;
    private TextView mTextRecent;
    private ImageView mCircleVar1;
    private ImageView mCircleVar2;
    private ImageView mCircleVar3;
    private ImageView mCircleVar4;
    private ImageView mCircleVar5;
    private RelativeLayout mLayoutHome;
    private ProgressBar mProgress;
    private Boolean mIsLoading;

    public PatientHomeFragment() {
        // Required empty public constructor
    }

    public static PatientHomeFragment newInstance(String param1) {
        PatientHomeFragment fragment = new PatientHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_USER, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUsername = getArguments().getString(ARG_PARAM_USER);
        }
        mIsLoading = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_patient_home, container, false);
        setRetainInstance(true);
        mLayoutHome = (RelativeLayout)view.findViewById(R.id.layout_home);
        mProgress = (ProgressBar)view.findViewById(R.id.pb_home);

        if(mIsLoading) {
            mLayoutHome.setVisibility(View.INVISIBLE);
            mProgress.setVisibility(View.VISIBLE);
        }else {
            mLayoutHome.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.INVISIBLE);
        }

        mTextRecent = (TextView)view.findViewById(R.id.text_recent);
        mLastDate = (TextView)view.findViewById(R.id.text_last_date);
        mCircleVar1 = (ImageView)view.findViewById(R.id.circle_var_1);
        mCircleVar2 = (ImageView)view.findViewById(R.id.circle_var_2);
        mCircleVar3 = (ImageView)view.findViewById(R.id.circle_var_3);
        mCircleVar4 = (ImageView)view.findViewById(R.id.circle_var_4);
        mCircleVar5 = (ImageView)view.findViewById(R.id.circle_var_5);
        return view;
    }

    private void populateViewData() {
        if(mTrials == null || mTrials.size() == 0) {
            mLastDate.setText("NO ASSESSMENTS");
            return;
        }

        Trial mostRecent = mTrials.get(0);
        mTextRecent.setText(String.format("%.1f%%", mostRecent.getCoeffOfVar()));
        if(mostRecent.getCoeffOfVar() <= 3.0f) {
            mTextRecent.setBackgroundResource(R.drawable.round_text_green);
        }
        else if(mostRecent.getCoeffOfVar() <= 6.0f) {
            mTextRecent.setBackgroundResource(R.drawable.round_text_yellow);
        }
        else {
            mTextRecent.setBackgroundResource(R.drawable.round_text_red);
        }

        // Show the latest trial date, or TODAY/YESTERDAY
        Calendar trialTime = Calendar.getInstance();
        trialTime.setTime(mTrials.get(0).getStartTime());
        Calendar now = Calendar.getInstance();
        if(now.get(Calendar.DATE) == trialTime.get(Calendar.DATE) ){
            mLastDate.setText("TODAY");
        }else if(now.get(Calendar.DATE) - trialTime.get(Calendar.DATE) == 1 ) {
            mLastDate.setText("YESTERDAY");
        }else {
            SimpleDateFormat df = new SimpleDateFormat("dd MMM", Locale.CANADA);
            mLastDate.setText(df.format(mTrials.get(0).getStartTime()));
        }

        showRecentColours();
    }

    private void showRecentColours() {
        if(mTrials != null) {
            if(mTrials.size() >= 5) {
                setColour(mTrials.get(4), mCircleVar1);
            }
            else
            {
                mCircleVar1.setVisibility(View.INVISIBLE);
            }

            if(mTrials.size() >= 4) {
                setColour(mTrials.get(3), mCircleVar2);
            }
            else
            {
                mCircleVar2.setVisibility(View.INVISIBLE);
            }

            if(mTrials.size() >= 3) {
                setColour(mTrials.get(2), mCircleVar3);
            }
            else
            {
                mCircleVar3.setVisibility(View.INVISIBLE);
            }

            if(mTrials.size() >= 2) {
                setColour(mTrials.get(1), mCircleVar4);
            }
            else
            {
                mCircleVar4.setVisibility(View.INVISIBLE);
            }

            if(mTrials.size() >= 1) {
                setColour(mTrials.get(0), mCircleVar5);
            }
            else
            {
                mCircleVar5.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setColour(Trial trial, ImageView circle) {
        if(trial.getCoeffOfVar() <= 3.0) {
            circle.setImageResource(R.drawable.green);
        }
        else if(trial.getCoeffOfVar() <= 6.0) {
            circle.setImageResource(R.drawable.yellow);
        }
        else {
            circle.setImageResource(R.drawable.red);
        }
        circle.setVisibility(View.VISIBLE);
    }

    public void reloadTrials(ArrayList<Trial> trials) {
        mTrials.clear();
        mTrials.addAll(trials);

        mIsLoading = false;
        mLayoutHome.setVisibility(View.VISIBLE);
        mProgress.setVisibility(View.INVISIBLE);

        populateViewData();
    }
}
