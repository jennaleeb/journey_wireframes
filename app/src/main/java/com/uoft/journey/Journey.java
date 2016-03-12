package com.uoft.journey;

import android.app.Application;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import com.baasbox.android.*;



public class Journey extends MultiDexApplication {

        private BaasBox box;
        private int UserId;
        private String Username;


    @Override
        public void onCreate() {
            super.onCreate();
           /* BaasBox.Config config = new BaasBox.Config();
            config.API_DOMAIN = "159.203.32.221"; // the host address
            config.APP_CODE = "1234567890"; // your appcode
            config.HTTP_PORT = 9000; // your port

            box = BaasBox.initDefault(this, config);*/

            BaasBox.Builder b =
                    new BaasBox.Builder(this);
            box = b.setAuthentication(BaasBox.Config.AuthType.SESSION_TOKEN)
                    .setApiDomain("159.203.32.221")
                    .setAppCode("1234567890")
                    .setPort(9000)
                    .init();
        }

    public void setUserID(int userID) {
        this.UserId = userID;
    }

    public int getUserID() {
        return UserId;
    }

    public void setUsername(String username) {
        this.Username = username;
    }

    public String getUsername() {
        return Username;
    }
}

