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
    public static final String TABLE_TRIAL_PAUSE = "trialPause";
    public static final String TABLE_INHIB_GAME = "inhibGame";
    // Columns - User
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_ACTUAL_NAME = "actualname";
    public static final String COLUMN_USER_DATE = "date";

    // Columns - Trial
    public static final String COLUMN_TRIAL_ID = "id";
    public static final String COLUMN_TRIAL_USER_ID = "userId";
    public static final String COLUMN_TRIAL_START_TIME = "startTime";
    public static final String COLUMN_TRIAL_MEAN_STEP_TIME = "meanStepTime";
    public static final String COLUMN_TRIAL_STEP_SD = "stepSD";
    public static final String COLUMN_TRIAL_STEP_CV = "stepCV";
    public static final String COLUMN_TRIAL_GAIT_SYMM = "gaitSymm";
    public static final String COLUMN_TRIAL_CADENCE = "cadence";
    public static final String COLUMN_TRIAL_MEAN_STRIDE_TIME = "meanStrideTime";
    public static final String COLUMN_TRIAL_STRIDE_SD = "strideSD";
    public static final String COLUMN_TRIAL_STRIDE_CV = "strideCV";
    public static final String COLUMN_TRIAL_GAME_PLAYED = "gamePlayed";
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
    // Columns - TrialStep
    public static final String COLUMN_TRIAL_PAUSE_TRIAL_ID = "trialId";
    public static final String COLUMN_TRIAL_PAUSE_START = "startPause";
    public static final String COLUMN_TRIAL_PAUSE_END = "endPause";

    // Columns - InhibGame
    public static final String COLUMN_GAME_ID = "id";
    public static final String COLUMN_GAME_USER_ID = "userId";
    public static final String COLUMN_GAME_TRIAL_ID = "trialId";
    public static final String COLUMN_GAME_START_TIME = "startTime";
    public static final String COLUMN_GAME_HIT_COUNT = "hitCount";
    public static final String COLUMN_GAME_MISS_COUNT = "missCount";
    public static final String COLUMN_GAME_FALSE_ALARM_COUNT = "falseAlarmCount";
    public static final String COLUMN_GAME_CORRECT_NEG_COUNT = "correctNegCount";
    public static final String COLUMN_GAME_OM_ERROR = "omissionError";
    public static final String COLUMN_GAME_COM_ERROR = "commissionError";
    public static final String COLUMN_GAME_MEAN_RT = "meanRT";
    public static final String COLUMN_GAME_RT_SD = "stdevRT";
    public static final String COLUMN_GAME_MEAN_RT_FALSE_ALARM = "meanRTFalseAlarm";
    public static final String COLUMN_GAME_RT_SD_FALSE_ALARM = "stdevRTFalseAlarm";
    public static final String COLUMN_GAME_OVERALL_ACCURACY = "overallAccuracy";
    public static final String COLUMN_GAME_LEVEL = "gameLevel";
    public static final String COLUMN_GAME_USER_NAME = "name";

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
        db.execSQL("CREATE TABLE user (id INTEGER , name TEXT PRIMARY KEY, actualname TEXT, date TEXT);");
        db.execSQL("CREATE TABLE trial (id INTEGER PRIMARY KEY, userId INTEGER, startTime TEXT, meanStepTime REAL, stepSD REAL, stepCV REAL, gaitSymm REAL, cadence REAL, meanStrideTime REAL, strideSD REAL, strideCV REAL, gamePlayed INTEGER, name TEXT);");
        db.execSQL("CREATE TABLE trialData (trialId INTEGER, elapsedTime INTEGER, xVal REAL, yVal REAL, zVal REAL, xProcessed REAL, yProcessed REAL, zProcessed REAL, FOREIGN KEY(trialId) REFERENCES trial(id));");
        db.execSQL("CREATE TABLE trialStep (trialId INTEGER, stepNum INTEGER, elapsedTime INTEGER, FOREIGN KEY(trialId) REFERENCES trial(id));");
        db.execSQL("CREATE TABLE trialPause (trialId INTEGER, startPause INTEGER, endPause INTEGER, FOREIGN KEY(trialId) REFERENCES trial(id));");
        db.execSQL("CREATE TABLE inhibGame (id INTEGER PRIMARY KEY, userId INTEGER, trialId INTEGER, startTime TEXT, hitCount INTEGER, missCount INTEGER, falseAlarmCount INTEGER, correctNegCount INTEGER, omissionError REAL, commissionError REAL, meanRT REAL, stdevRT REAL, meanRTFalseAlarm REAL, stdevRTFalseAlarm REAL, overallAccuracy REAL, gameLevel INTEGER, name TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new RuntimeException("How did we get here?");
    }
}
