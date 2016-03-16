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

    public static int addNewTrial(Context ctx, int userId, Date startTime, String username) {
        return LocalDatabaseAccess.addTrial(ctx, userId, startTime, username);
    }

    public static AccelerometerData getTrialData(Context ctx, int trialId) {
        return LocalDatabaseAccess.getTrialData(ctx, trialId);
    }

    public static Trial getTrial(Context ctx, int trialId, String username) {
        return LocalDatabaseAccess.getTrial(ctx, trialId, username);
    }

    public static ArrayList<Trial> getTrialsForUser(Context ctx, String username) {
        return LocalDatabaseAccess.getTrialsForUser(ctx, username);
    }
}
