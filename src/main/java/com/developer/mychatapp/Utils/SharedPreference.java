package com.developer.mychatapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreference {
    private static volatile SharedPreference newInstance = new SharedPreference();

    private final String PREFERENCE_FILE_KEY = "uk_chat";

            //private constructor.
    private SharedPreference(){}

    public static SharedPreference getInstance() {
        if (newInstance == null){ //if there is no instance available... create new one
            newInstance = new SharedPreference();
        }

        return newInstance;
    }

    public void saveString(Context context,String key,String value){
        SharedPreferences sharedPref = context.getSharedPreferences(
                PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(Context context,String key){
        SharedPreferences sharedPref = context.getSharedPreferences(
                PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        String res = sharedPref.getString(key, "");
        return res;
    }

    public void clearData(Context context){
        SharedPreferences preferences =context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();

    }


}
