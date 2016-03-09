package com.uoft.journey.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.uoft.journey.entity.AccelerometerData;
import com.uoft.journey.entity.Patient;
import com.uoft.journey.entity.Trial;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Methods for Local db CRUD
 */
public class LocalDatabaseAccess {

    // Insert a new user
    public static Integer addUser(Context ctx, String name) {
        try {
            LocalDatabaseHelper db = LocalDatabaseHelper.getInstance(ctx.getApplicationContext());
            ContentValues cv=new ContentValues();
            Cursor max = db.getReadableDatabase().rawQuery(String.format("SELECT max(%s) FROM %s", LocalDatabaseHelper.COLUMN_USER_ID, LocalDatabaseHelper.TABLE_USER), null);
            int nextId = 1;
            if(max.moveToFirst()) {
                nextId += max.getInt(0);
            }
            max.close();

            cv.put(LocalDatabaseHelper.COLUMN_USER_ID, nextId);
            cv.put(LocalDatabaseHelper.COLUMN_USER_NAME, name);
            db.getWritableDatabase().insert(LocalDatabaseHelper.TABLE_USER, null, cv);
            return nextId;
        }
        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // User for testing
    public static Patient getTestUser(Context ctx, String name) {
        try {
            LocalDatabaseHelper db = LocalDatabaseHelper.getInstance(ctx.getApplicationContext());
            ContentValues cv=new ContentValues();
            Cursor id = db.getReadableDatabase().rawQuery(String.format("SELECT %s FROM %s WHERE %s='%s'", LocalDatabaseHelper.COLUMN_USER_ID,
                    LocalDatabaseHelper.TABLE_USER, LocalDatabaseHelper.COLUMN_USER_NAME, name), null);
            int userId = 1;
            if(id.moveToFirst()) {
                userId = id.getInt(0);
                id.close();
            }
            else {
                id.close();
                userId = addUser(ctx, name);
            }

            return new Patient(userId, name, null);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Insert a trial
    public static Integer addTrial(Context ctx, int userId, Date startTime) {
        try {
            LocalDatabaseHelper db = LocalDatabaseHelper.getInstance(ctx.getApplicationContext());
            ContentValues cv=new ContentValues();
            Cursor max = db.getReadableDatabase().rawQuery(String.format("SELECT max(%s) FROM %s", LocalDatabaseHelper.COLUMN_USER_ID, LocalDatabaseHelper.TABLE_TRIAL), null);
            int nextId = 1;
            if(max.moveToFirst()) {
                nextId += max.getInt(0);
            }
            max.close();

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);

            cv.put(LocalDatabaseHelper.COLUMN_TRIAL_ID, nextId);
            cv.put(LocalDatabaseHelper.COLUMN_TRIAL_USER_ID, userId);
            cv.put(LocalDatabaseHelper.COLUMN_TRIAL_START_TIME, df.format(startTime));
            db.getWritableDatabase().insert(LocalDatabaseHelper.TABLE_TRIAL, null, cv);
            return nextId;
        }
        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Insert a trial retreived from fromserver
    public static Boolean insertTrial(Context ctx, int userId, Trial trial) {
        try {
            LocalDatabaseHelper db = LocalDatabaseHelper.getInstance(ctx.getApplicationContext());
            ContentValues cv=new ContentValues();

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);

            cv.put(LocalDatabaseHelper.COLUMN_TRIAL_ID, trial.getTrialId());
            cv.put(LocalDatabaseHelper.COLUMN_TRIAL_USER_ID, userId);
            cv.put(LocalDatabaseHelper.COLUMN_TRIAL_START_TIME, df.format(trial.getStartTime()));
            cv.put(LocalDatabaseHelper.COLUMN_TRIAL_MEAN_STRIDE_TIME, trial.getMeanStrideTime());
            cv.put(LocalDatabaseHelper.COLUMN_TRIAL_STANDARD_DEV, trial.getCoeffOfVar());
            cv.put(LocalDatabaseHelper.COLUMN_TRIAL_COEFF_OF_VAR, trial.getCoeffOfVar());
            db.getWritableDatabase().insertWithOnConflict(LocalDatabaseHelper.TABLE_TRIAL, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Boolean updateTrial(Context ctx, Trial trial) {
        try {
            LocalDatabaseHelper db = LocalDatabaseHelper.getInstance(ctx.getApplicationContext());
            ContentValues cv = new ContentValues();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
            cv.put(LocalDatabaseHelper.COLUMN_TRIAL_START_TIME, df.format(trial.getStartTime()));
            cv.put(LocalDatabaseHelper.COLUMN_TRIAL_MEAN_STRIDE_TIME, trial.getMeanStrideTime());
            cv.put(LocalDatabaseHelper.COLUMN_TRIAL_STANDARD_DEV, trial.getStandardDev());
            cv.put(LocalDatabaseHelper.COLUMN_TRIAL_COEFF_OF_VAR, trial.getCoeffOfVar());
            db.getWritableDatabase().update(LocalDatabaseHelper.TABLE_TRIAL, cv, LocalDatabaseHelper.COLUMN_TRIAL_ID + "=" + trial.getTrialId(), null);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Insert trial data
    public static Boolean addTrialData(Context ctx, int trialId, AccelerometerData data) {
        try {
            LocalDatabaseHelper db = LocalDatabaseHelper.getInstance(ctx.getApplicationContext());
            ContentValues cv=new ContentValues();

            for(int i=0; i<data.getDataCount(); i++) {
                cv.clear();
                cv.put(LocalDatabaseHelper.COLUMN_TRIAL_DATA_TRIAL_ID, trialId);
                cv.put(LocalDatabaseHelper.COLUMN_TRIAL_DATA_ELAPSED_TIME, data.getElapsedTimestamps()[i]);
                cv.put(LocalDatabaseHelper.COLUMN_TRIAL_DATA_X_VAL, data.getAccelDataX()[i]);
                cv.put(LocalDatabaseHelper.COLUMN_TRIAL_DATA_Y_VAL, data.getAccelDataY()[i]);
                cv.put(LocalDatabaseHelper.COLUMN_TRIAL_DATA_Z_VAL, data.getAccelDataZ()[i]);
                db.getWritableDatabase().insert(LocalDatabaseHelper.TABLE_TRIAL_DATA, null, cv);
            }

            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update processed trial data
    public static Boolean updateProcessedTrialData(Context ctx, AccelerometerData data) {
        try {
            LocalDatabaseHelper db = LocalDatabaseHelper.getInstance(ctx.getApplicationContext());
            ContentValues cv=new ContentValues();

            for(int i=0; i<data.getDataCount(); i++) {
                cv.clear();
                cv.put(LocalDatabaseHelper.COLUMN_TRIAL_DATA_X_PROCESSED, data.getProcessedX()[i]);
                cv.put(LocalDatabaseHelper.COLUMN_TRIAL_DATA_Y_PROCESSED, data.getProcessedY()[i]);
                cv.put(LocalDatabaseHelper.COLUMN_TRIAL_DATA_Z_PROCESSED, data.getProcessedZ()[i]);
                db.getWritableDatabase().update(LocalDatabaseHelper.TABLE_TRIAL_DATA, cv, "rowid=" + data.getTrialDataIds()[i], null);
            }

            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Add the trial steps
    public static Boolean addTrialSteps(Context ctx, int trialId, int[] stepTimes) {
        try {
            LocalDatabaseHelper db = LocalDatabaseHelper.getInstance(ctx.getApplicationContext());
            ContentValues cv=new ContentValues();

            for(int i=0; i<stepTimes.length; i++) {
                cv.clear();
                cv.put(LocalDatabaseHelper.COLUMN_TRIAL_STEP_STEP_NUM, i + 1);
                cv.put(LocalDatabaseHelper.COLUMN_TRIAL_STEP_ELAPSED_TIME, stepTimes[i]);
                cv.put(LocalDatabaseHelper.COLUMN_TRIAL_STEP_TRIAL_ID, trialId);
                db.getWritableDatabase().insert(LocalDatabaseHelper.TABLE_TRIAL_STEP, null, cv);
            }

            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get the list of trials for user
    public static ArrayList<Trial> getTrialsForUser(Context ctx, int userId) {
        try {
            LocalDatabaseHelper db = LocalDatabaseHelper.getInstance(ctx.getApplicationContext());
            Cursor data = db.getReadableDatabase().rawQuery(String.format("SELECT %s, %s, %s, %s, %s FROM %s WHERE userId=%d ORDER BY %s DESC", LocalDatabaseHelper.COLUMN_TRIAL_ID,
                    LocalDatabaseHelper.COLUMN_TRIAL_START_TIME, LocalDatabaseHelper.COLUMN_TRIAL_MEAN_STRIDE_TIME, LocalDatabaseHelper.COLUMN_TRIAL_STANDARD_DEV,
                    LocalDatabaseHelper.COLUMN_TRIAL_COEFF_OF_VAR, LocalDatabaseHelper.TABLE_TRIAL, userId, LocalDatabaseHelper.COLUMN_TRIAL_START_TIME), null);

            ArrayList<Trial> trials = new ArrayList<>();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
            while(data.moveToNext()) {
                Date startTime = null;
                if(!data.isNull(1)) {
                    startTime = df.parse(data.getString(1));
                }
                Trial trial = new Trial(data.getInt(0), startTime, null);

                if(!data.isNull(2)) {
                    trial.setStepAnalysis(data.getFloat(2), data.getFloat(3), data.getFloat(4));
                }

                trial.setStepTimes(getTrialStepTimes(ctx, trial.getTrialId()));
                trials.add(trial);

            }
            data.close();

            return trials;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get all data entries for a trial
    public static AccelerometerData getTrialData(Context ctx, int trialId) {
        try {
            LocalDatabaseHelper db = LocalDatabaseHelper.getInstance(ctx.getApplicationContext());
            Cursor data = db.getReadableDatabase().rawQuery(String.format("SELECT rowid, %s, %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s=%d ORDER BY %s", LocalDatabaseHelper.COLUMN_TRIAL_DATA_TRIAL_ID,
                    LocalDatabaseHelper.COLUMN_TRIAL_DATA_ELAPSED_TIME, LocalDatabaseHelper.COLUMN_TRIAL_DATA_X_VAL, LocalDatabaseHelper.COLUMN_TRIAL_DATA_Y_VAL, LocalDatabaseHelper.COLUMN_TRIAL_DATA_Z_VAL,
                    LocalDatabaseHelper.COLUMN_TRIAL_DATA_X_PROCESSED, LocalDatabaseHelper.COLUMN_TRIAL_DATA_Y_PROCESSED, LocalDatabaseHelper.COLUMN_TRIAL_DATA_Z_PROCESSED,
                    LocalDatabaseHelper.TABLE_TRIAL_DATA, LocalDatabaseHelper.COLUMN_TRIAL_DATA_TRIAL_ID, trialId, LocalDatabaseHelper.COLUMN_TRIAL_DATA_ELAPSED_TIME), null);

            ArrayList<Integer> rowIds = new ArrayList<>();
            ArrayList<Integer> elapsedTime = new ArrayList<>();
            ArrayList<Float> xVals = new ArrayList<>();
            ArrayList<Float> yVals = new ArrayList<>();
            ArrayList<Float> zVals = new ArrayList<>();
            ArrayList<Float> xProcessed = new ArrayList<>();
            ArrayList<Float> yProcessed = new ArrayList<>();
            ArrayList<Float> zProcessed = new ArrayList<>();
            boolean isProcessed = true;

            while(data.moveToNext()) {
                rowIds.add(data.getInt(0));
                elapsedTime.add(data.getInt(2));
                xVals.add(data.getFloat(3));
                yVals.add(data.getFloat(4));
                zVals.add(data.getFloat(5));

                if(isProcessed && data.isNull(7)) {
                    isProcessed = false;
                }

                if(isProcessed) {
                    xProcessed.add(data.getFloat(6));
                    yProcessed.add(data.getFloat(7));
                    zProcessed.add(data.getFloat(8));
                }
            }

            data.close();

            int[] idArray = new int[rowIds.size()];
            int[] elapsedArray = new int[elapsedTime.size()];
            float[] xArray = new float[xVals.size()];
            float[] yArray = new float[yVals.size()];
            float[] zArray = new float[zVals.size()];
            for(int i=0; i<xVals.size(); i++) {
                idArray[i] = rowIds.get(i);
                elapsedArray[i] = elapsedTime.get(i);
                xArray[i] = xVals.get(i);
                yArray[i] = yVals.get(i);
                zArray[i] = zVals.get(i);
            }

            AccelerometerData acc = new AccelerometerData(idArray, elapsedArray, xArray, yArray, zArray);

            if(isProcessed || (yProcessed.size() > yVals.size() - 20 && yProcessed.size() > 0)) {
                float[] xProc = new float[xProcessed.size()];
                float[] yProc = new float[yProcessed.size()];
                float[] zProc = new float[zProcessed.size()];

                for(int i=0; i<xProcessed.size(); i++) {
                    xProc[i] = xProcessed.get(i);
                    yProc[i] = yProcessed.get(i);
                    zProc[i] = zProcessed.get(i);
                }

                acc.addProcessedData(xProc, yProc, zProc);
            }

            return acc;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Trial getTrial(Context ctx, int trialId) {
        try {
            LocalDatabaseHelper db = LocalDatabaseHelper.getInstance(ctx.getApplicationContext());
            Cursor data = db.getReadableDatabase().rawQuery(String.format("SELECT %s, %s, %s, %s, %s FROM %s WHERE id=%d", LocalDatabaseHelper.COLUMN_TRIAL_USER_ID,
                    LocalDatabaseHelper.COLUMN_TRIAL_START_TIME, LocalDatabaseHelper.COLUMN_TRIAL_MEAN_STRIDE_TIME, LocalDatabaseHelper.COLUMN_TRIAL_STANDARD_DEV,
                    LocalDatabaseHelper.COLUMN_TRIAL_COEFF_OF_VAR, LocalDatabaseHelper.TABLE_TRIAL, trialId), null);

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
            data.moveToNext();
            Date startTime = null;
            if(!data.isNull(1)) {
                startTime = df.parse(data.getString(1));
            }
            Trial trial = new Trial(trialId, startTime, null);

            if(!data.isNull(2)) {
                trial.setStepAnalysis(data.getFloat(2), data.getFloat(3), data.getFloat(4));
            }

            AccelerometerData accData = getTrialData(ctx, trialId);
            if(accData != null)
                trial.setTrialData(accData);

            int[] steps = getTrialStepTimes(ctx, trialId);
            if(steps != null)
                trial.setStepTimes(steps);

            data.close();
            return trial;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int[] getTrialStepTimes(Context ctx, int trialId) {
        try {
            LocalDatabaseHelper db = LocalDatabaseHelper.getInstance(ctx.getApplicationContext());
            Cursor data = db.getReadableDatabase().rawQuery(String.format("SELECT %s FROM %s WHERE %s=%d ORDER BY %s ASC", LocalDatabaseHelper.COLUMN_TRIAL_STEP_ELAPSED_TIME,
                    LocalDatabaseHelper.TABLE_TRIAL_STEP, LocalDatabaseHelper.COLUMN_TRIAL_STEP_TRIAL_ID, trialId, LocalDatabaseHelper.COLUMN_TRIAL_STEP_ELAPSED_TIME), null);

            ArrayList<Integer> times = new ArrayList<>();
            while(data.moveToNext()) {
                times.add(data.getInt(0));
            }

            data.close();

            int[] timesArray = new int[times.size()];
            for(int i=0; i<times.size(); i++) {
                timesArray[i] = times.get(i);
            }

            return timesArray;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
