package com.uoft.journey.service;

import android.util.Log;

import org.apache.commons.math3.special.Erf;

import java.util.List;

/**
 * Created by jenna on 16-04-15.
 */
public class InhibitionGameStats {

    private List<long[]> hitStats;
    private int true_stim_counter;
    private int false_stim_counter;
    public static final String TAG = "STATSDEDUGTAG";

    public InhibitionGameStats(List<long[]> hitStats, int true_stim_counter, int false_stim_counter) {
        this.hitStats = hitStats;
        this.true_stim_counter = true_stim_counter;
        this.false_stim_counter = false_stim_counter;
    }

    public int hitCount() {
        int hit_count = 0;

        for (int i=0; i<hitStats.size(); i++){
            if (hitStats.get(i)[0] == 1) {
                hit_count++;
            }
        }

        return (true_stim_counter > hit_count) ? hit_count : true_stim_counter;
    }

    public int missCount() {
        int miss_count = (true_stim_counter > hitCount()) ? (true_stim_counter - hitCount()) : 0;
        return miss_count;
    }

    public int falseAlarmCount() {
        int false_alarm_hits = 0;

        for (int i=0; i<hitStats.size(); i++){
            if (hitStats.get(i)[0] == 0) {
                false_alarm_hits++;
            }

        }

        return false_alarm_hits;
    }

    public int correctNegCount() {
        int correct_neg = (false_stim_counter > falseAlarmCount()) ? (false_stim_counter - falseAlarmCount()) : 0;
        return correct_neg;
    }


    // RT of correct hits only
    public int meanResponseTime() {
        int sum_rt = 0;

        for (int i=0; i<hitStats.size(); i++){
            if (hitStats.get(i)[0] == 1) {
                sum_rt += hitStats.get(i)[1];
            }

        }
        return ( (sum_rt != 0) ? sum_rt / hitCount() : 0 );
    }

    public float responseTimeSD() {
        int mean = meanResponseTime();
        double rt_sd = 0;

        for (int i=0; i<hitStats.size(); i++){
            if (hitStats.get(i)[0] == 1) {
                rt_sd += (hitStats.get(i)[1] - mean * 1.0) * (hitStats.get(i)[1] - mean * 1.0);
            }

        }

        rt_sd = Math.sqrt(rt_sd / hitCount());
        return (float) rt_sd;
    }

    // Omission error rate (# missed / # pos stim)
    public float omissionError() {

        float om_err = (float) ( missCount() * 100.0 / true_stim_counter);
        return om_err;
    }

    // Commission error rate (# hit / # neg stim)
    public float commissionError() {

        float com_err = (false_stim_counter != 0) ? (float) ( falseAlarmCount() * 100.0 / false_stim_counter ) : 0.0f;
        return com_err;

    }



    // Overall accuracy?
    public float measureAccuracy() {
        int correct_hits = 0;
        int false_alarm_hits = 0;

        for (int i=0; i<hitStats.size(); i++){
            if (hitStats.get(i)[0] == 1) {
                correct_hits++;
            } else {
                false_alarm_hits++;
            }

        }



        // return ( (correct_hits / true_stim_counter) - (false_alarm_hits / false_stim_counter));
        double p = 0.3;
        return (float) pToZ(p);
    }

    public double pToZ(double p) {
        double z = Math.sqrt(2) * Erf.erfcInv(2 * p);
        Log.d(TAG, "z: " + z);
        return z;
    }
}
