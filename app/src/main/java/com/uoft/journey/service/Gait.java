package com.uoft.journey.service;

/**
 * Signal processing methods
 * Some updated from MyWalk app
 */
public class Gait {

    //4th order Butterworth Lowpass Filter
    //Cutoff=4hz, fs=60;
    //Obtain these coefficients from MATLAB
    private static float[] aCoefficients = {0f,-2.90904949f,3.28399603f,-1.68764560f,0.33150344f};
    private static float[] bCoefficients = {0.00117528f,0.00470112f,0.00705167f,0.00471118f,0.00117528f};

    public static float[] lowPassFilter(float[] data) {
        float[] data_filt=new float[data.length];
        float[] input = {0,0,0,0,0};
        float[] output = {0,0,0,0,0};
        int bufferPosition=-1;

        //Filter the data, starting with the oldest in the buffer
        for(int i=0; i<data.length;i++) {
            //Increment the circular buffer position each time this is called
            bufferPosition = (bufferPosition+1) % (aCoefficients.length);  //error should not be acoeff-1
            //Store the input to the current circular buffer position
            input[bufferPosition] = data[i];
            output[bufferPosition] = 0;

            //Variable to fetch previous values in the buffer
            int delayPosition = bufferPosition;

            //Increment through difference equations starting from a1,b1 coefficients
            for (int j = 0; j < aCoefficients.length; j++) {
                output[bufferPosition] += bCoefficients[j]*input[delayPosition]-aCoefficients[j]*output[delayPosition];

                if(--delayPosition<0)delayPosition+=aCoefficients.length;//error should not be acoeff-1
            }

            data_filt[i]=output[bufferPosition];
        }
        return data_filt;
    }

    public static float[] autocorrelate(float[] data) {
        //Find mean and subtract from data
        float mean,sum=0;
        for(int m=0; m<data.length; ++m) {
            sum+=data[m];
        }
        mean=sum/data.length;
        for(int m=0; m<data.length; ++m) {
            data[m]-=mean;
        }

        //Log.d("Gait","data m: " + data[0]);
        //Log.d("Gait","sum " + sum);

        //Perform autocorrelation
        float[] acf= new float[data.length];
        for(int m=0; m<data.length-1; ++m) {
            acf[m]=0;
            for(int i=0; i<data.length-m; ++i) {
                acf[m]+=data[i]*data[i+m];
            }
            //Unbias the data
            //Log.d("Gait","Acf before m: " + acf[m]);
            acf[m]/=(data.length-m);
            //Log.d("Gait","Acf m: " + acf[m]);
        }

        for(int m=0; m<data.length-1; ++m) {
            //	acf[m]/=norm;
            acf[m]/=acf[0];
            //	Log.d("Gait","Acf: " + acf[0]);
        }

        return acf;
    }

    public static float[] simpleLowPassFilter(float[] data, float smoothing) {
        float value = data[0]; // start with the first input
        for (int i=1; i<data.length; i++){
            float currentValue = data[i];
            value += (currentValue - value) / smoothing;
            data[i] = value;
        }

        return data;
    }
}
