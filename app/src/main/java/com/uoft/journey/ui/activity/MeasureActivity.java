package com.uoft.journey.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.uoft.journey.R;
import com.uoft.journey.entity.AccelerometerData;
import com.uoft.journey.service.SensorService;

public class MeasureActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnStart;
    private Button mBtnStop;
    private LineChart mGraph;
    private IntentFilter mIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);
        mBtnStart = (Button)findViewById(R.id.button_start);
        mBtnStart.setOnClickListener(this);
        mBtnStop = (Button)findViewById(R.id.button_stop);
        if(!SensorService.isRunning) {
            mBtnStop.setEnabled(false);
            mBtnStop.setVisibility(View.INVISIBLE);
        }
        mBtnStop.setOnClickListener(this);
        mGraph = (LineChart)findViewById(R.id.graph);
        setupGraph();
        mIntentFilter = new IntentFilter(SensorService.ACTION_ACCELEROMETER_DATA);
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

        // X
        LineDataSet setX = new LineDataSet(null, "x");
        setX.setLineWidth(2.5f);
        setX.setDrawCircles(false);
        setX.setColor(Color.rgb(240, 99, 99));
        data.addDataSet(setX);

        // Y
        LineDataSet setY = new LineDataSet(null, "y");
        setY.setLineWidth(2.5f);
        setY.setDrawCircles(false);
        setY.setColor(Color.rgb(99, 240, 99));
        data.addDataSet(setY);

        // Z
        LineDataSet setZ = new LineDataSet(null, "z");
        setZ.setLineWidth(2.5f);
        setZ.setDrawCircles(false);
        setZ.setColor(Color.rgb(99, 99, 240));
        data.addDataSet(setZ);
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            getApplicationContext().unregisterReceiver(mReceiver);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(SensorService.isRunning) {
            try {
                getApplicationContext().registerReceiver(mReceiver, mIntentFilter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_start:
                mBtnStop.setVisibility(View.VISIBLE);
                mBtnStop.setEnabled(true);
                mBtnStart.setVisibility(View.INVISIBLE);
                mBtnStart.setEnabled(false);
                mGraph.clear();
                setupGraph();
                startCollecting();
                break;
            case R.id.button_stop:
                mBtnStop.setVisibility(View.INVISIBLE);
                mBtnStop.setEnabled(false);
                mBtnStart.setVisibility(View.VISIBLE);
                mBtnStart.setEnabled(true);
                stopCollecting();
                break;
        }
    }

    private void startCollecting() {
        if(!SensorService.isRunning) {

            // Call the service to start collecting accelerometer data
            Intent intent = new Intent(this, SensorService.class);
            startService(intent);
            getApplicationContext().registerReceiver(mReceiver, mIntentFilter);
        }
    }

    private void stopCollecting() {
        if (SensorService.isRunning) {
            stopService(new Intent(this, SensorService.class));
            try {
                getApplicationContext().unregisterReceiver(mReceiver);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    private void showResults() {
//        try {
//            FileInputStream fis = openFileInput("log.csv");
//            FileReader fr = new FileReader(fis.getFD());
//            BufferedReader br = new BufferedReader(fr);
//            String line = null;
//            while ((line = br.readLine()) != null) {
//                //mTxtResult.append(line);
//                //mTxtResult.append("\n");
//            }
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void outputResults(AccelerometerData data) {
        try {

            LineData graphData = mGraph.getData();
            int startPoint = graphData.getXValCount();

            for(int i=0; i< data.getDataCount(); i++) {
                // Add timestamp to X-axis and X,Y,Z line series values
                graphData.addXValue(data.getElapsedTimestamps()[i] + "");
                graphData.addEntry(new Entry(data.getAccelDataX()[i], i + startPoint), 0);
                graphData.addEntry(new Entry(data.getAccelDataY()[i], i + startPoint), 1);
                graphData.addEntry(new Entry(data.getAccelDataZ()[i], i + startPoint), 2);
            }

            mGraph.notifyDataSetChanged();
            mGraph.invalidate();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new HandleUpdate().execute(intent);
        }

        class HandleUpdate extends AsyncTask<Intent, Void, AccelerometerData> {
            @Override
            protected AccelerometerData doInBackground(Intent ... params) {
                return (AccelerometerData)params[0].getParcelableExtra(AccelerometerData.ACCELEROMETER_DATA_KEY);
            }

            @Override
            protected void onPostExecute(AccelerometerData result) {
                outputResults(result);
            }
        }
    };
}
