package com.uoft.journey.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.uoft.journey.R;
import com.uoft.journey.service.SoundService;

/**
 * Created by jenna on 16-04-26.
 */
public class GameInstructionsDialogFragment extends DialogFragment {

    private Button mGoButton;
    private Button mNoGoButton;
    private Context mContext;
    private SoundService mSoundService;

    public GameInstructionsDialogFragment() {

    }

    public static GameInstructionsDialogFragment newInstance(Context c) {
        GameInstructionsDialogFragment frag = new GameInstructionsDialogFragment();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_instructions_inhib_game, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mSoundService = new SoundService(mContext);
        mGoButton = (Button)view.findViewById(R.id.button_go_sound);
        mNoGoButton = (Button)view.findViewById(R.id.button_nogo_sound);

        mGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSoundService.playGo();
            }
        });

        mNoGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSoundService.playNoGo();
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }




}
