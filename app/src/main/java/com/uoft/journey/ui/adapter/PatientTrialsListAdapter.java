package com.uoft.journey.ui.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uoft.journey.R;
import com.uoft.journey.entity.Trial;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for list of trials
 */
public class PatientTrialsListAdapter extends RecyclerView.Adapter<PatientTrialsListAdapter.ViewHolder>  {

    private List<Trial> mTrials;

    public PatientTrialsListAdapter(List<Trial> trials) {
        mTrials = trials;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView day;
        TextView title;
        TextView line1;
        TextView line2;
        TextView line3;
        TextView line4;

        // Show each trial as a card
        ViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.trial_card);
            day = (TextView)itemView.findViewById(R.id.card_day);
            title = (TextView)itemView.findViewById(R.id.card_title);
            line1 = (TextView)itemView.findViewById(R.id.card_line_1);
            line2 = (TextView)itemView.findViewById(R.id.card_line_2);
            line3 = (TextView)itemView.findViewById(R.id.card_line_3);
            line4 = (TextView)itemView.findViewById(R.id.card_line_4);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.trial_card, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DateFormat df = new SimpleDateFormat("dd MMM", Locale.CANADA);
        DateFormat df2 = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.CANADA);
        if(mTrials != null && mTrials.size() > position) {
            holder.day.setText(df.format(mTrials.get(position).getStartTime()));
            holder.title.setText(String.format("Assessment %d", mTrials.get(position).getTrialId()));
            holder.line1.setText(df2.format(mTrials.get(position).getStartTime()));
            holder.line2.setText(String.format("Average Stride Time: %.2fms", mTrials.get(position).getMeanStrideTime()));
            holder.line3.setText(String.format("Standard Deviation: %.2fms", mTrials.get(position).getStandardDev()));
            holder.line4.setText(String.format("Coefficient of Variation: %.2f", mTrials.get(position).getCoeffOfVar()));
        }
    }

    @Override
    public int getItemCount() {
        return mTrials.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
