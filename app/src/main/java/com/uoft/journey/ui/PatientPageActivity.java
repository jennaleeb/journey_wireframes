package com.uoft.journey.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.uoft.journey.R;

public class PatientPageActivity extends AppCompatActivity {

    FloatingActionButton addAssessButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_page);

        addAssessButton = (FloatingActionButton)findViewById(R.id.add_assess_button);
        addAssessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientPageActivity.this, com.uoft.journey.ui.AddNewAssessmentActivity.class);
                startActivity(intent);
            }
        });
    }

}
