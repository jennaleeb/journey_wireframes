package com.uoft.journey.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uoft.journey.R;

/**
 * Created by jenna on 16-01-27.
 */
public class PatientNewsfeedFragment extends Fragment {

    FloatingActionButton addAssessButton;

    public PatientNewsfeedFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_patient_newsfeed, container, false);
        addAssessButton = (FloatingActionButton)view.findViewById(R.id.add_assess_button);
        addAssessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), com.uoft.journey.ui.AddNewAssessmentActivity.class);
                startActivity(intent);
            }
        });
        return view;


    }
}
