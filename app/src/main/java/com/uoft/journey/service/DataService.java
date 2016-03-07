package com.uoft.journey.service;

import android.content.Context;

import com.uoft.journey.data.LocalDatabaseAccess;
import com.uoft.journey.entity.AccelerometerData;
import com.uoft.journey.entity.Trial;

import java.util.ArrayList;
import java.util.Date;

/**
 * Static methods for comms between activities and DB access
 */
public class DataService {

    public static int addNewTrial(Context ctx, int userId, Date startTime) {
        return LocalDatabaseAccess.addTrial(ctx, userId, startTime);
    }

    public static AccelerometerData getTrialData(Context ctx, int trialId) {
        return LocalDatabaseAccess.getTrialData(ctx, trialId);
    }

    public static Trial getTrial(Context ctx, int trialId) {
        return LocalDatabaseAccess.getTrial(ctx, trialId);
    }

    public static ArrayList<Trial> getTrialsForUser(Context ctx, int userId) {
        return LocalDatabaseAccess.getTrialsForUser(ctx, userId);
    }
}
