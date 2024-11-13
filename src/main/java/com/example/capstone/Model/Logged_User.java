package com.example.capstone.Model;

import java.sql.Timestamp;

/**
 * Logged_User model variables and the setters and getters for them, with a default constructor for Logged_User
 */
public class Logged_User {
    private static int logged_User_ID;
    private static String logged_User_Name;
    private static Timestamp logged_TimeStamp;

    public Logged_User(Integer ID, String User, Timestamp logTime) {
        logged_User_ID = ID;
        logged_User_Name = User;
        logged_TimeStamp = logTime;
    }

    public static String getLogged_User_Name() {
        return logged_User_Name;
    }

    public static Timestamp getLogged_TimeStamp() {
        return logged_TimeStamp;
    }

    public static int getLogged_User_ID() {
        return logged_User_ID;
    }


}
