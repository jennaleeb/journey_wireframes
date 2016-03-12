package com.uoft.journey.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Creates the local DB and holds all column/table information
 */
public class LocalDatabaseHelper extends SQLiteOpenHelper {
    private static LocalDatabaseHelper mInstance = null;
    private Context mContext;
    private static final String DATABASE_NAME="journeyLocal.db";
    private static final int SCHEMA=1;
    // Tables
    public static final String TABLE_USER = "user";
    public static final String TABLE_TRIAL = "trial";
    public static final String TABLE_TRIAL_DATA = "trialData";
    public static final String TABLE_TRIAL_STEP = "trialStep";
    // Columns - User
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_NAME = "name";
    // Columns - Trial
    public static final String COLUMN_TRIAL_ID = "id";
    public static final String COLUMN_TRIAL_USER_ID = "userId";
    public static final String COLUMN_TRIAL_START_TIME = "startTime";
    public static final String COLUMN_TRIAL_MEAN_STRIDE_TIME = "meanStrideTime";
    public static final String COLUMN_TRIAL_STANDARD_DEV = "standardDev";
    public static final String COLUMN_TRIAL_COEFF_OF_VAR = "coeffOfVar";
    public static final String COLUMN_TRIAL_USER_NAME = "name";

    // Columns - TrialData
    public static final String COLUMN_TRIAL_DATA_TRIAL_ID = "trialId";
    public static final String COLUMN_TRIAL_DATA_ELAPSED_TIME = "elapsedTime";
    public static final String COLUMN_TRIAL_DATA_X_VAL = "xVal";
    public static final String COLUMN_TRIAL_DATA_Y_VAL = "yVal";
    public static final String COLUMN_TRIAL_DATA_Z_VAL = "zVal";
    public static final String COLUMN_TRIAL_DATA_X_PROCESSED = "xProcessed";
    public static final String COLUMN_TRIAL_DATA_Y_PROCESSED = "yProcessed";
    public static final String COLUMN_TRIAL_DATA_Z_PROCESSED = "zProcessed";
    // Columns - TrialStep
    public static final String COLUMN_TRIAL_STEP_TRIAL_ID = "trialId";
    public static final String COLUMN_TRIAL_STEP_STEP_NUM = "stepNum";
    public static final String COLUMN_TRIAL_STEP_ELAPSED_TIME = "elapsedTime";

    private LocalDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
        mContext = context;
    }

    public static LocalDatabaseHelper getInstance(Context ctx) {
        // Use singleton pattern
        if (mInstance == null) {
            mInstance = new LocalDatabaseHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the tables
        db.execSQL("CREATE TABLE user (id INTEGER , name TEXT PRIMARY KEY );");
        db.execSQL("CREATE TABLE trial (id INTEGER PRIMARY KEY, userId INTEGER, startTime TEXT, meanStrideTime REAL, standardDev REAL, coeffOfVar REAL, name TEXT);");
        db.execSQL("CREATE TABLE trialData (trialId INTEGER, elapsedTime INTEGER, xVal REAL, yVal REAL, zVal REAL, xProcessed REAL, yProcessed REAL, zProcessed REAL, FOREIGN KEY(trialId) REFERENCES trial(id));");
        db.execSQL("CREATE TABLE trialStep (trialId INTEGER, stepNum INTEGER, elapsedTime INTEGER, FOREIGN KEY(trialId) REFERENCES trial(id));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new RuntimeException("How did we get here?");
    }
}
