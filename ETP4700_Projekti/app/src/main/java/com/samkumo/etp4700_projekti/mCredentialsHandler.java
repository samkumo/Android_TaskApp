package com.samkumo.etp4700_projekti;

import android.content.Context;

import org.json.JSONObject;

import static com.samkumo.etp4700_projekti.Constants.*;

/**
 * Created by Samuli on 28.3.2017.
 *
 * Helper class to handle saving and loading login credentials
 * TODO: Add encryption
 */

public class mCredentialsHandler {
    Context context;

    public mCredentialsHandler(Context context){
        this.context = context;
    }

    public void SaveCredentials(String username, String password){
        mPreferences mPreferences = new mPreferences(context);

        mPreferences.setValue(KEY_USER_NAME,username);
        mPreferences.setValue(KEY_USER_PWD,password);
    }
    public JSONObject LoadCredentials(){
        mPreferences mPreferences = new mPreferences(context);

        UserObject userObject = new UserObject();
        userObject.setID(mPreferences.getValue(KEY_USER_ID));
        userObject.setName(mPreferences.getValue(KEY_USER_NAME));
        userObject.setPassword(mPreferences.getValue(KEY_USER_PWD));

        JSONObject jsonObject = new mJSONParser().UserToJSON(userObject);

        return jsonObject;
    }
}
