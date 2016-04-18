package com.uoft.journey.service;

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
    public int omissionError() {

        int correct_hits = 0;

        for (int i=0; i<hitStats.size(); i++){
            if (hitStats.get(i)[0] == 1) {
                correct_hits++;
            }

        }

        return (true_stim_counter - correct_hits) / true_stim_counter;
    }

    // Commission error rate (# hit / # neg stim)
    public int commissionError() {

        int false_alarm_hits = 0;

        for (int i=0; i<hitStats.size(); i++){
            if (hitStats.get(i)[0] == 0) {
                false_alarm_hits++;
            }

        }

        return false_alarm_hits / false_stim_counter;
    }

    // Correct detection (# hit / # pos stim)
    public int correctDetection() {
        int correct_hits = 0;

        for (int i=0; i<hitStats.size(); i++){
            if (hitStats.get(i)[0] == 1) {
                correct_hits++;
            }

        }

        return correct_hits / true_stim_counter;
    }



    // Overall accuracy?
    public int measureAccuracy() {
        int correct_hits = 0;
        int false_alarm_hits = 0;

        for (int i=0; i<hitStats.size(); i++){
            if (hitStats.get(i)[0] == 1) {
                correct_hits++;
            } else {
                false_alarm_hits++;
            }

        }

        return ( (correct_hits / true_stim_counter) - (false_alarm_hits / false_stim_counter));
    }
}
