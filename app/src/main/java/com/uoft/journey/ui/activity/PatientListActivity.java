package com.uoft.journey.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.uoft.journey.R;
import com.uoft.journey.data.AddPatient;
import com.uoft.journey.data.DownloadFriends;
import com.uoft.journey.entity.Patient;
import com.uoft.journey.ui.adapter.PatientListAdapter;

import java.util.ArrayList;
import java.util.List;

public class PatientListActivity extends AppCompatActivity {

    List<Patient> mPatientList = new ArrayList<Patient>();
    PatientListAdapter mAdapter;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        Intent intent = getIntent();
        String addpatient = intent.getStringExtra("newpatient");


        mRecyclerView = (RecyclerView) findViewById(R.id.patient_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        FloatingActionButton newBtn = (FloatingActionButton) findViewById(R.id.add_patient_button);
        newBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                add(v);
            }
        });



        // for now hard code patients
      /*  Patient patient1 = new Patient(193192, "Arnold Palmer", new Date(12/1/2016));
        Patient patient2 = new Patient(193192, "Jane Smith", new Date(21/12/2015));
        Patient patient3 = new Patient(193192, "Kate Bishop", new Date(15/1/2016));
        Patient patient4 = new Patient(193192, "Marco Polo", new Date(28/1/2016));
        Patient patient5 = new Patient(193192, "Patrick Mackelmore", new Date(1/25/2016));

        mPatientList.add(patient1);
        mPatientList.add(patient2);
        mPatientList.add(patient3);
        mPatientList.add(patient4);
        mPatientList.add(patient5);*/
        mAdapter = new PatientListAdapter(this, mPatientList);
        mRecyclerView.setAdapter(mAdapter);

        if (!addpatient.equals("no")){

           /* AddPatient task2 = new AddPatient(getApplicationContext(), mAdapter, addpatient);
            task2.execute().;*/
            DownloadFriends task = new DownloadFriends(getApplicationContext(), mAdapter, addpatient);
            task.execute();
        }else {

            DownloadFriends task = new DownloadFriends(getApplicationContext(), mAdapter, null);
            task.execute();

        }

    }

    public void add(View v) {
        switch (v.getId()) {
            case R.id.add_patient_button:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Enter Patients email");

                // Set up the input
                final EditText input = new EditText(this);

                input.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("Current Patient", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        addPatient(input.getText().toString());
                    }

                });

                builder.setNegativeButton("New Patient", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                        intent.putExtra("clinician","yes");

                        startActivity(intent);
                    }

                });

               /* builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });*/

                builder.show();
                break;
        }


    }
    private void addPatient(String s) {

        AddPatient task = new AddPatient(getApplicationContext(), mAdapter, s);
        task.execute();


    }

    @Override
    public void onPause(){
        super.onPause();

    }


}


