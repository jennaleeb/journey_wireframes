package com.uoft.journey.data;

import android.content.Context;
import android.os.AsyncTask;

import com.uoft.journey.ui.adapter.MainPagerAdapter;

/**
 * Download from server
 */
public class DownloadTrials extends AsyncTask<Void, String, Boolean> {

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
        protected Boolean doInBackground(Void... unused) {
            try {
                ServerAccess.getTrialforUser(ct, UserID, user);
                return true;
                //ServerAccess.getFriends();
                //ServerAccess.getTrialforFriend(ct, UserID, BaasUser.current().getName());
            }
            catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPreExecute() {
             super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(String... item) {

        }

        @Override
        protected void onPostExecute(Boolean success) {
            m.trialsLoaded(success);
            super.onPostExecute(success);
        }
}
