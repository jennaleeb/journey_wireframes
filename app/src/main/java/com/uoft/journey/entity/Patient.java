package com.uoft.journey.entity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Charlie on 11/02/2016.
 */
public class Patient {

    private String mName;
    private Date mDateAdmitted;
    private String mDateAdmittedString;
    private int mID;

    public Patient(int id, String name, Date dateAdmitted) {
        mID = id;
        mName = name;
        mDateAdmitted = dateAdmitted;
    }


    public int getID() {
        return mID;
    }

    public void setID(int ID) {
        mID = ID;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Date getDateAdmitted() {
        return mDateAdmitted;
    }

    public void setDateAdmitted (Date dateAdmitted) {
        mDateAdmitted = dateAdmitted;
    }

    public String getDateAdmittedString() {
        DateFormat mDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String mDateAdmittedString = mDateFormat.format(mDateAdmitted);
        return mDateAdmittedString;
    }

}
