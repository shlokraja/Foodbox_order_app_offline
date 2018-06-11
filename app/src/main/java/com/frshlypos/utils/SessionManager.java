package com.frshlypos.utils;

import android.content.Context;

public class SessionManager {
    private SecurePreferences pref;

    public SessionManager(Context context) {
        pref = SecurePreferences.getInstance(context, "FrshlyPref");

    }

    /**
     * Getting value for key from shared Preferences
     *
     * @param key          key for which we need to get Value
     * @param defaultValue default value to be returned if key is not exits
     * @return It will return value of key if exist and defaultValue otherwise
     */
    public String getValueFromKey(String key, String defaultValue) {
        if (pref.containsKey(key)) {
            return pref.getString(key, defaultValue);
        } else {
            return defaultValue;
        }
    }

    public String getDataByKey(String Key) {
        return getDataByKey(Key, "");
    }

    public Boolean getDataByKey(String Key, boolean DefaultValue) {
        if (pref.containsKey(Key)) {
            return pref.getBoolean(Key, DefaultValue);
        } else {
            return DefaultValue;
        }
    }

    public String getDataByKey(String Key, String DefaultValue) {
        String returnValue;
        if (pref.containsKey(Key)) {
            returnValue = pref.getString(Key, DefaultValue);
        } else {
            returnValue = DefaultValue;
        }
        return returnValue;
    }

    public int getDataByKey(String Key, int DefaultValue) {
        if (pref.containsKey(Key)) {
            return pref.getInt(Key, DefaultValue);
        } else {
            return DefaultValue;
        }
    }

    public void storeDataByKey(String key, int Value) {
        pref.putInt(key, Value);
        pref.commit();
    }

    public void storeDataByKey(String key, String Value) {
        pref.putString(key, Value);
        pref.commit();
    }

    public void storeDataByKey(String key, boolean Value) {
        pref.putBoolean(key, Value);
        pref.commit();
    }

    public void clearDataByKey(String key) {
        pref.removeValue(key);
    }

    public void clearSession() {
        pref.clear();
        pref.commit();
    }
}
