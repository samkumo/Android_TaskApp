package com.samkumo.etp4700_projekti;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.samkumo.etp4700_projekti.Constants.CMD_TASK_LIST;
import static com.samkumo.etp4700_projekti.Constants.CMD_TASK_RESERVE;
import static com.samkumo.etp4700_projekti.Constants.CMD_TASK_START;
import static com.samkumo.etp4700_projekti.Constants.CMD_TASK_STOP;
import static com.samkumo.etp4700_projekti.Constants.DEFAULT_EXPLANTION;

/**
 * Created by Samuli on 3.5.2017.
 */

public class TaskDetails extends AppCompatActivity {
    private static final int TASK_AVAILABLE = 300;
    private static final int TASK_RESERVED = 301;
    private static final int TASK_UNAVAILABLE = 302;
    private TaskObject task;
    private int taskStatus;
    private UserObject currentUser;
    private TextView tv_taskowner;
    private TextView tv_desc;
    private TextView tv_taskstarted;
    private TextView tv_taskstopped;
    private ToggleButton btn_reserve;
    private ToggleButton btn_start;
    private View colorBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskdetails);
        String taskString = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        task = new mJSONParser().JSONToTask(taskString);

        //Configure actionbar
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(false);
        bar.setTitle(getResources().getString(R.string.nearbytask));

        //Initialize UI elements
        tv_taskowner = (TextView)findViewById(R.id.tv_taskdetails_taskowner);
        tv_desc = (TextView)findViewById(R.id.tv_taskdetails_desc2);
        tv_taskstarted = (TextView)findViewById(R.id.tv_taskdetails_tasksettingsstart);
        tv_taskstopped = (TextView)findViewById(R.id.tv_taskdetails_tasksettingsstop);
        btn_reserve = (ToggleButton)findViewById(R.id.btn_taskdetails_reserve);
        btn_start = (ToggleButton)findViewById(R.id.btn_taskdetails_start);
        colorBar = (View)findViewById(R.id.colorbar);

        //Get instance of current user on this app
        currentUser = new mPreferences(this).getActiveUser();

        //Set task status
        if(task.getUserId().equals(currentUser.getID())){
            taskStatus = TASK_RESERVED;
            String ownerString = "Availability: " + getResources().getString(R.string.assignedtoyou);
            tv_taskowner.setText(ownerString);
            colorBar.setBackgroundColor(Color.CYAN);
        }else if(task.getUserId().equals("null")){
            taskStatus = TASK_AVAILABLE;
            String ownerString = "Availability: " + getResources().getString(R.string.taskisavailable);
            tv_taskowner.setText(ownerString);
            colorBar.setBackgroundColor(Color.GREEN);
        }else{
            taskStatus = TASK_UNAVAILABLE;
            String ownerString = "Availability: " + getResources().getString(R.string.assignedtootheruser);
            tv_taskowner.setText(ownerString);
            colorBar.setBackgroundColor(Color.GRAY);
        }

        updateTaskDetails(task);
    }

    private void updateTaskDetails(TaskObject task)
    {
        //TODO: Display current task information to user
        tv_desc.setText(task.getDescription());

        switch (taskStatus){
            case TASK_AVAILABLE: //Task is available and not started or completed
                btn_start.setEnabled(true);
                btn_reserve.setEnabled(true);
                btn_start.setChecked(false);
                btn_reserve.setChecked(false);
                break;
            case TASK_RESERVED: //Task is reserved to user
                btn_reserve.setEnabled(false);
                btn_reserve.setChecked(true);
                if(!task.getStop().equals("null") || task.getStart().equals("null")){ //task has been stopped or not started
                    btn_start.setChecked(false);
                    if(!task.getStop().equals("null")) { //Task has been stopped, disable control
                        btn_start.setEnabled(false);
                    }else{
                        btn_start.setEnabled(true);
                    }
                }else{ //Task has been started and is current active
                    btn_start.setChecked(true);
                    btn_start.setEnabled(true);
                }
                break;
            case TASK_UNAVAILABLE: //Task is unavailable, reserved for other user
                btn_start.setEnabled(false);
                btn_reserve.setEnabled(false);
                btn_reserve.setChecked(true);
                if(!task.getStop().equals("null") || task.getStart().equals("null")){ //task has been stopped or not started
                    btn_start.setChecked(false);
                }else{ //task has not been started
                    btn_start.setChecked(true);
                }
                break;
            default:
                break;
        }

        //Set start and stop times
        if(!task.getStart().equals("null")){
            tv_taskstarted.setText(task.getStart());
        }else{
            tv_taskstarted.setText(getResources().getString(R.string.notstartedyet));
        }
        if(!task.getStop().equals("null")){
            tv_taskstopped.setText(task.getStop());
        }else{
            tv_taskstopped.setText(getResources().getString(R.string.notstoppedyet));
        }
    }

    public void saveTaskDetails(View view)
    {
        //TODO: Save any changes user made to the task and update it on server as well
        TaskObject updatedTask = task;

    }

    @Override
    public void onBackPressed() {
        finish();
        //super.onBackPressed();
    }

    public void closeActivity()
    {
        finish();
    }

    public void reserveTask(View view) {
        //Reserve task to user
        if(taskStatus == TASK_AVAILABLE){
            try {
                new mAsyncTask(getApplicationContext()).execute(CMD_TASK_RESERVE,task.getId(),currentUser.getID()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            closeActivity();

        }
    }

    public void startTask(View view) {
        //Start task
        if(taskStatus == TASK_RESERVED){
            if(btn_start.isChecked()){
                try {
                    new mAsyncTask(getApplicationContext()).execute(CMD_TASK_START,task.getId()).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    new mAsyncTask(getApplicationContext()).execute(CMD_TASK_STOP,task.getId(),DEFAULT_EXPLANTION).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            closeActivity();
        }
    }
    private void reloadTask(){
        try {
            String jsonstring = new mAsyncTask(getApplicationContext()).execute(CMD_TASK_LIST,task.getId()).get();
            List<TaskObject> list = new mJSONParser().ParseJSONTasklist(jsonstring);
            for (int i = 0; i < list.size(); i++) {
                if(list.get(i).getId().equals(task.getId())){
                    task = list.get(i);
                    return;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return;
        }
        return;

    }
    private void reloadActivity(){
        finish();
        startActivity(getIntent());
    }
}
