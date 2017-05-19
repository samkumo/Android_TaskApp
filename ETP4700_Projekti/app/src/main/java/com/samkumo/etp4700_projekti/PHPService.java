package com.samkumo.etp4700_projekti;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.samkumo.etp4700_projekti.Constants.CMD_TASK_LIST;
import static com.samkumo.etp4700_projekti.Constants.KEY_LOGIN_STATUS;
import static com.samkumo.etp4700_projekti.Constants.KEY_TOGGLE_BACKGROUNDSERVICE;
import static com.samkumo.etp4700_projekti.Constants.KEY_UPDATE_DISTANCE;
import static com.samkumo.etp4700_projekti.Constants.SERVICELOG_FILENAME;


/**
 * Created by Samuli on 30.4.2017.
 */

public class PHPService extends IntentService {
    private final String TAG = "PHPService";
    private int interval;
    private int mId = 60;

    public PHPService() {
        this(PHPService.class.getName());
    }
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public PHPService(String name){
        super(name);
        setIntentRedelivery(true); //needed for running in background
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //Recording service call to logfile
        writeToLog(getLogMessage());

        //Check if service is set to be running
        if(new mPreferences(getApplicationContext()).getValueBool(KEY_TOGGLE_BACKGROUNDSERVICE)) {
            setIntentRedelivery(true);
            //Make sure user is logged in
            if (new mPreferences(getApplicationContext()).getValueBool(KEY_LOGIN_STATUS)) {
                Log.d(TAG, "onHandleIntent: Running");
                List<TaskObject> tasks = getTasks();
                if (!tasks.isEmpty()) { //Do not iterate over empty list
                    TaskObject nearestTask = getNearestTask(tasks);
                    Log.d(TAG, "onHandleIntent: Nearest task: " + nearestTask.getId());

                    if (checkDistance(nearestTask)) {
                        Log.d(TAG, "onHandleIntent: Task is near");

                        //Show notification
                        Notification.Builder builder = new Notification.Builder(this)
                                .setSmallIcon(R.drawable.ic_near_me_white_24dp)
                                .setContentTitle(getResources().getString(R.string.tasknearby))
                                .setContentText(getResources().getString(R.string.taptoopentaskdetails))
                                .setAutoCancel(true); //Notification is cleared on click

                        //Clicking on notification opens TaskDetails activity for the nearest found task
                        Intent resultIntent = new Intent(this, TaskDetails.class);
                        resultIntent.putExtra(Intent.EXTRA_TEXT, new mJSONParser().TaskToJSON(nearestTask).toString());

                        //Need to use pendingIntent for this type of activity
                        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        builder.setContentIntent(resultPendingIntent);

                        //Build notification and launch it
                        Notification notification = builder.build();
                        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(mId, notification);
                    }
                } else {
                    Log.d(TAG, "onHandleIntent: No tasks found!");
                }
            }
        }else {
            //Service has been stopped by user
            setIntentRedelivery(false);
        }
    }

    private void writeToLog(String message) {
        new mLogHandler(getApplicationContext()).writeFile(SERVICELOG_FILENAME,message,true);
    }
    private String getLogMessage(){
        String message = "";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        StringBuilder builder = new StringBuilder();
        builder.append(timestamp.toString());
        builder.append(" ");
        builder.append(getApplicationInfo().packageName);
        builder.append(" ");
        builder.append(PHPService.class.getSimpleName());
        builder.append(": ");
        builder.append("Service request called");
        message = builder.toString();

        return message;
    }

    protected boolean checkDistance(TaskObject task){
        mGPSHandler gpsHandler = new mGPSHandler(getApplicationContext());
        try {
            LatLng currentLoc = new LatLng(gpsHandler.getLat(), gpsHandler.getLon());
            LatLng taskLoc = new LatLng(Double.parseDouble(task.getLat()), Double.parseDouble(task.getLon()));
            int thresholdDistance = Integer.MAX_VALUE;

            try{
                mPreferences prefs = new mPreferences(getApplicationContext());
                thresholdDistance = prefs.getValueInt(KEY_UPDATE_DISTANCE);
            }catch (Exception e){
                Log.d(TAG, "checkDistance: " + e.getMessage());
            }
            float[] distance = new float[1];
            Location.distanceBetween(currentLoc.latitude,currentLoc.longitude,taskLoc.latitude,taskLoc.longitude,distance);
            if(thresholdDistance>distance[0]){
                return true;
            }
        }catch (Exception e){
            Log.d(TAG, "checkDistance: " + e.getMessage());
            return false;
        }

        return false;
    }

    protected List<TaskObject> getTasks(){
        mPreferences prefs = new mPreferences(getApplicationContext());
        UserObject currentUserObject = prefs.getActiveUser();

            //Fetching latest tasklist from server
            List<TaskObject> taskObjects = new ArrayList<TaskObject>();
            try {
                String json = new mAsyncTask(getApplicationContext().getApplicationContext()).execute(CMD_TASK_LIST, currentUserObject.getID()).get();
                List<TaskObject> importedTaskObjects = new mJSONParser().ParseJSONTasklist(json);
                if(importedTaskObjects != null){
                    taskObjects = importedTaskObjects;
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return  taskObjects;
    }
    protected TaskObject getNearestTask(List<TaskObject> tasklist){
        TaskObject nearestTask = new TaskObject();
        float previousDistance = Float.MAX_VALUE;

        try{
            //Get current location to use as reference point
            mGPSHandler gpsHandler = new mGPSHandler(getApplicationContext());
            LatLng currentLoc = new LatLng(gpsHandler.getLat(),gpsHandler.getLon());

            //Iterate through tasks to find nearest
            for (int i = 0; i < tasklist.size(); i++) {
                TaskObject compareTask = tasklist.get(i);
                try{
                    float[] distance = new float[1];
                    LatLng taskLatLng = new LatLng(Double.parseDouble(compareTask.getLat()),Double.parseDouble(compareTask.getLon()));
                    Location.distanceBetween(currentLoc.latitude,currentLoc.longitude,taskLatLng.latitude,taskLatLng.longitude,distance);
                    if(previousDistance>distance[0]){
                        nearestTask = compareTask;
                        previousDistance = distance[0];
                    }
                }catch (Exception e){
                    Log.d(TAG, "getNearestTask: " + e.getMessage());
                }
            }
        }catch (Exception e){
            Log.d(TAG, "loadSettings: " + e.getMessage());
        }
        Log.d(TAG, "getNearestTask: Distance to task:" + String.valueOf(previousDistance));
        return nearestTask;
    }


}
