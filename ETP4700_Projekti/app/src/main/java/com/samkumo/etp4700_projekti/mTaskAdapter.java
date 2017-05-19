package com.samkumo.etp4700_projekti;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.samkumo.etp4700_projekti.Constants.CMD_TASK_DELETE;
import static com.samkumo.etp4700_projekti.Constants.CMD_TASK_RESERVE;
import static com.samkumo.etp4700_projekti.Constants.CMD_TASK_START;
import static com.samkumo.etp4700_projekti.Constants.CMD_TASK_STOP;

/**
 * Created by Samuli on 18.4.2017.
 *
 * Custom adapter for TaskObjects that will be shown as list items
 */

public class mTaskAdapter extends ArrayAdapter<TaskObject>{
    private static final int CURRENTUSER = 1;
    private static final int OTHERUSER = 2;
    private static final int NONE = 3;
    private static final String STOPEXPLANATION = "STOPPED";
    private static final int STARTED = 10;
    private static final int FINISHED = 11;
    private static final int NOTSTARTED = 12;
    private static SimpleDateFormat dateformat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
    private static UserObject currentUser;
    private Context context;
    private View taskSettingsPrompt;
    private int taskOwner;



    public mTaskAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<TaskObject> objects) {
        super(context, resource, objects);
        this.context = context;
        currentUser = new mPreferences(context).getActiveUser();
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull final ViewGroup parent) {
        View itemview = super.getView(position,convertView,parent);

        //Initialize the background color to transparent
        itemview.setBackgroundColor(Color.TRANSPARENT);

        //Create customized view
        View adjustedItem = SetItemColor(itemview,position);
        adjustedItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TaskObject task  = getItem(position);
                //OnClick handler for each task item
                taskSettingsPrompt = LayoutInflater.from(v.getContext()).inflate(R.layout.prompt_taskettings,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(taskSettingsPrompt.getContext(),android.R.style.Theme_Material_Light_Dialog));
                builder.setView(taskSettingsPrompt);

                //Find view elements
                final TextView tv_desc = (TextView)taskSettingsPrompt.findViewById(R.id.tv_tasksettingsdesc);
                final TextView tv_laststart = (TextView)taskSettingsPrompt.findViewById(R.id.tv_tasksettingsstart);
                final TextView tv_laststop = (TextView)taskSettingsPrompt.findViewById(R.id.tv_tasksettingsstop);
                final Switch sw_reservetask = (Switch)taskSettingsPrompt.findViewById(R.id.sw_reservetask);
                final Switch sw_startask = (Switch)taskSettingsPrompt.findViewById(R.id.sw_taskstart);

                //Check the assigned user for this task
                taskOwner = getTaskOwner(task);

                //Set the description
                tv_desc.setText(task.getDescription());

                //Set correct start/stop times
                if(!task.getStart().equals("null")){
                    tv_laststart.setText(task.getStart());
                }else {
                    tv_laststart.setText("");
                }
                if(!task.getStop().equals("null")){
                    tv_laststop.setText(task.getStop());
                }else{
                    tv_laststop.setText("");
                }

                //TODO: Initialize toggle switches in correct state
                //Enable/disable switches based on assigned taskOwner
                switch (taskOwner){
                    case CURRENTUSER:
                        sw_reservetask.setEnabled(false);
                        sw_reservetask.setChecked(true);
                        if(getTaskState(task) == FINISHED){
                            sw_startask.setEnabled(false);
                        }else{
                            sw_startask.setEnabled(true);
                        }
                        break;
                    case OTHERUSER:
                        sw_reservetask.setEnabled(false);
                        sw_reservetask.setChecked(true);
                        sw_startask.setEnabled(false);
                        break;
                    case NONE:
                        sw_reservetask.setEnabled(true);
                        sw_reservetask.setChecked(false);
                        if(getTaskState(task) == FINISHED){
                            sw_startask.setEnabled(false);
                        }else{
                            sw_startask.setEnabled(true);
                        }
                        break;
                }
                //Initialize taskStart switch to correct position
                if(getTaskState(task) == STARTED){
                    sw_startask.setChecked(true);
                }else{
                    sw_startask.setChecked(false);
                }
                builder
                        .setCancelable(true);
                //Dialog buttons depend on assigned user
                switch (taskOwner){
                    case CURRENTUSER:
                        int taskstate = getTaskState(task);
                        String title = "";
                        if(taskstate == FINISHED){
                            title = "Task: Finished";
                        }else if(taskstate == STARTED){
                            title = "Task: Started";
                        }else{
                            title = "Task: Not started yet";
                        }
                        builder
                                .setTitle(title)
                                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Update reserved state
                                    setTaskOwner(task,currentUser,sw_reservetask.isChecked());
                                    //Set task active/inactive
                                    setTaskState(task,sw_startask.isChecked());
                                    notifyDataSetChanged();
                                }
                                })
                                .setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Open dialog to confirm delete operation
                                        final View deletePrompt = LayoutInflater.from(taskSettingsPrompt.getContext()).inflate(R.layout.prompt_deleteprompt,null);
                                        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(deletePrompt.getContext(),android.R.style.Theme_Material_Light_Dialog));
                                        builder.setView(deletePrompt);
                                        builder
                                                .setCancelable(true)
                                                .setTitle(R.string.askconfirmdelete)
                                                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //Delete task from remote database
                                                        try {
                                                            String result = new mAsyncTask(deletePrompt.getContext()).execute(CMD_TASK_DELETE,task.getId()).get();
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        } catch (ExecutionException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                })
                                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }

                                        });
                                        AlertDialog deletedialog = builder.create();
                                        deletedialog.show();

                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                                });
                        break;
                    case OTHERUSER:
                        builder
                                .setTitle(R.string.taskunavailable)
                                .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        break;
                    case NONE:
                        builder
                                .setTitle(R.string.taskavailable)
                                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Update reserved state
                                        setTaskOwner(task,currentUser,sw_reservetask.isChecked());
                                        //Set task active/inactive
                                        setTaskState(task,sw_startask.isChecked());
                                        notifyDataSetChanged();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        break;
                    default:
                        break;
                }
                Log.d("TASKOWNER::",String.valueOf(taskOwner));
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        });
        return adjustedItem;
    }

    private void setTaskOwner(TaskObject task, UserObject user, boolean switchstate){
        if(switchstate){
            try {
                String result = new mAsyncTask(taskSettingsPrompt.getContext()).execute(CMD_TASK_RESERVE,task.getId(),user.getID()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }else {
            try {
                String result = new mAsyncTask(taskSettingsPrompt.getContext()).execute(CMD_TASK_RESERVE,task.getId(),"0").get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
    private void setTaskState(TaskObject task, boolean switchstate){
        int taskState = getTaskState(task);
        if(switchstate && taskState == NOTSTARTED){
            try {
                String result = new mAsyncTask(taskSettingsPrompt.getContext()).execute(CMD_TASK_START,task.getId()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }else if(!switchstate && taskState == STARTED){
            try {
                String result = new mAsyncTask(taskSettingsPrompt.getContext()).execute(CMD_TASK_STOP,task.getId(),STOPEXPLANATION).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private int getTaskOwner(TaskObject task){
        String ownerID = task.getUserId();

        if(ownerID.equals(currentUser.getID())){
            return CURRENTUSER;
        }else if(isInteger(ownerID)){
            return OTHERUSER;
        }else{
            return NONE;
        }
    }

    private View SetItemColor(View v, int position){
        View view = v;
        TaskObject task = getItem(position);
        int taskstate = getTaskState(task);
        int taskowner = getTaskOwner(task);

        //Is task is assigned to current user?
        if(taskowner == CURRENTUSER){

            if(taskstate == FINISHED){
                view.setBackgroundColor(Color.BLUE);
            }else if(taskstate == STARTED){
                view.setBackgroundColor(Color.CYAN);
            }else{
                view.setBackgroundColor(Color.YELLOW);
            }
            return view;
        }else if(taskowner == OTHERUSER){
            //Task is not assigned to any (valid) ID, it's free
            view.setBackgroundColor(Color.TRANSPARENT);
            return view;
        }else{
            //Task is not free and unavailable, leave color at default
            view.setBackgroundColor(Color.GREEN);
            return view;
        }
    }

    // Helper function used to check for valid ID-strings
    private boolean isInteger(String string){
        try{
            int number = Integer.parseInt(string);
            if(number <= 0){
                return false;
            }
        }catch (Exception e){
            return false;
        }
        return true;
    }

    private int getTaskState(TaskObject task){
        String startTime = task.getStart();
        String stopTime = task.getStop();

        /*
        //Set task status NOTSTARTED, STARTED or FINISHED based on timestamps
        if(startTime.equals("null")){
            return NOTSTARTED;
        }else if(!startTime.equals("null") && stopTime.equals("null")){
            return STARTED;
        }else{
            return FINISHED;
        }
    */
        if(!task.getStop().equals("null") || task.getStart().equals("null")){ //task has been stopped or not started
            if(!task.getStop().equals("null")) { //Task has been stopped, disable control
                return FINISHED;
            }else{
                return NOTSTARTED;
            }
        }else{ //Task has been started and is current active
            return STARTED;
        }
    }
}
