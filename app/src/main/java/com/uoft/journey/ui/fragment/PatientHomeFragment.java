package com.uoft.journey.ui.fragment;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uoft.journey.R;
import com.uoft.journey.entity.Trial;

import java.util.ArrayList;

/**
 * Home page for patients
 */
public class PatientHomeFragment extends Fragment {
    private static final String ARG_PARAM_USER = "username";
    private String mUsername;
    private ArrayList<Trial> mTrials = new ArrayList<>();
    private TextView mTextRecent;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_patient_home, container, false);
        setRetainInstance(true);
        mTextRecent = (TextView)view.findViewById(R.id.text_recent);
        return view;
    }

    private void populateViewData() {
        if(mTrials == null || mTrials.size() == 0)
            return;

        Trial mostRecent = mTrials.get(mTrials.size() - 1);
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

        mTextRecent.invalidate();
    }


    public void reloadTrials(ArrayList<Trial> trials) {
        mTrials.clear();
        mTrials.addAll(trials);
        populateViewData();
    }
}
