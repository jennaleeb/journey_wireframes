package com.uoft.journey.ui.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.uoft.journey.R;
import com.uoft.journey.data.ServerAccess;
import com.uoft.journey.entity.AccelerometerData;
import com.uoft.journey.entity.Trial;
import com.uoft.journey.service.DataService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AssessmentDetailActivity extends AppCompatActivity {

    private Trial mTrial;
    private LineChart mGraph;
    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_assessment_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        int trialId = extras.getInt("trialId");
        mUsername = extras.getString("user");
        getSupportActionBar().setTitle(R.string.assessment);
        mTrial = DataService.getTrial(this, trialId, mUsername);
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
        ((TextView)findViewById(R.id.text_detail_date)).setText(df.format(mTrial.getStartTime()));
        ((TextView)findViewById(R.id.text_detail_1_val)).setText(String.format("%d", mTrial.getDuration() / 1000));
        ((TextView)findViewById(R.id.text_detail_2_val)).setText(String.format("%d", mTrial.getNumberOfSteps()));
        ((TextView)findViewById(R.id.text_detail_3_val)).setText(String.format("%.0f", mTrial.getMeanStrideTime()));

        TextView stv = (TextView) findViewById(R.id.text_detail_4_val);
        stv.setText(String.format("%.1f", mTrial.getCoeffOfVar()));

        if( mTrial.getCoeffOfVar() <= 3.0f) {
            stv.setBackgroundResource(R.drawable.round_text_green);
        }
        else if( mTrial.getCoeffOfVar() <= 6.0f) {
            stv.setBackgroundResource(R.drawable.round_text_yellow);
        }
        else {
            stv.setBackgroundResource(R.drawable.round_text_red);
        }

        //((TextView)findViewById(R.id.text_detail_4_val)).setText(String.format("%.1fms", mTrial.getStandardDev()));
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

                // Show any pauses
                if(mTrial.getPauseTimes() != null) {
                    XAxis xAxis = mGraph.getXAxis();
                    for(int j=0; j<mTrial.getPauseTimes().size(); j++) {
                        if(data.getElapsedTimestamps()[i] == mTrial.getPauseTimes().get(j)[0] ||
                                (i < data.getElapsedTimestamps().length - 1 && data.getElapsedTimestamps()[i] < mTrial.getPauseTimes().get(j)[0] && data.getElapsedTimestamps()[i+1] > mTrial.getPauseTimes().get(j)[0] )) {

                            LimitLine ll = new LimitLine(i, "Pause");
                            ll.setLineColor(Color.YELLOW);
                            ll.setLineWidth(2f);
                            ll.setTextColor(Color.BLACK);
                            ll.setTextSize(10f);
                            ll.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                            xAxis.addLimitLine(ll);
                        }

                        if(data.getElapsedTimestamps()[i] == mTrial.getPauseTimes().get(j)[1] ||
                                (i < data.getElapsedTimestamps().length - 1 && data.getElapsedTimestamps()[i] < mTrial.getPauseTimes().get(j)[1] && data.getElapsedTimestamps()[i+1] > mTrial.getPauseTimes().get(j)[1] )) {

                            LimitLine ll2 = new LimitLine(i);
                            ll2.setLineColor(Color.YELLOW);
                            ll2.setLineWidth(2f);
                            xAxis.addLimitLine(ll2);
                        }
                    }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Menu with delete icon.
        getMenuInflater().inflate(R.menu.menu_assessment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Handle menu item clicks, only one option in this case
        if (id == R.id.delete) {
            DeleteTask task = new DeleteTask(mTrial.getTrialId(), mUsername, this);
            task.execute();
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteComplete(boolean success) {
        if(success) {
            Toast.makeText(this, "Assessment deleted", Toast.LENGTH_SHORT).show();
            this.finish();
        }
        else {
            Toast.makeText(this, "Failed to delete assessment, try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private class DeleteTask extends AsyncTask<Void, Void, Boolean> {
        private int trialId;
        private String username;
        private Context context;

        public DeleteTask(int tId, String user, Context ctx) {
            trialId = tId;
            username = user;
            context = ctx;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return DataService.deleteTrial(context, trialId, username);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            deleteComplete(success);
            super.onPostExecute(success);
        }
    }
}
