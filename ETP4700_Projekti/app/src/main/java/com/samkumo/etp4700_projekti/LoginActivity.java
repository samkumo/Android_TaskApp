package com.samkumo.etp4700_projekti;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import static com.samkumo.etp4700_projekti.Constants.CMD_USER_DETAILS;
import static com.samkumo.etp4700_projekti.Constants.EVENT_LOGIN_FAILED;
import static com.samkumo.etp4700_projekti.Constants.EXTRA_USER_DETAILS;
import static com.samkumo.etp4700_projekti.Constants.KEY_LOGIN_STATUS;

/**
 * Created by Samuli on 24.3.2017.
 */

public class LoginActivity extends AppCompatActivity{
    private mPreferences mPreferences;
    private EditText etName;
    private EditText etPassword;
    private TextView etRegister;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mPreferences = new mPreferences(this);

        etName = (EditText)findViewById(R.id.et_name);
        etPassword = (EditText)findViewById(R.id.et_password);
        etRegister = (TextView)findViewById(R.id.et_register);

        InitListners();

        if(mPreferences.getValueBool(KEY_LOGIN_STATUS)){
            UserObject userObject = mPreferences.getActiveUser();
            gotoMainActivity(new mJSONParser().UserToJSON(userObject).toString());
        }
    }

    private void InitListners() {
        etRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoRegisterAction();
            }
        });
    }


    public void loginAction(View view) {
        String result = "";
        String name  = etName.getText().toString();
        String pwd = etPassword.getText().toString();
        try {
            result = new mAsyncTask(this.getApplicationContext()).execute(CMD_USER_DETAILS,name,pwd).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(result.equals(String.valueOf(EVENT_LOGIN_FAILED))){
            mPreferences.setValueBool(KEY_LOGIN_STATUS,false);
            Toast.makeText(this,"Login failed!",Toast.LENGTH_SHORT).show();
            etName.setText("");
            etPassword.setText("");

        }else{
            mPreferences.setValueBool(KEY_LOGIN_STATUS,true);
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result);
                UserObject userObject = new mJSONParser().JSONtoUser(jsonObject);
                mPreferences.setActiveUser(userObject);
                Toast.makeText(this,"Login successful!",Toast.LENGTH_SHORT).show();
                gotoMainActivity(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    private void gotoRegisterAction() {
        Intent intent = new Intent(this,RegisterActivity.class);
        finishAffinity();
        startActivity(intent);
    }

    private void gotoMainActivity(String result)
    {
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra(EXTRA_USER_DETAILS,result);
        startActivity(intent);
        finishAffinity();

    }
}
