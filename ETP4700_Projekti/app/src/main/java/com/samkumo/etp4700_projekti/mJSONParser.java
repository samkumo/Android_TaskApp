package com.samkumo.etp4700_projekti;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.samkumo.etp4700_projekti.Constants.TAG_TASK_DESCRIPTION;
import static com.samkumo.etp4700_projekti.Constants.TAG_TASK_EXPLANATION;
import static com.samkumo.etp4700_projekti.Constants.TAG_TASK_ID;
import static com.samkumo.etp4700_projekti.Constants.TAG_TASK_LAT;
import static com.samkumo.etp4700_projekti.Constants.TAG_TASK_LON;
import static com.samkumo.etp4700_projekti.Constants.TAG_TASK_PLACE;
import static com.samkumo.etp4700_projekti.Constants.TAG_TASK_START;
import static com.samkumo.etp4700_projekti.Constants.TAG_TASK_STOP;
import static com.samkumo.etp4700_projekti.Constants.TAG_TASK_USERID;
import static com.samkumo.etp4700_projekti.Constants.TAG_USER_DESCRIPTION;
import static com.samkumo.etp4700_projekti.Constants.TAG_USER_ID;
import static com.samkumo.etp4700_projekti.Constants.TAG_USER_NAME;
import static com.samkumo.etp4700_projekti.Constants.TAG_USER_PASSWORD;

/**
 * Created by Samuli on 24.3.2017.
 */

public class mJSONParser {


    private static final String TAG = "mJSONParser";

    public ArrayList<UserObject> ParseJSONUserlist(String json) {
        ArrayList<UserObject> userlist = new <UserObject> ArrayList();
        if (json != null) {
            try {

                //JSONObject jsonObj = new JSONObject(json);
                Log.d("JSON to list: ", "> " + json);

                // Getting JSON Array node
                JSONArray users = new JSONArray(json);

                // looping through All Users
                for (int i = 0; i < users.length(); i++) {
                    JSONObject c = users.getJSONObject(i);

                    String id = c.getString(TAG_USER_ID);
                    String name = c.getString(TAG_USER_NAME);
                    String password = c.getString(TAG_USER_PASSWORD);
                    String description = c.getString(TAG_USER_DESCRIPTION);

                    UserObject u = new UserObject();

                    u.setID(id);
                    u.setName(name);
                    u.setPassword(password);
                    u.setDescription(description);

                    // adding user to users list
                    userlist.add(u);
                }
                return userlist;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "No data received from HTTP Request");
            return null;
        }
    }

    public List<TaskObject> ParseJSONTasklist(String json) {
        List<TaskObject> tasklist = new ArrayList<TaskObject>();
        if (json != null) {
            try {

                //JSONObject jsonObj = new JSONObject(json);
                Log.d("JSON to list: ", "> " + json);

                // Getting JSON Array node
                JSONArray tasks = new JSONArray(json);

                // looping through all tasks
                for (int i = 0; i < tasks.length(); i++) {
                    JSONObject c = tasks.getJSONObject(i);

                    String id = c.getString(TAG_TASK_ID);
                    String userid = c.getString(TAG_TASK_USERID);
                    String start = c.getString(TAG_TASK_START);
                    String stop = c.getString(TAG_TASK_STOP);
                    String explanation = c.getString(TAG_TASK_EXPLANATION);
                    String description = c.getString(TAG_TASK_DESCRIPTION);
                    String lon = c.getString(TAG_TASK_LON);
                    String lat = c.getString(TAG_TASK_LAT);
                    String place = c.getString(TAG_TASK_PLACE);

                    TaskObject t = new TaskObject();

                    t.setId(id);
                    t.setUserId(userid);
                    t.setStart(start);
                    t.setStop(stop);
                    t.setExplanation(explanation);
                    t.setDescription(description);
                    t.setLon(lon);
                    t.setLat(lat);
                    t.setPlace(place);

                    // adding tasks to tasklist
                    tasklist.add(t);
                }
                return tasklist;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "No data received from HTTP Request");
            return null;
        }
    }
    public JSONObject TaskToJSON(TaskObject taskObject){
        JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put(TAG_TASK_ID,taskObject.getId());
                jsonObject.put(TAG_TASK_USERID,taskObject.getUserId());
                jsonObject.put(TAG_TASK_START,taskObject.getStart());
                jsonObject.put(TAG_TASK_STOP,taskObject.getStop());
                jsonObject.put(TAG_TASK_EXPLANATION,taskObject.getExplanation());
                jsonObject.put(TAG_TASK_DESCRIPTION,taskObject.getDescription());
                jsonObject.put(TAG_TASK_LON,taskObject.getLon());
                jsonObject.put(TAG_TASK_LAT,taskObject.getLat());
                jsonObject.put(TAG_TASK_PLACE,taskObject.getPlace());
            }catch(Exception e){
                Log.d(TAG, "instance initializer: " + e.getMessage());
                return null;
            }
        return jsonObject;
    }
    public TaskObject JSONToTask(String jsonString){
        TaskObject taskObject = new TaskObject();
        try{
            taskObject = JSONToTask(new JSONObject(jsonString));
        }catch (Exception e){
            return null;
        }
        return taskObject;
    }
    public TaskObject JSONToTask(JSONObject jsonString){
        TaskObject taskObject = new TaskObject();
        try{
            taskObject.setId(jsonString.getString(TAG_TASK_ID));
            taskObject.setUserId(jsonString.getString(TAG_TASK_USERID));
            taskObject.setStart(jsonString.getString(TAG_TASK_START));
            taskObject.setStop(jsonString.getString(TAG_TASK_STOP));
            taskObject.setExplanation(jsonString.getString(TAG_TASK_EXPLANATION));
            taskObject.setDescription(jsonString.getString(TAG_TASK_DESCRIPTION));
            taskObject.setLon(jsonString.getString(TAG_TASK_LON));
            taskObject.setLat(jsonString.getString(TAG_TASK_LAT));
            taskObject.setPlace(jsonString.getString(TAG_TASK_PLACE));
        }catch (Exception e){
            Log.d(TAG, "JSONToTask: " + e.getMessage());
            return null;
        }
        return taskObject;
    }

    public JSONObject UserToJSON(UserObject userObject){

        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put(TAG_USER_ID,String.valueOf(userObject.getID()));
            jsonObject.put(TAG_USER_NAME, userObject.getName());
            jsonObject.put(TAG_USER_PASSWORD, userObject.getPassword());
            jsonObject.put(TAG_USER_DESCRIPTION, userObject.getDescription());
        }catch (Exception e){
            Log.d("mJSONParser:",e.getMessage());
            return null;
        }
        return jsonObject;
    }
    public UserObject JSONtoUser(String jsonString){
        UserObject userObject = new UserObject();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            userObject.setID(jsonObject.getString(TAG_USER_ID));
            userObject.setName(jsonObject.getString(TAG_USER_NAME));
            userObject.setPassword(jsonObject.getString(TAG_USER_PASSWORD));
            userObject.setDescription(jsonObject.getString(TAG_USER_DESCRIPTION));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userObject;
    }
    public UserObject JSONtoUser(JSONObject jsonObject){
        UserObject userObject = new UserObject();
        try {
            userObject.setID(jsonObject.getString(TAG_USER_ID));
            userObject.setName(jsonObject.getString(TAG_USER_NAME));
            userObject.setPassword(jsonObject.getString(TAG_USER_PASSWORD));
            userObject.setDescription(jsonObject.getString(TAG_USER_DESCRIPTION));
        }catch (Exception e){
            Log.d("mJSONParser:",e.getMessage());
            return null;
        }
        return userObject;
    }
}
