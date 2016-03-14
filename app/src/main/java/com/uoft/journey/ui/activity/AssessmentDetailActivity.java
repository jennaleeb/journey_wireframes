package com.uoft.journey.ui.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.uoft.journey.R;
import com.uoft.journey.entity.AccelerometerData;
import com.uoft.journey.entity.Trial;
import com.uoft.journey.service.DataService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AssessmentDetailActivity extends AppCompatActivity {

    private Trial mTrial;
    private LineChart mGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_detail);

        Bundle extras = getIntent().getExtras();
        int trialId = extras.getInt("trialId");
        String username = extras.getString("user");

        mTrial = DataService.getTrial(this, trialId, username);
        mGraph = (LineChart)findViewById(R.id.detail_graph);
        setupGraph();

        if(mTrial != null) {
            populateTrial();
            plotGraph();
        }
        else {
            Toast.makeText(this, "Failed to load assessment", Toast.LENGTH_SHORT).show();
        }
    }

    private void populateTrial() {
        DateFormat df = new SimpleDateFormat("dd MMM yyyy - hh:mm a", Locale.CANADA);
        ((TextView)findViewById(R.id.text_detail_title)).setText(String.format("Assessment %d Details", mTrial.getTrialId()));
        ((TextView)findViewById(R.id.text_detail_date)).setText(df.format(mTrial.getStartTime()));
        ((TextView)findViewById(R.id.text_detail_1_val)).setText(String.format("%ds", mTrial.getDuration() / 1000));
        ((TextView)findViewById(R.id.text_detail_2_val)).setText(String.format("%d", mTrial.getNumberOfSteps()));
        ((TextView)findViewById(R.id.text_detail_3_val)).setText(String.format("%.1fms", mTrial.getMeanStrideTime()));
        ((TextView)findViewById(R.id.text_detail_4_val)).setText(String.format("%.1fms", mTrial.getStandardDev()));
        ((TextView)findViewById(R.id.text_detail_5_val)).setText(String.format("%.2f", mTrial.getCoeffOfVar()));
    }

    private void setupGraph() {
        mGraph.setDragEnabled(true);
        mGraph.setScaleEnabled(true);
        mGraph.setDrawGridBackground(false);
        mGraph.setPinchZoom(true);
        LineData data = new LineData();
        data.setValueTextColor(Color.RED);
        mGraph.setData(data);
        mGraph.setVisibleXRange(0, 100);
        mGraph.setVisibleYRangeMaximum(100, YAxis.AxisDependency.LEFT);

        // Y values
        LineDataSet setX = new LineDataSet(null, "movement");
        setX.setLineWidth(2.5f);
        setX.setDrawCircles(false);
        setX.setColor(Color.rgb(240, 99, 99));
        data.addDataSet(setX);

        // Steps
        LineDataSet setY = new LineDataSet(null, "steps");
        setY.setLineWidth(2.5f);
        setY.setDrawCircles(false);
        setY.setColor(Color.rgb(99, 240, 99));
        data.addDataSet(setY);
    }

    private void plotGraph() {
        try {

            AccelerometerData data = mTrial.getTrialData();
            mGraph.clear();
            setupGraph();
            LineData graphData = mGraph.getData();

            int nextStep = 0;
            for(int i=0; i< data.getProcessedY().length; i++) {
                // Add timestamp to X-axis and line series values
                graphData.addXValue(data.getElapsedTimestamps()[i] + "");
                graphData.addEntry(new Entry(data.getProcessedY()[i], i), 0); // The processed Y data

                boolean added = false;
                if(mTrial.getStepTimes() != null) {
                    // Show the calculated maxima points
                    while (nextStep < mTrial.getStepTimes().length) {
                        if (mTrial.getStepTimes()[nextStep] == data.getElapsedTimestamps()[i]) {
                            // Step found, add a 10 point
                            graphData.addEntry(new Entry(10, i), 1);
                            nextStep++;
                            added = true;
                            break;
                        }

                        if (mTrial.getStepTimes()[nextStep] > data.getElapsedTimestamps()[i]) {
                            break;
                        }

                        nextStep++;
                    }
                }
                // Not a step so add zero point
                if(!added) {
                    graphData.addEntry(new Entry(0, i), 1);
                }
            }

            mGraph.notifyDataSetChanged();
            mGraph.invalidate();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause(){
        super.onPause();

    }

}
