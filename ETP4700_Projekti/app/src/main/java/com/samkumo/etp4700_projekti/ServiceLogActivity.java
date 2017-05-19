package com.samkumo.etp4700_projekti;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import static com.samkumo.etp4700_projekti.Constants.SERVICELOG_FILENAME;

/**
 * Created by Samuli on 9.5.2017.
 */

public class ServiceLogActivity extends AppCompatActivity {
    final Handler handler = new Handler();
    private TextView tv_servicelog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicelog);


        //Configure actionbar
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle(getResources().getString(R.string.servicelog));
        }

        //Initialize service log view
        tv_servicelog = (TextView)findViewById(R.id.tv_servicelog);
        tv_servicelog.setMaxLines(Integer.MAX_VALUE);
        tv_servicelog.setMovementMethod(new ScrollingMovementMethod());

        updateLogView();

    }

    private void updateLogView() {
        String logContent = new mLogHandler(getApplicationContext()).readFile(SERVICELOG_FILENAME);
        tv_servicelog.setText(logContent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
