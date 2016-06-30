package com.uoft.journey.service;

import android.util.Log;

import org.apache.commons.math3.special.Erf;

import java.util.Arrays;
import java.util.List;
/**
 * Created by jenna on 16-04-15.
 */
public class InhibitionGameStats {

    private List<long[]> hitStats;
    private int true_stim_counter;
    private int false_stim_counter;
    public static final String TAG = "STATSDEDUGTAG";

    private int hit_count = 0;
    private int false_alarm_hits = 0;
    private int sum_rt = 0;

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


    // Mean RT of correct hits only
    public int meanResponseTime() {
        int sum_rt = 0;

        for (int i=0; i<hitStats.size(); i++){
            if (hitStats.get(i)[0] == 1) {
                sum_rt += hitStats.get(i)[1];
            }

        }
        return ( (sum_rt != 0) ? sum_rt / hitCount() : 0 );
    }

    public int[] collectCorrectResponseTimes() {

        int[] correct_rts = new int[hitStats.size()];

        for (int i=0; i<hitStats.size(); i++) {
            if (hitStats.get(i)[0] == 1) {
                correct_rts[i] = (int) hitStats.get(i)[1];
            }
        }

        int j = 0;
        for (int i=0; i<correct_rts.length; i++) {
            if (correct_rts[i] !=0)
                correct_rts[j++] = correct_rts[i];
        }

        int[] revised_correct_rts = new int[j];
        System.arraycopy( correct_rts, 0, revised_correct_rts, 0, j );

        return revised_correct_rts;
    }

    // Median RT of correct hits
   public long medianResponseTime() {
        long median = 0;
        int hit_count = hitCount();
        if (hit_count > 0) {

            int[] correct_rts = collectCorrectResponseTimes();
            Arrays.sort(correct_rts);

            int middle = ((correct_rts.length) / 2);
            if(correct_rts.length % 2 == 0){
                long medianA = correct_rts[middle];
                long medianB = correct_rts[middle-1];
                median = (medianA + medianB) / 2;
            } else{
                median = correct_rts[middle + 1];
            }

        }

        return median;
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

    // RT of false alarms
    public int meanFalseAlarmResponseTime() {
        int sum_rt = 0;

        for (int i=0; i<hitStats.size(); i++){
            if (hitStats.get(i)[0] == 0) {
                sum_rt += hitStats.get(i)[1];
            }

        }
        return ( (sum_rt != 0) ? sum_rt / falseAlarmCount() : 0 );
    }

    public float responseTimeFalseAlarmSD() {
        int mean = meanFalseAlarmResponseTime();
        double rt_sd = 0;

        for (int i=0; i<hitStats.size(); i++){
            if (hitStats.get(i)[0] == 0) {
                rt_sd += (hitStats.get(i)[1] - mean * 1.0) * (hitStats.get(i)[1] - mean * 1.0);
            }

        }

        rt_sd = Math.sqrt(rt_sd / falseAlarmCount());
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

        // Don't go over 100%
        if (com_err > 100) {
            return 100.0f;
        }
        else return com_err;

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
