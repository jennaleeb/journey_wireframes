package com.uoft.journey.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uoft.journey.R;
import com.uoft.journey.entity.Patient;
import com.uoft.journey.ui.activity.PatientPageActivity;

import java.util.List;

/**
 * Created by jenna on 16-01-21.
 */
public class PatientListAdapter extends RecyclerView.Adapter<PatientListAdapter.PatientListViewHolder> {

    private static final String TAG = "PatientListDebug";
    private Context context;

    //TODO: fix static field once we implement database
    private static List<Patient> mPatientList;

    public PatientListAdapter(Context context, List<Patient> mPatientList) {
        this.mPatientList = mPatientList;
        this.context = context;

    }


    @Override
    public PatientListAdapter.PatientListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.fragment_patient_list_item, viewGroup, false);

        return new PatientListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PatientListAdapter.PatientListViewHolder patientListViewHolder, int i) {

        Patient patient = mPatientList.get(i);

        patientListViewHolder.vName.setText(patient.getName());
        patientListViewHolder.vDateStarted.setText(String.valueOf(patient.getDateAdmittedString()));


    }

    @Override
    public int getItemCount() {
        return mPatientList.size();
    }


    public static class PatientListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView vName;
        private TextView vDateStarted;

        public PatientListViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            vName = (TextView) v.findViewById(R.id.text_name);
            vDateStarted = (TextView) v.findViewById(R.id.text_date_started);
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "Clicked" + getAdapterPosition());

            Intent intent= new Intent(view.getContext(), PatientPageActivity.class);
            intent.putExtra("name", mPatientList.get(getAdapterPosition()).getName());
            view.getContext().startActivity(intent);
        }

    }
}

