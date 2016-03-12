package com.uoft.journey.data;

import android.content.Context;
import android.os.AsyncTask;

import com.baasbox.android.BaasUser;
import com.uoft.journey.ui.adapter.MainPagerAdapter;

/**
 * Created by sukri on 2016-03-09.
 */
public class DownloadTrials extends AsyncTask<Void, String, Void> {

        private Context ct;
        private int UserID;
    private String user;
        MainPagerAdapter m;

        public DownloadTrials(Context ctx, String username, int userID, MainPagerAdapter mp) {
            ct = ctx;
            UserID = userID;
            m = mp;
            user = username;

        }

        @Override
        protected Void doInBackground(Void... unused) {
            ServerAccess.getTrialforUser(ct, UserID, user);
            //ServerAccess.getFriends();
            //ServerAccess.getTrialforFriend(ct, UserID, BaasUser.current().getName());
            return (null);
        }

        @Override
        protected void onPreExecute() {

                 super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(String... item) {

        }

        @Override
        protected void onPostExecute(Void unused) {

            m.pageChange(0);

            super.onPostExecute(unused);


        }


}
