package com.uoft.journey.data;

import android.content.Context;
import android.util.Log;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasInvalidSessionException;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.Grant;
import com.baasbox.android.RequestToken;
import com.baasbox.android.SaveMode;
import com.google.gson.Gson;
import com.uoft.journey.entity.AccelerometerData;
import com.uoft.journey.entity.Trial;

import java.util.Date;
import java.util.List;

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

        Trial tdata = LocalDatabaseAccess.getTrial(ctx, trialID);
        tdata.setTrialData(null);

        if (tdata == null){


            System.out.println("tdata is null");

            return;
        }

        Gson gson = new Gson();

        BaasDocument newTrial = new BaasDocument("Trials");
        newTrial.putString("data",gson.toJson(tdata));



        //PUT /document/:collection/:id/:action/role/:rolename
        //PUT /document/posts/aaa-bbb-ccc-ddd/read/role/friends_of_user_a

        String friend_of = "friends_of_" + BaasUser.current().getName();

        // assuming doc is an instance of the document


        mAddToken = newTrial.save(SaveMode.IGNORE_VERSION,uploadHandler);

        /*newTrial.grant(Grant.READ,friend_of,new BaasHandler<Void>() {
            @Override
            public void handle(BaasResult<Void> res) {
                if (res.isSuccess()) {
                    Log.d("LOG","Permission granted");
                } else {
                    Log.e("LOG","Error",res.error());
                }
            }
        });*/

    }

    public static void getTrialforUser(Context ctx, int userid) {

        BaasResult<java.util.List<BaasDocument>> res  = BaasDocument.fetchAllSync("Trials");
        Gson gson = new Gson();

        for (BaasDocument doc : res.value()) {
            Log.d("LOG", "Doc: " + doc);

            Trial t = gson.fromJson(doc.getString("data"), Trial.class);


            LocalDatabaseAccess.insertTrial(ctx, userid, t);
        }


    }
}
