package com.uoft.journey.data;

import android.content.Context;
import android.util.Log;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasInvalidSessionException;
import com.baasbox.android.BaasQuery;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.Grant;
import com.baasbox.android.RequestToken;
import com.baasbox.android.Role;
import com.baasbox.android.SaveMode;
import com.baasbox.android.json.JsonObject;
import com.google.gson.Gson;
import com.uoft.journey.Journey;
import com.uoft.journey.entity.InhibitionGame;
import com.uoft.journey.entity.Trial;

import java.util.List;

/**
 * Methods for reading and writing to server
 */
public class ServerAccess {



    private static RequestToken mAddToken;
    private static String TAG = "SERVERTAG";
    // private RequestToken mAddToken;

    private static final BaasHandler<BaasDocument> uploadHandler =
            new BaasHandler<BaasDocument>() {
                @Override
                public void handle(BaasResult<BaasDocument> doc) {
                    mAddToken = null;

                    if (doc.isSuccess()) {
                        Log.d("SUCCESS", "upload successful", doc.error());
                        grantPermission(doc.value());


                    } else {
                        if (doc.error() instanceof BaasInvalidSessionException) {
                            Log.d("ERROR", "upload  error", doc.error());

                        } else {
                            Log.d("ERROR", "upload fail error", doc.error());
                        }
                    }
                }
            };

    public static void addTrial(Context ctx, int trialID, String username) {
        Journey mApp;
        mApp = ((Journey)ctx.getApplicationContext());

        System.out.println("SERVERADDTRIAL THE USERNAME IS: "+ username);

        Trial tdata = LocalDatabaseAccess.getTrial(ctx, trialID, username);


        InhibitionGame game = LocalDatabaseAccess.getInhibGameByTrial(ctx, trialID, username);

        tdata.setTrialData(null);

        if (tdata == null){
            System.out.println("tdata is null");
            return;
        }

        Gson gson = new Gson();

        BaasDocument newTrial = new BaasDocument("Trials");

        // If the trial included a game...
        if (game != null) {
            newTrial.put("gameStats", (new Gson()).toJson(game));
        }

        newTrial.put("data", gson.toJson(tdata));



        mAddToken = newTrial.save(SaveMode.IGNORE_VERSION,uploadHandler);


    }

    public static void grantPermission(BaasDocument doc) {


        String friend_of = "friends_of_".concat(BaasUser.current().getName());


        doc.grantAll(Grant.READ, Role.friendsOf(BaasUser.current().getName()), new BaasHandler<Void>() {
            @Override
            public void handle(BaasResult<Void> res) {
                if (res.isSuccess()) {
                    Log.d("LOG", "Permission granted");
                } else {
                    Log.e("LOG", "Error", res.error());
                }
            }
        });

        doc.grantAll(Grant.ALL, Role.REGISTERED, new BaasHandler<Void>() {
            @Override
            public void handle(BaasResult<Void> res) {
                if (res.isSuccess()) {
                    Log.d("LOG", "Permission granted");
                } else {
                    Log.e("LOG", "Error", res.error());
                }
            }
        });

    }


    /*    public static void getTrialforUser(Context ctx, int userid, String username) {

        BaasResult<java.util.List<BaasDocument>> res  = BaasDocument.fetchAllSync("Trials");
        Gson gson = new Gson();

        for (BaasDocument doc : res.value()) {
            Log.d("LOG", "Doc: " + doc);

            Trial t = gson.fromJson(doc.getString("data"), Trial.class);


            LocalDatabaseAccess.insertTrial(ctx, userid, t, username);
        }

        getTriallforUser(ctx,userid, username);

    }*/

    public static void getTrialforUser(Context ctx, int userid, String username) {
      //  \"mUsername\":\"jo4@gmail.com\",
        //\"mTrialId\":1
        BaasQuery.Criteria filter = BaasQuery.builder()
               // .where("_author = ?")
                .where(String.format("(data like '%%\"mUsername\":\"%s\"}%%' or data like '%%\"mUsername\":\"%s\",%%')", username, username))
               // .whereParams(username)
                .criteria();
        BaasResult<java.util.List<BaasDocument>> res = BaasDocument.fetchAllSync("Trials", filter);
        Gson gson = new Gson();


        for (BaasDocument doc : res.value()) {

            // Get trial data
            Trial t = gson.fromJson(doc.getString("data"), Trial.class);

            LocalDatabaseAccess.insertTrial(ctx, userid, t, username);
            LocalDatabaseAccess.addTrialSteps(ctx, t.getTrialId(), t.getStepTimes(), t.getPauseTimes());

            // Get game data
            gson.fromJson(doc.getString("gameStats"), InhibitionGame.class);

            InhibitionGame g = gson.fromJson(doc.getString("gameStats"), InhibitionGame.class);

            if (g != null) {
                LocalDatabaseAccess.insertInhibGame(ctx, userid, g, username);
            }


        }

    }

    public static Boolean addFriend(Context ctx, String username) {

        BaasUser user = BaasUser.withUserName(username);

        BaasResult<BaasUser> res = user.followSync();

        if(res.isSuccess()) {
            JsonObject profile = res.value().getScope(BaasUser.Scope.PUBLIC);
            String actual = profile.getString("actualname");
            LocalDatabaseAccess.addUser(ctx, res.value().getName(), actual, res.value().getSignupDate());
            Log.d("LOG", "Success adding friend: It's profile " + profile);
            return true;
        } else{

            Log.e("LOG", "Error", res.error());
            return false;

        }
    }

    public static void getFriends(Context ctx) {

        BaasUser user = BaasUser.current(); // current user


        BaasResult<java.util.List<BaasUser>> res = user.followingSync();
        for(BaasUser u: res.value()) {
            String actual =  u.getScope(BaasUser.Scope.PUBLIC).getString("actualname");
            LocalDatabaseAccess.addUser(ctx, u.getName(), actual, u.getSignupDate());
        }

    }

    public static Boolean updateTrial(Context ctx, int trialId, String username) {


        BaasQuery.Criteria filter = BaasQuery.builder()
                .where(String.format("(data like '%%\"mUsername\":\"%s\"}%%' or data like '%%\"mUsername\":\"%s\",%%') and (data like '%%\"mTrialId\":%d}%%' or data like '%%\"mTrialId\":%d,%%')", username, username, trialId, trialId))
                .criteria();

        try {

            BaasResult<List<BaasDocument>> res = BaasDocument.fetchAllSync("Trials", filter);
            // Will only be one trial
            for (BaasDocument doc : res.value()) {

                Trial new_trial = LocalDatabaseAccess.getTrial(ctx, trialId, username);
                new_trial.setTrialData(null);

                Gson gson = new Gson();

                doc.put("data", gson.toJson(new_trial));

                doc.save(SaveMode.IGNORE_VERSION, new BaasHandler<BaasDocument>() {
                    @Override
                    public void handle(BaasResult<BaasDocument> res) {
                        if (res.isSuccess()) {
                            Log.d("LOG", "Document saved " + res.value().getId());
                        } else {
                            Log.e("LOG", "Error", res.error());
                        }
                    }
                });
            }
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "updateTrial()", e);
            return false;
        }
    }


    public static Boolean deleteTrial(int trialId, String username) {
        BaasQuery.Criteria filter = BaasQuery.builder()
                .where(String.format("(data like '%%\"mUsername\":\"%s\"}%%' or data like '%%\"mUsername\":\"%s\",%%') and (data like '%%\"mTrialId\":%d}%%' or data like '%%\"mTrialId\":%d,%%')", username, username, trialId, trialId))
                .criteria();

        try {

            BaasResult<java.util.List<BaasDocument>> res = BaasDocument.fetchAllSync("Trials", filter);
            // Will only be one trial
            for (BaasDocument doc : res.value()) {
                doc.delete(new BaasHandler<Void>() {
                    @Override
                    public void handle(BaasResult<Void> res) {
                        if (res.isSuccess()) {
                            Log.d("LOG", "Document deleted");
                        } else {
                            Log.e("LOG", "error", res.error());
                        }
                    }
                });
            }
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
