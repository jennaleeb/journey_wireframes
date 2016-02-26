package com.uoft.journey.service;

import java.util.ArrayList;

/**
 * Signal processing methods
 * Some updated from MyWalk app
 */
public class Gait {

    private static float minThreshold = 9.4f; // Threshold for Y values, below which minima can be taken

    // A very simple low pass filter, smoothing value may need adjusting
    public static float[] simpleLowPassFilter(float[] data, float smoothing) {
        float value = data[0]; // start with the first input
        for (int i=1; i<data.length; i++){
            float currentValue = data[i];
            value += (currentValue - value) / smoothing;
            data[i] = value;
        }

        return data;
    }

    // Locate where the local maximas are and return the indexes
    // This will always take the first maxima, which may be fine in most cases
    public static Integer[] localMaximaIndexes(float[] data) {
        ArrayList<Integer> indexes = new ArrayList<>();
        Boolean isUp = false;

        for(int i=0; i<data.length-1; i++) {
            if(!isUp) {
                if (data[i] < data[i + 1] && data[i] < minThreshold) {
                    isUp = true;
                }
            }
            else if(data[i] > data[i+1] && data[i] > minThreshold) {
                isUp = false;
                indexes.add(i);
            }
        }

        return indexes.toArray(new Integer[indexes.size()]);
    }

    // Locate where the local maximas are and return the step times
    // This will always take the first maxima, which may be fine in most cases
    public static Integer[] localMaximaTimes(float[] data, int[] times) {
        ArrayList<Integer> stepTimes = new ArrayList<>();
        Boolean isUp = false;

        for(int i=0; i<data.length-1; i++) {
            if(!isUp) {
                if (data[i] < data[i + 1] && data[i] < minThreshold) {
                    isUp = true;
                }
            }
            else if(data[i] > data[i+1] && data[i] > minThreshold) {
                isUp = false;
                stepTimes.add(times[i]);
            }
        }

        return stepTimes.toArray(new Integer[stepTimes.size()]);
    }

    // As above. Locate where the local maximas but return the data as zeros, with 10s for the maxima points
    public static Integer[] binaryLocalMaxima(float[] data) {
        Integer[] vals = new Integer[data.length];
        Boolean isUp = false;

        for(int i=0; i<data.length-1; i++) {
            if(!isUp) {
                vals[i] = 0;
                if (data[i] < data[i + 1] && data[i] < minThreshold) {
                    isUp = true;
                }
            }
            else {
                if(data[i] > data[i+1]  && data[i] > minThreshold) {
                    isUp = false;
                    vals[i] = 10;
                }
                else {
                    vals[i] = 0;
                }
            }
        }

        vals[data.length - 1] = 0;

        return vals;
    }

    public static float getMeanStepTime(int[] stepTimes) {
        return ((float)stepTimes[stepTimes.length - 1] - (float)stepTimes[0]) / ((float)stepTimes.length - 1);
    }

    // Get the standard deviation of the steps
    public static float getStandardDeviation(int[] steps, float mean) {
        float var = 0.0f;
        // Calculate the variance
        for(int i=0; i<steps.length-1; i++) {
            float stepTime = steps[i+1] - steps[i];
            var += (mean - stepTime) * (mean - stepTime);
        }
        var = var/(steps.length-1);

        // Standard dev
        return (float)Math.sqrt(var);
    }

    // Coefficient of Variation of steps 100 * SD / mean
    public static float getCoefficientOfVariation(float standardDev, float mean) {
        return 100.0f * standardDev / mean;
    }
}
