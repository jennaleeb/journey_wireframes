package com.uoft.journey.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uoft.journey.R;
import com.uoft.journey.data.LocalDatabaseAccess;
import com.uoft.journey.entity.Trial;
import com.uoft.journey.ui.adapter.PatientTrialsListAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PatientTrialsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PatientTrialsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PatientTrialsFragment extends Fragment {
    private static final String ARG_USER_ID = "userId";
    private int mUserId;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //private OnFragmentInteractionListener mListener;

    public PatientTrialsFragment() {
        // Required empty public constructor
    }

    public static PatientTrialsFragment newInstance(int param1) {
        PatientTrialsFragment fragment = new PatientTrialsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_USER_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserId = getArguments().getInt(ARG_USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_patient_trials, container, false);

        // List contained in RecyclerView
        mRecyclerView = (RecyclerView) view.findViewById(R.id.trial_list_view);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        ArrayList<Trial> trials = LocalDatabaseAccess.getTrialsForUser(getContext(), mUserId);
        mAdapter = new PatientTrialsListAdapter(trials);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
