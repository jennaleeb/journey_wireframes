package com.uoft.journey.entity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Charlie on 11/02/2016.
 */
public class Patient {

    private String muserName;
    private String mactualName;
    private String mDateAdmitted;
    private String mDateAdmittedString;
    private int mID;

    public Patient(int id, String username, String dateAdmitted, String actualname) {
        mID = id;
        muserName = username;
        mactualName = actualname;
        mDateAdmitted = dateAdmitted;
    }


    public int getID() {
        return mID;
    }

    public void setID(int ID) {
        mID = ID;
    }

    public String getuserName() {
        return muserName;
    }

    public void setuserName(String username) {
        muserName = username;
    }

    public String getactualName() {
        return mactualName;
    }

    public void setactualName(String name) {
        mactualName = name;
    }

    public String getDateAdmitted() {
        return mDateAdmitted;
    }

    public void setDateAdmitted (String dateAdmitted) {
        mDateAdmitted = dateAdmitted;
    }

    public String getDateAdmittedString() {


        DateFormat mDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String mDateAdmittedString;
        if(mDateAdmitted != null) {
            mDateAdmittedString = mDateFormat.format(mDateAdmitted);
        }
        else{
            mDateAdmittedString = "no Date Found";
        }
        return mDateAdmittedString;
    }

}
