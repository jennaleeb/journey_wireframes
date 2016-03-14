package com.uoft.journey.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
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
    private ImageView mCircleVar1;
    private ImageView mCircleVar2;
    private ImageView mCircleVar3;
    private ImageView mCircleVar4;
    private ImageView mCircleVar5;


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
        mCircleVar1 = (ImageView)view.findViewById(R.id.circle_var_1);
        mCircleVar2 = (ImageView)view.findViewById(R.id.circle_var_2);
        mCircleVar3 = (ImageView)view.findViewById(R.id.circle_var_3);
        mCircleVar4 = (ImageView)view.findViewById(R.id.circle_var_4);
        mCircleVar5 = (ImageView)view.findViewById(R.id.circle_var_5);
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

        YAxis leftAxis = mVariationChart.getAxisLeft();

        LimitLine ll = new LimitLine(3f, "Target Max Variation");
        ll.setLineColor(Color.RED);
        ll.setLineWidth(2f);
        ll.setTextColor(Color.BLACK);
        ll.setTextSize(10f);

        leftAxis.addLimitLine(ll);

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
                xVals.add(df.format(trial.getStartTime()));
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
            data.setHighlightEnabled(false);

            mVariationChart.setData(data);
            mVariationChart.notifyDataSetChanged();
            mVariationChart.invalidate();

            showRecentColours();

            // Mean stride time
            BarData data1 = new BarData(xVals);
            data1.setValueTextColor(Color.rgb(231, 76, 60));

            BarDataSet setMean = new BarDataSet(yValsMean, "Average Stride Time");
            setMean.setBarSpacePercent(5);
            setMean.setColor(Color.rgb(231, 76, 60));
            data1.addDataSet(setMean);
            data1.setHighlightEnabled(false);

            mMeanChart.setData(data1);
            mMeanChart.notifyDataSetChanged();
            mMeanChart.invalidate();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void reloadTrials() {
        populateTrials();
        plotCharts();
    }
}
