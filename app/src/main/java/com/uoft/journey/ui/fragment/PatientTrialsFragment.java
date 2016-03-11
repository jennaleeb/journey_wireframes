package com.uoft.journey.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uoft.journey.R;
import com.uoft.journey.entity.Trial;
import com.uoft.journey.service.DataService;
import com.uoft.journey.ui.activity.AssessmentDetailActivity;
import com.uoft.journey.ui.activity.MeasureActivity;
import com.uoft.journey.ui.adapter.PatientTrialsListAdapter;

import java.util.ArrayList;

/**
 * Fragment for displaying list of assessments
 */
public class PatientTrialsFragment extends Fragment implements PatientTrialsListAdapter.OnItemClickListener, View.OnClickListener {
    private static final String ARG_USER_ID = "userId";
    private int mUserId;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Context mContext;
    private ArrayList<Trial> mTrials;

    public PatientTrialsFragment() {
        // Required empty public constructor
    }

    public static PatientTrialsFragment newInstance(int param1) {
        PatientTrialsFragment fragment = new PatientTrialsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_USER_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserId = getArguments().getInt(ARG_USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_patient_trials, container, false);
        mContext = view.getContext();
        setRetainInstance(true);
        FloatingActionButton newBtn = (FloatingActionButton)view.findViewById(R.id.button_new);
        newBtn.setOnClickListener(this);

        // List contained in RecyclerView
        mRecyclerView = (RecyclerView) view.findViewById(R.id.trial_list_view);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mTrials = DataService.getTrialsForUser(view.getContext(), mUserId);
        mAdapter = new PatientTrialsListAdapter(mTrials, this);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    // When a trial is clicked
    @Override
    public void onItemClick(int trialId) {
        Intent intent = new Intent(mContext, AssessmentDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("trialId", trialId);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    // New assessment button clicked
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_new:
                Intent intent = new Intent(mContext, MeasureActivity.class); // **** Standard Version ****
                //Intent intent = new Intent(mContext, DebugMeasureActivity.class); // **** For Debug Version ****
                Bundle bundle = new Bundle();
                bundle.putInt("userId", mUserId);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }

    public void reloadTrials() {
        mTrials.clear();
        mTrials.addAll(DataService.getTrialsForUser(mContext, mUserId));
        mAdapter.notifyDataSetChanged();
    }
}
