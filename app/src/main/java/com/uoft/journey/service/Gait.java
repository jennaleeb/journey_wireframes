package com.uoft.journey.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Signal processing methods
 * Some updated from MyWalk app
 */
public class Gait {

    private static float minThreshold = 9.4f; // Threshold for Y values, below which minima can be taken
    private static float maxThreshold = 9.8f; // Above this maxima can be taken

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

    // Converted from c# algorithm found on http://www.centerspace.net/butterworth-filter-csharp/
    public static float[] butterworthFilter(float[] inData, double sampleFrequency, int order, double f0, double dcGain) {
        double[] data = convertFloatsToDoubles(inData);

        int n = data.length;

        // Apply forward FFT
        DoubleFFT_1D fft = new DoubleFFT_1D(n);
        fft.realForward(data);

        if ( f0 > 0 )
        {
            int numBins = n / 2;  // Half the length of the FFT by symmetry
            double binWidth = sampleFrequency / n; // Hz

            // Filter
            for(int i=1; i<=numBins; i++) {
                double binFreq = binWidth * i;
                double gain = dcGain / ( Math.sqrt((1 +
                        Math.pow(binFreq / f0, 2.0 * order))) );
                data[i] *= gain;
                data[n - i] *= gain;
            }
        }

        // Reverse filtered signal, with scale to get correct amplitude
        fft.realInverse(data, true);

        return convertDoublesToFloats(data);
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

    // Locate where the local maximas are and return the step times
    // This version uses locking windows to identify steps
    public static Integer[] localMaximaTimesUsingWindow(float[] data, int[] times) {
        ArrayList<Integer> stepTimes = new ArrayList<>();
        ArrayList<Integer> peaks = new ArrayList<>();
        int windowSize = getWindow(data, times);

        // Identify the peaks
        for(int i=1; i<data.length-1; i++) {
            if(times[i] >= windowSize/2 && data[i-1] < data[i] && data[i] > data[i+1] && data[i] > maxThreshold) {
                peaks.add(i);
            }
        }

        float prevLeft = 0;
        float prevRight = 0;
        int prevIndex = -1;
        int numIndex = -1;
        int prevNumIndex = -1;
        int halfWin = windowSize/2;

        for(int i: peaks) {

            numIndex++;

            // Check if too close to prev step
            if(prevIndex > -1 && times[i] - times[prevIndex] < halfWin)
                continue;

            float minLeft = data[i];
            float minRight = data[i];
            int centreTime = times[i];

            // Find the minimum value to the left, within lock window
            for(int j=i; j>=0 && Math.abs(times[j] - centreTime) <= halfWin; j--) {
                if(data[j] < minLeft)
                    minLeft = data[j];
            }

            // Find the minimum value to the right, within lock window
            for(int j=i; j<data.length && Math.abs(times[j] - centreTime) <= halfWin; j++) {
                if(data[j] < minRight)
                    minRight = data[j];
            }

            minLeft = data[i] - minLeft;
            minRight = data[i] - minRight;

            // First step
            if(prevLeft == 0) {
                prevLeft = minLeft;
                prevRight = minRight;
            }

            // Within threshold so add step
            if(minLeft/prevLeft > 0.38 && minRight/prevRight > 0.38) {
                if(stepTimes.size() > 1 && times[i] - stepTimes.get(stepTimes.size() - 1) > (stepTimes.get(stepTimes.size() - 1) - stepTimes.get(stepTimes.size() - 2)) * 1.75) {
                    //Possible missed step so look at any in between peaks
                    int stepDiff = stepTimes.get(stepTimes.size() - 2) - stepTimes.get(stepTimes.size() - 1);
                    int prevStepTime = stepTimes.get(stepTimes.size() - 1);

                    // Look at the middle steps and relax the rules slightly
                    for(int k=prevNumIndex + 1; k<numIndex; k++) {
                        if(times[peaks.get(k)] - prevStepTime >= stepDiff * 0.4 ) {
                            centreTime = times[peaks.get(k)];
                            float newMinLeft = data[peaks.get(k)];
                            float newMinRight = data[peaks.get(k)];

                            // Find the minimum value to the left, within lock window
                            for(int j=peaks.get(k); j>=0 && Math.abs(times[j] - centreTime) <= halfWin; j--) {
                                if(data[j] < newMinLeft)
                                    newMinLeft = data[j];
                            }

                            // Find the minimum value to the right, within lock window
                            for(int j=peaks.get(k); j<data.length && Math.abs(times[j] - centreTime) <= halfWin; j++) {
                                if(data[j] < newMinRight)
                                    newMinRight = data[j];
                            }

                            // Check relaxed threshold
                            if(newMinLeft/prevLeft > 0.4 && newMinRight/prevRight > 0.4) {
                                stepTimes.add(times[peaks.get(k)]);
                                break;
                            }
                        }
                    }
                }

                stepTimes.add(times[i]);
                prevLeft = minLeft;
                prevRight = minRight;
                prevIndex = i;
                prevNumIndex = numIndex;
            }
        }

        return stepTimes.toArray(new Integer[stepTimes.size()]);
    }

    // Calculate a locking window where steps won't be detected
    private static int getWindow(float[] data, int[] times) {
        ArrayList<Integer> crossPoints = new ArrayList<>();
        int max = 0;
        int prevTime = 0;

        // Find all the points where it crosses the low threshold
        for(int i=0; i<data.length-1; i++) {
            if((data[i] <= minThreshold && data[i+1] > minThreshold)) {
                if(crossPoints.size() == 0) {
                    crossPoints.add(times[i]);
                    max = times[i];
                    prevTime = times[i];
                }
                else {
                    int val = times[i] - prevTime;
                    prevTime = times[i];
                    crossPoints.add(val);
                    if(val > max)
                        max = val;
                }
            }
        }

        int halfMax = max/2;
        if(max > 700) {
            // Max between two points is too large (above 0.7s) so return half the mean
            int tot = 0;
            for(int i=1; i<crossPoints.size(); i++){
                tot += crossPoints.get(i);
            }
            int mean = tot / (crossPoints.size() - 1);
            return mean/2;

        }
        else if(max < 400) {
            // Max too small (below 0.4s) so multiply by 0.6
            return (int)(max * 0.6f);
        }
        else {
            // Default to half max gap
            return halfMax;
        }
    }

    public static ArrayList<int[]> getPausePoints(int[] times) {
        ArrayList<int[]> pauseTimes = new ArrayList<>();
        if(times.length == 0) {
            return pauseTimes;
        }

        int median = getMedian(times);
        for(int i=0; i < times.length - 1; i++) {
            // Step is longer the twice the media so record a pause
            if(times[i+1] - times[i] > median * 1.8) {
                // Create the pause in the middle of the steps
                int[] pause = new int[2];
                pause[0] = times[i] + (median/2);
                pause[1] = times[i+1] - (median/2);
                pauseTimes.add(pause);
            }
        }

        return pauseTimes;
    }

    private static int getMedian(int[] times) {
        int[] items = new int[times.length - 1];
        for(int i=0; i<times.length-1; i++) {
            items[i] = times[i+1] - times[i];
        }

        Arrays.sort(items);
        if(items.length % 2 == 0)
            return (items[items.length/2] + items[items.length/2 - 1])/2;
        else
            return items[items.length/2];
    }

    public static float getMeanStepTime(int[] stepTimes, List<int[]> pauseTimes) {
        if (stepTimes.length > 0) {
            float pauseTime = 0;
            if(pauseTimes != null) {
                for(int i=0; i<pauseTimes.size(); i++){
                    pauseTime += (pauseTimes.get(i)[1] - pauseTimes.get(i)[0]);
                }
            }

            return ((float) stepTimes[stepTimes.length - 1] - (float) stepTimes[0] - pauseTime) / ((float) stepTimes.length - 1);
        }
        return 0;
    }

    // Get mean stride time (calculated as the time between every other step identified)
    public static float getMeanStrideTime(int[] stepTimes, List<int[]> pauseTimes) {
        if (stepTimes.length > 0) {
            float pauseTime = 0;
            if(pauseTimes != null) {
                for(int i=0; i<pauseTimes.size(); i++){
                    pauseTime += (pauseTimes.get(i)[1] - pauseTimes.get(i)[0]);
                }
            }

            float strideTimeSum = 0;

            for(int i=2; i < (stepTimes.length - 1); i++) {
                strideTimeSum += (stepTimes[i] - stepTimes[i-2]);
            }

            return ((strideTimeSum - pauseTime) / ((float) stepTimes.length - 2));


        }
        return 0;
    }

    public static float getStrideSD(int[] steps, float mean, List<int[]> pauseTimes) {
        float var = 0.0f;

        // Calculate the standard deviation
        for(int i=2; i<steps.length-1; i++) {
            float strideTime = steps[i] - steps[i-2];
            for(int j=0; j<pauseTimes.size(); j++) {
                if(pauseTimes.get(j)[0] > steps[i-2] && pauseTimes.get(j)[1] < steps[i])
                    strideTime = mean;
            }

            var += (mean - strideTime) * (mean - strideTime);
        }

        var = var / (steps.length-2);

        // Standard dev
        return (float)Math.sqrt(var);

    }

    public static float getStrideCV(float standardDev, float mean) {
        return 100.0f * standardDev / mean;
    }

//    public static float getStrideCV(int[] steps, float mean, List<int[]> pauseTimes){
//        float sd = 0.0f;
//        float cv = 0.0f;
//
//        // Calculate the standard deviation
//        for(int i=2; i<steps.length-1; i++) {
//            float strideTime = steps[i] - steps[i-2];
//            for(int j=0; j<pauseTimes.size(); j++) {
//                if(pauseTimes.get(j)[0] > steps[i-2] && pauseTimes.get(j)[1] < steps[i])
//                    strideTime = mean;
//            }
//
//            sd += (mean - strideTime) * (mean - strideTime);
//        }
//
//        sd = sd / (steps.length-2);
//        cv = 100 * ( (float)Math.sqrt(sd) ) / mean;
//
//        return cv;
//    }





    // Get the standard deviation of the step times
    public static float getStepSD(int[] steps, float mean, List<int[]> pauseTimes) {
        float var = 0.0f;
        int median = getMedian(steps);
        // Calculate the variance
        for(int i=0; i<steps.length-1; i++) {
            float stepTime = steps[i+1] - steps[i];
            for(int j=0; j<pauseTimes.size(); j++) {
                if(pauseTimes.get(j)[0] > steps[i] && pauseTimes.get(j)[1] < steps[i+1])
                    stepTime = median;
            }

            var += (mean - stepTime) * (mean - stepTime);
        }
        var = var/(steps.length-1);

        // Standard dev
        return (float)Math.sqrt(var);
    }

    // Coefficient of Variation of steps 100 * SD / mean
    public static float getStepCV(float standardDev, float mean) {
        return 100.0f * standardDev / mean;
    }

    public static float getGaitSymmetry(int[] steps, List<int[]> pauseTimes){

        int median = getMedian(steps);

        if (steps.length > 0) {
            float foot1Time = 0;
            float foot2Time = 0;

            for(int i=0; i<steps.length; i++){

                if (i % 2 == 0){
                    foot1Time += steps[i];
                } else {
                    foot2Time += steps[i];
                }

                for(int j=0; j<pauseTimes.size(); j++) {
                    if(pauseTimes.get(j)[0] > steps[i] && pauseTimes.get(j)[1] < steps[i+1])
                        foot1Time += 0;
                        foot2Time += 0;
                }

            }

            return foot1Time / foot2Time;
        }
        return 0;
    }

    private static double[] convertFloatsToDoubles(float[] input)
    {
        if (input == null)
        {
            return null;
        }
        double[] output = new double[input.length];
        for (int i = 0; i < input.length; i++)
        {
            output[i] = input[i];
        }
        return output;
    }

    private static float[] convertDoublesToFloats(double[] input)
    {
        if (input == null)
        {
            return null;
        }
        float[] output = new float[input.length];
        for (int i = 0; i < input.length; i++)
        {
            output[i] = (float)input[i];
        }
        return output;
    }
}
