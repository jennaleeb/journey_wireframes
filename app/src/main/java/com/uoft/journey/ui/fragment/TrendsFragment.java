package com.uoft.journey.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.uoft.journey.R;
import com.uoft.journey.entity.Trial;
import com.uoft.journey.service.DataService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
* Fragment to show trends
 */
public class TrendsFragment extends Fragment {
    private static final String ARG_USER_ID = "userId";
    private int mUserId;
    private Context mContext;
    private BarChart mVariationChart;
    private BarChart mMeanChart;
    private ArrayList<Trial> mTrials;
    private String mUsername;

    public TrendsFragment() {
        // Required empty public constructor
    }
    public static TrendsFragment newInstance(int userId, String username) {
        TrendsFragment fragment = new TrendsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_USER_ID, userId);
        args.putString("username", username);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserId = getArguments().getInt(ARG_USER_ID);
            mUsername = getArguments().getString("username");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trends, container, false);
        mContext = view.getContext();
        setRetainInstance(true);
        mVariationChart = (BarChart)view.findViewById(R.id.chart_variation);
        mMeanChart = (BarChart)view.findViewById(R.id.chart_mean);
        populateTrials();
        setupChart();
        plotCharts();
        return view;
    }

    public void populateTrials() {
        mTrials = DataService.getTrialsForUser(mContext, mUserId, mUsername);
    }

    public void setupChart() {
        mVariationChart.setDragEnabled(true);
        mVariationChart.setScaleEnabled(true);
        mVariationChart.setDrawGridBackground(false);
        mVariationChart.setPinchZoom(true);
        mVariationChart.setDescription("");

        mMeanChart.setDragEnabled(true);
        mMeanChart.setScaleEnabled(true);
        mMeanChart.setDrawGridBackground(false);
        mMeanChart.setPinchZoom(true);
        mMeanChart.setDescription("");
    }

    public void plotCharts() {
        try {
            if(mTrials == null)
                return;

            mVariationChart.clear();
            mMeanChart.clear();

            ArrayList<String> xVals = new ArrayList<String>();
            ArrayList<BarEntry> yValsVar = new ArrayList<BarEntry>();
            ArrayList<BarEntry> yValsMean = new ArrayList<BarEntry>();

            for (int i= mTrials.size()-1 ; i >=0; i--) {
                Trial trial = mTrials.get(i);
                DateFormat df = new SimpleDateFormat("dd MMM", Locale.CANADA);
                xVals.add(trial.getTrialId() + "(" + df.format(trial.getStartTime()) + ")");
                yValsVar.add(new BarEntry(trial.getCoeffOfVar(), Math.abs(i - mTrials.size()+1)));
                yValsMean.add(new BarEntry(trial.getMeanStrideTime(), Math.abs(i-mTrials.size()+1)));
            }

            // Stride variation
            BarData data = new BarData(xVals);
            data.setValueTextColor(Color.rgb(39, 174, 96));
            BarDataSet setVar = new BarDataSet(yValsVar, "Stride Variation");
            setVar.setBarSpacePercent(5);
            setVar.setColor(Color.rgb(39, 174, 96));
            data.addDataSet(setVar);

            mVariationChart.setData(data);
            mVariationChart.notifyDataSetChanged();
            mVariationChart.invalidate();

            // Mean stride time
            BarData data1 = new BarData(xVals);
            data1.setValueTextColor(Color.rgb(231, 76, 60));

            BarDataSet setMean = new BarDataSet(yValsMean, "Average Stride Time");
            setMean.setBarSpacePercent(5);
            setMean.setColor(Color.rgb(231, 76, 60));
            data1.addDataSet(setMean);

            mMeanChart.setData(data1);
            mMeanChart.notifyDataSetChanged();
            mMeanChart.invalidate();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void reloadTrials() {
        populateTrials();
        plotCharts();
    }
}
