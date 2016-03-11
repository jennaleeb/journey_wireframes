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

    public interface OnItemClickListener {
        void onItemClick(int trialId);
    }

    private List<Trial> mTrials;
    private final OnItemClickListener mListener;

    public PatientTrialsListAdapter(List<Trial> trials, OnItemClickListener listener)
    {
        mTrials = trials;
        mListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        TextView day;
        TextView title;
        TextView time;
        TextView stepCount;
        TextView strideTimeVar;

        // Show each trial as a card
        ViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.trial_card);
            day = (TextView)itemView.findViewById(R.id.card_day);
            time = (TextView)itemView.findViewById(R.id.card_time);
            title = (TextView)itemView.findViewById(R.id.card_title);
            stepCount = (TextView)itemView.findViewById(R.id.step_count_value);
            strideTimeVar = (TextView)itemView.findViewById(R.id.stride_var_value);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.trial_card, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        DateFormat df = new SimpleDateFormat("dd MMM", Locale.CANADA);
        DateFormat df2 = new SimpleDateFormat("dd MMM yyyy - hh:mm a", Locale.CANADA);
        if(mTrials != null && mTrials.size() > position) {
            holder.day.setText(df.format(mTrials.get(position).getStartTime()));
            holder.title.setText(String.format("Assessment %d", mTrials.get(position).getTrialId()));
            holder.stepCount.setText(String.valueOf(mTrials.get(position).getNumberOfSteps()));
            holder.strideTimeVar.setText(String.format("%.1f", mTrials.get(position).getCoeffOfVar()));
            holder.time.setText(df2.format(mTrials.get(position).getStartTime()));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(mTrials.get(position).getTrialId());
                }
            });
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
