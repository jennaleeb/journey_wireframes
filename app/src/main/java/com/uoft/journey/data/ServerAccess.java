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
import com.uoft.journey.entity.Trial;

/**
 * Created by Charlie on 11/02/2016.
 */
public class ServerAccess {



    private static RequestToken mAddToken;
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

    public static void addTrial(Context ctx, int trialID) {
        Journey mApp;
        mApp = ((Journey)ctx.getApplicationContext());

        Trial tdata = LocalDatabaseAccess.getTrial(ctx, trialID, mApp.getUsername());
        tdata.setTrialData(null);

        if (tdata == null){
            System.out.println("tdata is null");
            return;
        }

        Gson gson = new Gson();

        BaasDocument newTrial = new BaasDocument("Trials");
        newTrial.putString("data",gson.toJson(tdata));


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

        BaasQuery.Criteria filter = BaasQuery.builder()
                .where("_author = ?")
                .whereParams(username)
                .criteria();
        BaasResult<java.util.List<BaasDocument>> res = BaasDocument.fetchAllSync("Trials", filter);
        Gson gson = new Gson();


        for (BaasDocument doc : res.value()) {
            Log.d("LOG", "Doc: " + doc);

            Trial t = gson.fromJson(doc.getString("data"), Trial.class);


            LocalDatabaseAccess.insertTrial(ctx, userid, t, username);
        }

    }

    public static Boolean addFriend(Context ctx, String username) {

        BaasUser user = BaasUser.withUserName(username);

        BaasResult<BaasUser> res = user.followSync();

        if(res.isSuccess()) {
            JsonObject profile = res.value().getScope(BaasUser.Scope.FRIEND);
            LocalDatabaseAccess.addUser(ctx, res.value().getName());
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
            LocalDatabaseAccess.addUser(ctx, u.getName());
        }

    }
}
