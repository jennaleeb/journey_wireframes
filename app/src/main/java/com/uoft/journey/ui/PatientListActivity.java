package com.uoft.journey.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.uoft.journey.R;
import com.uoft.journey.models.Patient;
import com.uoft.journey.ui.adapters.PatientListAdapter;

import java.util.ArrayList;
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
        Patient patient1 = new Patient("Arnold Palmer", 0);
        Patient patient2 = new Patient("Jane Smith", 5);
        Patient patient3 = new Patient("Kate Bishop", 13);
        Patient patient4 = new Patient("Marco Polo", 7);
        Patient patient5 = new Patient("Patrick McGilvery", 10);

        mPatientList.add(patient1);
        mPatientList.add(patient2);
        mPatientList.add(patient3);
        mPatientList.add(patient4);
        mPatientList.add(patient5);

        mAdapter = new PatientListAdapter(this, mPatientList);
        mRecyclerView.setAdapter(mAdapter);

    }


}
