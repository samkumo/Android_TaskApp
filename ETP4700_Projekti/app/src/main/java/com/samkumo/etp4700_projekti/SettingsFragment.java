package com.samkumo.etp4700_projekti;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import static com.samkumo.etp4700_projekti.Constants.DEFAULT_TOGGLE_STATUS;
import static com.samkumo.etp4700_projekti.Constants.DEFAULT_UPDATE_DISTANCE;
import static com.samkumo.etp4700_projekti.Constants.DEFAULT_UPDATE_INTERVAL;
import static com.samkumo.etp4700_projekti.Constants.KEY_TOGGLE_BACKGROUNDSERVICE;
import static com.samkumo.etp4700_projekti.Constants.KEY_UPDATE_DISTANCE;
import static com.samkumo.etp4700_projekti.Constants.KEY_UPDATE_INTERVAL;
import static com.samkumo.etp4700_projekti.Constants.SERVICELOG_FILENAME;

/**
 * Created by Samuli on 31.3.2017.
 */

public class SettingsFragment extends Fragment {
    private static final String TAG = "SettingsFragment";
    private static View settingsPrompt;
    private Context context;
    private boolean toggleStatus;
    private int updateInterval;
    private int updateDistance;
    private Button btn_changeSettings;
    private Switch sw_toggleservice;
    private static TextView tv_updateInterval;
    private static TextView tv_updateDistance;
    private Button btn_openServicelog;

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settings,container,false);
        loadSettings();

        //Initialize UI elements
        sw_toggleservice  = (Switch)view.findViewById(R.id.sw_toggleservice);
        tv_updateInterval = (TextView)view.findViewById(R.id.tv_updateintervalinput);
        tv_updateDistance = (TextView)view.findViewById(R.id.tv_updatedistanceinput);
        btn_openServicelog = (Button)view.findViewById(R.id.btn_openservicelog);
        sw_toggleservice.setChecked(toggleStatus);
        tv_updateInterval.setText(String.valueOf(updateInterval));
        tv_updateDistance.setText(String.valueOf(updateDistance));

        //Click listener for opening service call log
        btn_openServicelog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openServicelog(v);
            }
        });


        //Setting click listener for change settings button
        btn_changeSettings = (Button)view.findViewById(R.id.btn_changesettings);
        btn_changeSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingsDialog(v);
            }
        });

        sw_toggleservice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
              //  tv_updateInterval = (TextView)settingsPrompt.findViewById(R.id.tv_updateintervalinput);
               // tv_updateDistance = (TextView)settingsPrompt.findViewById(R.id.tv_updatedistanceinput);
                int updateInterval;
                try{
                    updateInterval  = Integer.parseInt(tv_updateInterval.getText().toString());
                }catch (Exception e){
                    Log.d(TAG, "onClick: " + e.getMessage());
                    updateInterval = DEFAULT_UPDATE_INTERVAL;
                }
                int updateDistance;
                try {
                    updateDistance = Integer.parseInt(tv_updateDistance.getText().toString());
                }catch (Exception e){
                    Log.d(TAG, "onClick: " + e.getMessage());
                    updateDistance = DEFAULT_UPDATE_DISTANCE;
                }
                boolean toggleStatus = sw_toggleservice.isChecked();

                saveSettings(toggleStatus,updateInterval, updateDistance);
            }
        });

        return view;
    }

    private void openSettingsDialog(View v){
        // Tasks.createTaskPrompt = LayoutInflater.from(context).inflate(R.layout.prompt_createtask,null);
        SettingsFragment.settingsPrompt = LayoutInflater.from(context).inflate(R.layout.prompt_changesettings,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(settingsPrompt.getContext(),android.R.style.Theme_Material_Light_Dialog));
        builder.setView(settingsPrompt);

        //Initialize UI elements

        builder
                .setCancelable(true)
                .setTitle(R.string.settings)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Get user input values and save them to preferences
                        EditText et_updateInterval = (EditText)settingsPrompt.findViewById(R.id.et_updateinterval);
                        EditText et_updateDistance = (EditText)settingsPrompt.findViewById(R.id.et_updatedistance);
                        int updateInterval;
                        try{
                            updateInterval  = Integer.parseInt(et_updateInterval.getText().toString());
                        }catch (Exception e){
                            Log.d(TAG, "onClick: " + e.getMessage());
                            updateInterval = DEFAULT_UPDATE_INTERVAL;
                        }
                        int updateDistance;
                        try {
                            updateDistance = Integer.parseInt(et_updateDistance.getText().toString());
                        }catch (Exception e){
                            Log.d(TAG, "onClick: " + e.getMessage());
                            updateDistance = DEFAULT_UPDATE_DISTANCE;
                        }

                        boolean toggleStatus = sw_toggleservice.isChecked();
                        saveSettings(toggleStatus,updateInterval,updateDistance);
                        reloadFragment();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Reloads fragment to update shown values
    private void reloadFragment() {
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    //Load setting values from local preferences
    private void loadSettings(){
        mPreferences mprefs = new mPreferences(context);

        try {
            toggleStatus = mprefs.getValueBool(KEY_TOGGLE_BACKGROUNDSERVICE);
            updateInterval = mprefs.getValueInt(KEY_UPDATE_INTERVAL);
            updateDistance = mprefs.getValueInt(KEY_UPDATE_DISTANCE);

        }catch (Exception e){
            Log.d(TAG, "loadSettings: " + e.getMessage());
            toggleStatus = DEFAULT_TOGGLE_STATUS;
            updateInterval = DEFAULT_UPDATE_INTERVAL;
            updateDistance = DEFAULT_UPDATE_DISTANCE;
        }

    }

    //Save setting values to local preferences
    private void saveSettings(boolean toggleState, int interval, int distance){
        mPreferences mprefs = new mPreferences(context);

        //Save toggle-state
        mprefs.setValueBool(KEY_TOGGLE_BACKGROUNDSERVICE,toggleState);

        //Check that setting values are positive integers
        if(interval>0){
            mprefs.setValueInt(KEY_UPDATE_INTERVAL,interval);
        }
        if(distance>0){
            mprefs.setValueInt(KEY_UPDATE_DISTANCE,distance);
        }
        if(toggleState){
            ((MainActivity)getActivity()).startPHPService();
        }else {
            ((MainActivity)getActivity()).stopPHPService();
        }

    }
    public void openServicelog(View v){
        String fileread = new mLogHandler(context).readFile(SERVICELOG_FILENAME);
        Log.d(TAG, "openServicelog: " + fileread);

        //Launch ServiceLogActivity that shows service calls in separate view
        Intent intent = new Intent(context,ServiceLogActivity.class);
        startActivity(intent);

    }
}
