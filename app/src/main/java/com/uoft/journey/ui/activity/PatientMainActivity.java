package com.uoft.journey.ui.activity;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.uoft.journey.R;
import com.uoft.journey.data.LocalDatabaseAccess;
import com.uoft.journey.entity.Patient;
import com.uoft.journey.ui.adapter.PatientTrialsListAdapter;
import com.uoft.journey.ui.fragment.PatientTrialsFragment;

public class PatientMainActivity extends AppCompatActivity implements View.OnClickListener {

    Patient mPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_main);

        Button btnNew = (Button)findViewById(R.id.button_new);
        btnNew.setOnClickListener(this);

        // Show test user
        mPatient = LocalDatabaseAccess.getTestUser(this, "Joe Bloggs");
        TextView name = (TextView)findViewById(R.id.patient_name);
        name.setText(mPatient.getName());

        // Create the trial list fragment
        PatientTrialsFragment fragment = PatientTrialsFragment.newInstance(mPatient.getID());
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.replace(R.id.feed_container, fragment);
        trans.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_new:
                Intent intent = new Intent(PatientMainActivity.this, MeasureActivity.class); // **** Standard Version ****
                //Intent intent = new Intent(PatientMainActivity.this, DebugMeasureActivity.class); // **** For Debug Version ****
                Bundle bundle = new Bundle();
                bundle.putInt("userId", mPatient.getID());
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        // Reload the trial list fragment
        PatientTrialsFragment fragment = PatientTrialsFragment.newInstance(mPatient.getID());
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.replace(R.id.feed_container, fragment);
        trans.commit();
    }
}
