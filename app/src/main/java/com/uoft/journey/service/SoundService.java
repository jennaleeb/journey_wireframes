package com.uoft.journey.service;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;

import com.uoft.journey.R;

/**
 * Created by jenna on 16-04-13.
 */
public class SoundService {

    private Context context;
    private Handler mHandler;
    private boolean play = true;
    private MediaPlayer mMediaPlayer;

    public SoundService(Handler handler, Context context) {
        this.mHandler = handler;
        this.context = context;
    }

    public void play(){
        do {
            mMediaPlayer = MediaPlayer.create(context, R.raw.notification0);
            mMediaPlayer.start();
        }while(play);
    }

    public void stop() {
        play = false;
        mMediaPlayer.release();
        mMediaPlayer = null;
    }
}
