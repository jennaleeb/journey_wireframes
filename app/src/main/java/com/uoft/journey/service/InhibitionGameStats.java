package com.uoft.journey.service;

import org.apache.commons.math3.special.Erf;

import java.util.List;

/**
 * Created by jenna on 16-04-15.
 */
public class InhibitionGameStats {

    private List<long[]> hitStats;
    private int true_stim_counter;
    private int false_stim_counter;

    public InhibitionGameStats(List<long[]> hitStats, int true_stim_counter, int false_stim_counter) {
        this.hitStats = hitStats;
        this.true_stim_counter = true_stim_counter;
        this.false_stim_counter = false_stim_counter;
    }


    // RT of correct hits only
    public int meanResponseTime() {
        int sum_rt = 0;
        for (int i=0; i<hitStats.size(); i++){
            if (hitStats.get(i)[0] == 1) {
                sum_rt += hitStats.get(i)[1];
            }

        }
        return sum_rt / true_stim_counter;
    }

    // Omission error rate (# missed / # pos stim)
    public float omissionError() {

        int correct_hits = 0;

        for (int i=0; i<hitStats.size(); i++){
            if (hitStats.get(i)[0] == 1) {
                correct_hits++;
            }

        }

        float om_err = (float) ( (true_stim_counter - correct_hits) * 100.0 / true_stim_counter);
        return om_err;
    }

    // Commission error rate (# hit / # neg stim)
    public float commissionError() {

        int false_alarm_hits = 0;

        for (int i=0; i<hitStats.size(); i++){
            if (hitStats.get(i)[0] == 0) {
                false_alarm_hits++;
            }

        }

        float com_err = (false_stim_counter != 0) ? (float) ( false_alarm_hits * 100.0 / false_stim_counter ) : 0.0f;
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
        return (float) pToZ(0.5f);
    }

    public double pToZ(float p) {
        double z = Math.sqrt(2) * Erf.erf(2*p);
        return z;
    }
}
