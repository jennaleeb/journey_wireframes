package com.uoft.journey.data;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.uoft.journey.Journey;
import com.uoft.journey.entity.Patient;
import com.uoft.journey.ui.adapter.MainPagerAdapter;
import com.uoft.journey.ui.adapter.PatientListAdapter;

import java.util.ArrayList;

/**
 * Created by sukri on 2016-03-09.
 */
public class DownloadFriends extends AsyncTask<Void, String, Void> {

        private Context ct;
        PatientListAdapter   m;

        public DownloadFriends(Context ctx, PatientListAdapter mp) {
            ct = ctx;
            m = mp;

        }

        @Override
        protected Void doInBackground(Void... unused) {
            ServerAccess.getFriends(ct);
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

             ArrayList<Patient> users = LocalDatabaseAccess.getallUsers(ct);
            m.clearlist();
            int count = 0;
            for (Patient p: users){

                m.addPatient(p);
                count++;
            }

            Toast.makeText(ct, "Patients added ".concat(Integer.toString(count)), Toast.LENGTH_LONG).show();

            m.notifyDataSetChanged();

            super.onPostExecute(unused);


        }


}
