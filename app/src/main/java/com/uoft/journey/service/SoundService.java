package com.uoft.journey.service;

import android.content.Context;
import android.media.MediaPlayer;

import com.uoft.journey.R;

import java.util.Random;

/**
 * Created by jenna on 16-04-13.
 */
public class SoundService implements MediaPlayer.OnCompletionListener {

    private Context context;
    private MediaPlayer mMediaPlayer;
    private int sound;
    public boolean sound_hit;

    public SoundService(){

    }

    public SoundService(Context context) {
        this.context = context;
    }


    public void play(){
        hitMissLogic();
        if (sound_hit){
            sound = R.raw.hit_sound;
        } else {
            sound = R.raw.miss_sound;
        }
        MediaPlayer mp = MediaPlayer.create(context, sound);
        mp.setOnCompletionListener(this);
        mp.start();

    }

    public void stop() {
        mMediaPlayer.release();
        mMediaPlayer = null;
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        if(mp!=null) {
            if(mp.isPlaying())
                mp.stop();
            mp.reset();
            mp.release();
        }
    }

    // Game logic
    private void hitMissLogic(){

        // Which sound plays? Hits (80%) or misses (20%)
        Random rand = new Random();
        int r = rand.nextInt((10 - 1) + 1) + 1;

        if (r > 2) {
            sound_hit = true;
        } else {
            sound_hit = false;
        }

    }

    public int timeIntervalLogic(){
        int timeInterval = 0;

        // Generate random number between 0 and 10
        Random rand = new Random();
        int r = rand.nextInt((10 - 0) + 0) + 0;

        // Time interval ranges from 1000ms to 2000ms
        timeInterval = 1000 + r*100;

        return timeInterval;
    }

    public void playGo() {
        MediaPlayer mp = MediaPlayer.create(context, R.raw.hit_sound);
        mp.setOnCompletionListener(this);
        mp.start();
    }

    public void playNoGo() {
        MediaPlayer mp = MediaPlayer.create(context, R.raw.miss_sound);
        mp.setOnCompletionListener(this);
        mp.start();
    }

}

