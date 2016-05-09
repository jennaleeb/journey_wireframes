package com.uoft.journey.service;

import android.content.Context;

import com.uoft.journey.data.LocalDatabaseAccess;
import com.uoft.journey.data.ServerAccess;
import com.uoft.journey.entity.AccelerometerData;
import com.uoft.journey.entity.InhibitionGame;
import com.uoft.journey.entity.Trial;

import java.util.ArrayList;
import java.util.Date;

/**
 * Static methods for comms between activities and DB access
 */
public class DataService {

    // Gait Data
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

    public static boolean deleteTrial(Context ctx, int trialId, String username) {
        boolean success = LocalDatabaseAccess.deleteTrial(ctx, trialId);
        if(success)
            return ServerAccess.deleteTrial(trialId, username);

        return false;
    }

    // Inhibition Game Data
    public static int addNewInhibGame(Context ctx, int userId, Date startTime, String username) {
        return LocalDatabaseAccess.addInhibGame(ctx, userId, startTime, username);
    }


    public static InhibitionGame getGameByTrialId(Context ctx, int trialId, String username) {
        return LocalDatabaseAccess.getInhibGameByTrial(ctx, trialId, username);
    }
}
