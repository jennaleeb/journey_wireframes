package com.uoft.journey.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.uoft.journey.R;
import com.uoft.journey.entity.Patient;
import com.uoft.journey.ui.adapter.PatientListAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PatientListActivity extends AppCompatActivity {

    List<Patient> mPatientList = new ArrayList<Patient>();
    RecyclerView.Adapter mAdapter;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.patient_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // for now hard code patients
        Patient patient1 = new Patient(193192, "Arnold Palmer", new Date(12/1/2016));
        Patient patient2 = new Patient(193192, "Jane Smith", new Date(21/12/2015));
        Patient patient3 = new Patient(193192, "Kate Bishop", new Date(15/1/2016));
        Patient patient4 = new Patient(193192, "Marco Polo", new Date(28/1/2016));
        Patient patient5 = new Patient(193192, "Patrick Mackelmore", new Date(1/25/2016));

        mPatientList.add(patient1);
        mPatientList.add(patient2);
        mPatientList.add(patient3);
        mPatientList.add(patient4);
        mPatientList.add(patient5);

        mAdapter = new PatientListAdapter(this, mPatientList);
        mRecyclerView.setAdapter(mAdapter);

    }


}
