package com.samkumo.etp4700_projekti;

import android.content.Context;
import android.content.SharedPreferences;

import static com.samkumo.etp4700_projekti.Constants.KEY_USER_DESCRIPTION;
import static com.samkumo.etp4700_projekti.Constants.KEY_USER_ID;
import static com.samkumo.etp4700_projekti.Constants.KEY_USER_NAME;
import static com.samkumo.etp4700_projekti.Constants.KEY_USER_PWD;
import static com.samkumo.etp4700_projekti.Constants.MY_SHARED_PREFERENCES;

/**
 * Created by Samuli on 28.3.2017.
 */

public class mPreferences {
    Context context;
    SharedPreferences sharedPreferences;

    public mPreferences(Context context){
        this.context = context;
        loadPrefs();

    }
    public void loadPrefs(){
        sharedPreferences = context.getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }
    public boolean getLogin(){
        return sharedPreferences.getBoolean(context.getString(R.string.loginstatus),false);
    }
    public void setLogin(boolean value){
        sharedPreferences.edit().putBoolean(context.getString(R.string.loginstatus),value).apply();
    }
    public void setValue(String key, String value){
        sharedPreferences.edit().putString(key,value).apply();
    }
    public void setValueBool(String key, boolean value){
        sharedPreferences.edit().putBoolean(key,value).apply();
    }
    public void clearValue(String key){
        sharedPreferences.edit().remove(key).apply();
    }
    public String getValue(String key){
        return sharedPreferences.getString(key,"null");
    }
    public boolean getValueBool(String key){
        return sharedPreferences.getBoolean(key,false);
    }
    public int getValueInt(String key){
        return sharedPreferences.getInt(key,0);
    }
    public void setValueInt(String key, int value){
        sharedPreferences.edit().putInt(key, value).apply();
    }
    public void setActiveUser(UserObject userObject){
        setValue(KEY_USER_ID, userObject.getID());
        setValue(KEY_USER_NAME, userObject.getName());
        setValue(KEY_USER_PWD, userObject.getPassword());
        setValue(KEY_USER_DESCRIPTION, userObject.getDescription());
    }
    public UserObject getActiveUser(){
        UserObject userObject = new UserObject(getValue(KEY_USER_ID),getValue(KEY_USER_NAME),getValue(KEY_USER_PWD),getValue(KEY_USER_DESCRIPTION));
        return userObject;
    }
    public void clearActiveUser(){
        clearValue(KEY_USER_ID);
        clearValue(KEY_USER_NAME);
        clearValue(KEY_USER_PWD);
        clearValue(KEY_USER_DESCRIPTION);
    }

}

