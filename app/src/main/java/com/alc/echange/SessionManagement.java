package com.alc.echange;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SessionManagement {

    public static final String LOGIN_PHONE_NUMBER = "com.alc.echange.LOGIN_PHONE_NUMBER";
    public static final String LOGIN_PASSWORD = "com.alc.echange.LOGIN_PASSWORD";

    private final SharedPreferences prefs;

    public SessionManagement(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setLoginPhoneNo(String loginPhoneNo) {
        prefs.edit().putString(LOGIN_PHONE_NUMBER, loginPhoneNo).apply();
    }

    public String getLoginPhoneNo() {
        return prefs.getString(LOGIN_PHONE_NUMBER, "");
    }

    public void setLoginPassword(String loginPassword) {
        prefs.edit().putString(LOGIN_PASSWORD, loginPassword).apply();
    }

    public String getLoginPassword() {
        return prefs.getString(LOGIN_PASSWORD, "");
    }
}
