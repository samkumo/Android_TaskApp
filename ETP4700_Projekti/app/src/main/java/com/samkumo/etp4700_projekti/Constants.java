package com.samkumo.etp4700_projekti;

/**
 * Created by Samuli on 24.3.2017.
 *
 * Using Constants class as a easier way to access global constants
 * You could use Resources files instead, but I find this method less obfuscated and more conventient.
 */

public class Constants {
    public static final String URL_USER_LIST = "https://<YOUR SERVER HERE>/users.php";           //returns a list of all users in JSON format
    public static final String URL_USER_DETAILS = "https://<YOUR SERVER HERE>/user.php";         //..?Name=XX&Password=YY // returns user details in JSON
    public static final String URL_USER_INSERT = "https://<YOUR SERVER HERE>/insertuser.php";    //..?Name=xx&Password=yy&Description=Something // inserts a new user
    public static final String URL_USER_DELETE = "https://<YOUR SERVER HERE>/deleteuser.php";    //..?ID=Z // Delete this user by ID"
    public static final String URL_USER_UPDATE = "https://<YOUR SERVER HERE>/updateuser.php";    //..?ID=Z&Password=yy&Description=Something else // Update user
    public static final String URL_TASK_LIST = "https://<YOUR SERVER HERE>/tasks.php";           //returns a list of all tasks in JSON format
    public static final String URL_TASK_USERTASKS = "https://<YOUR SERVER HERE>/UserAndFreeTasks.php";   //..?UserID=Z // returns  a list of user/free tasks in JSON format
    public static final String URL_TASK_ADD = "https://<YOUR SERVER HERE>/inserttask.php";       //..?Description=Paint%20the%20house&Lon=50.00&Lat=20.000&Place=Someplace // A new task
    public static final String URL_TASK_DELETE = "https://<YOUR SERVER HERE>/deletetask.php";    //..?Id=Z // Delete this task by ID
    public static final String URL_TASK_RESERVE = "https://<YOUR SERVER HERE>/reservetask.php";  //..?Id=Z&UserId=X // Reserves task Z to user X
    public static final String URL_TASK_START = "https://<YOUR SERVER HERE>/starttask.php";      //..?Id=Z // Starts the task Z
    public static final String URL_TASK_STOP = "https://<YOUR SERVER HERE>/stoptask.php";        //..?Id=Z&Explanation=Some info // Stop the task Z with some explanation

    public static final String CMD_USER_LIST = "10";
    public static final String CMD_USER_DETAILS = "11";
    public static final String CMD_USER_INSERT = "12";
    public static final String CMD_USER_DELETE = "13";
    public static final String CMD_USER_UPDATE = "14";
    public static final String CMD_USER_ID = "15";
    public static final String CMD_TASK_LIST = "20";
    public static final String CMD_TASK_USERTASKS = "21";
    public static final String CMD_TASK_ADD = "22";
    public static final String CMD_TASK_DELETE = "23";
    public static final String CMD_TASK_RESERVE = "24";
    public static final String CMD_TASK_START = "25";
    public static final String CMD_TASK_STOP = "26";

    public static final String TAG_USER_ID = "ID";
    public static final String TAG_USER_NAME = "Name";
    public static final String TAG_USER_PASSWORD = "Password";
    public static final String TAG_USER_DESCRIPTION = "Description";

    public static final String TAG_TASK_ID = "ID";
    public static final String TAG_TASK_USERID = "UserID";
    public static final String TAG_TASK_START = "Start";
    public static final String TAG_TASK_STOP = "Stop";
    public static final String TAG_TASK_EXPLANATION = "Explanation";
    public static final String TAG_TASK_DESCRIPTION = "Description";
    public static final String TAG_TASK_LON = "Lon";
    public static final String TAG_TASK_LAT = "Lat";
    public static final String TAG_TASK_PLACE = "Place";

    public static final String EVENT_LOGIN_SUCCESS = "EVENT_LOGIN_SUCCESS";
    public static final String EVENT_LOGIN_FAILED = "EVENT_LOGIN_FAILED";
    public static final String EVENT_INVALID_ID = "EVENT_INVALID_ID";
    public static final String EVENT_INSERT_USER_SUCCESS = "EVENT_INSERT_USER_SUCCESS";
    public static final String EVENT_INSERT_USER_FAILED = "EVENT_INSERT_USER_FAILED";
    public static final String EVENT_UPDATE_USER_SUCCESS = "EVENT_UPDATE_USER_SUCCESS";
    public static final String EVENT_UPDATE_USER_FAILED = "EVENT_UPDATE_USER_FAILED";
    public static final String EVENT_TASK_LIST_GET_SUCCESS = "EVENT_TASK_LIST_GET_SUCCESS";
    public static final String EVENT_TASK_LIST_GET_FAILED = "EVENT_TASK_LIST_GET_FAILED";

    public static final String KEY_LOGIN_STATUS = "LOGIN_STATUS";
    public static final String KEY_USER_ID = "USER_ID";
    public static final String KEY_USER_NAME = "USER_NAME";
    public static final String KEY_USER_PWD = "USER_PWD";
    public static final String KEY_USER_DESCRIPTION = "USER_DESCRIPTION";
    public static final String KEY_SELECTED_LOCATION = "SELECTED_LOCATION";
    public static final String KEY_UPDATE_INTERVAL = "UPDATE_INTERVAL";
    public static final String KEY_UPDATE_DISTANCE = "UPDATE_DISTANCE";
    public static final String KEY_TOGGLE_BACKGROUNDSERVICE = "TOGGLE_BACKGROUNDSERVICE";
    public static final String KEY_USERLIST = "USERLIST";
    public static final String KEY_USERTASKLIST = "USERTASKLIST";
    public static final String KEY_TASKLIST = "TASKLIST";

    public static final String EXTRA_USER_DETAILS = "com.samkumo.etp4700_projekti.USERID";

    public static final String MY_SHARED_PREFERENCES = "com.samkumo.etp4700_projekti.SHARED_PREFERENCES";

    public static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 50;
    public static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 51;
    public static final int MY_PERMISSION_READ_EXTERNAL_STORAGE = 52;
    public static final int MY_PERMISSION_WRITE_EXTERNAL_STORAGE = 53;

    public static final int TASK_LOCATION_REQUEST = 200;

    public static final boolean DEFAULT_TOGGLE_STATUS = false;
    public static final int DEFAULT_UPDATE_INTERVAL = 5000;
    public static final int DEFAULT_UPDATE_DISTANCE = 50;
    public static final String DEFAULT_EXPLANTION = "TASK_COMPLETE";

    public static final String SERVICELOG_FILENAME = "servicelog.txt";
    public static final String SERVICELOG_DIRECTORY = "logs";

}
