package com.uoft.journey.data;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.uoft.journey.entity.Patient;
import com.uoft.journey.ui.adapter.PatientListAdapter;

import java.util.ArrayList;

/**
 * Created by sukri on 2016-03-09.
 */
public class AddPatient extends AsyncTask<Void, String, Void> {

        private Context ct;
        PatientListAdapter   m;
        String  user_add;
        Boolean res = false;

        public AddPatient(Context ctx, PatientListAdapter mp, String user) {
            ct = ctx;
            m = mp;
            user_add = user;

        }

        @Override
        protected Void doInBackground(Void... unused) {
            res = ServerAccess.addFriend(ct, user_add);
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

                if (res) {
                    m.clearlist();

                    for (Patient p : users) {

                        m.addPatient(p);
                    }
                    m.notifyDataSetChanged();
                    Toast.makeText(ct, "Added patient", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(ct, "Patient does not exist", Toast.LENGTH_LONG).show();

                }


            super.onPostExecute(unused);


        }


}
