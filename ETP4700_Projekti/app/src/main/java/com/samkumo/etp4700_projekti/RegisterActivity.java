package com.samkumo.etp4700_projekti;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import static com.samkumo.etp4700_projekti.Constants.CMD_USER_DETAILS;
import static com.samkumo.etp4700_projekti.Constants.CMD_USER_INSERT;
import static com.samkumo.etp4700_projekti.Constants.EVENT_INSERT_USER_SUCCESS;
import static com.samkumo.etp4700_projekti.Constants.EXTRA_USER_DETAILS;

/**
 * Created by Samuli on 28.3.2017.
 */

public class RegisterActivity extends AppCompatActivity {
    Intent previousIntent;
    EditText etRegistername;
    EditText etRegisterpassword;
    EditText etRegisterdescription;
    TextView tvReturntoprevious;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        previousIntent = getIntent();
        setContentView(R.layout.activity_register);

        etRegistername = (EditText)findViewById(R.id.et_registername);
        etRegisterpassword = (EditText)findViewById(R.id.et_registerpassword);
        etRegisterdescription = (EditText)findViewById(R.id.et_registerdescription);
        tvReturntoprevious = (TextView)findViewById(R.id.tv_returntoprevious);

        tvReturntoprevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        InitActionBar();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    private void InitActionBar(){
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setTitle("AAAA");
    }

    public void registerAccount(View view) {
        String username = etRegistername.getText().toString();
        String password = etRegisterpassword.getText().toString();
        String description = etRegisterdescription.getText().toString();
        String result = "";

        try {
            result = new mAsyncTask(getApplicationContext()).execute(CMD_USER_INSERT,username,password,description).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(result.equals(EVENT_INSERT_USER_SUCCESS)){
            try {
                String userdetails = new mAsyncTask(getApplicationContext()).execute(CMD_USER_DETAILS,username,password).get();
                gotoMainActivity(userdetails);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }else{
            Toast.makeText(this,"Account could not be registered!",Toast.LENGTH_SHORT);
        }
    }
    private void gotoMainActivity(String userdetails)
    {
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra(EXTRA_USER_DETAILS,userdetails);
        startActivity(intent);
    }
}
