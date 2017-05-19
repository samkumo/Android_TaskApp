package com.samkumo.etp4700_projekti;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.samkumo.etp4700_projekti.Constants.*;

/**
 * Created by Samuli on 24.3.2017.
 */

public class mAsyncTask extends AsyncTask<String, Void, String>{
    public Context mContext;
    private ProgressDialog proDialog;

    public mAsyncTask(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Showing progress loading dialog
        proDialog = new ProgressDialog(mContext.getApplicationContext());
        proDialog.setMessage("Please wait…");
        proDialog.setCancelable(false);
        //proDialog.show();

    }

    @Override
    protected String doInBackground(String... params) {
        StringBuilder builder;
        //String jsonString;
        ArrayList<UserObject> userlist;
        ArrayList<TaskObject> taskObjectList;
        String returnValue;

        String cmd = params[0];

        switch (cmd){
            case CMD_USER_LIST:
                readHTTPStream(URL_USER_LIST);
                break;
            case CMD_USER_DETAILS:
                builder = new StringBuilder();
                builder.append(URL_USER_DETAILS);
                builder.append("?Name=");
                builder.append(params[1]);
                builder.append("&Password=");
                builder.append(params[2]);
                returnValue = readHTTPStream(builder.toString());

                try {
                    userlist = new mJSONParser().ParseJSONUserlist(returnValue);

                    if(userlist.size() != 0) {
                        return new mJSONParser().UserToJSON(userlist.get(0)).toString();
                    }else{
                        return EVENT_LOGIN_FAILED;
                    }
                }catch(Exception e) {
                    Log.d("TASKER:", "Login failed, parser error: " + e.getMessage());
                    return EVENT_LOGIN_FAILED;
                }
            case CMD_USER_INSERT:
                builder = new StringBuilder();
                builder.append(URL_USER_INSERT);
                builder.append("?Name=");
                builder.append(params[1]);
                builder.append("&Password=");
                builder.append(params[2]);
                builder.append("&Description=");
                builder.append(params[3]);
                returnValue = readHTTPStream(builder.toString());

                if(returnValue.charAt(0) == '0'){
                    return EVENT_INSERT_USER_SUCCESS;
                }else {
                    return EVENT_INSERT_USER_FAILED;
                }
            case CMD_USER_UPDATE:
                builder = new StringBuilder();
                builder.append(URL_USER_UPDATE);
                builder.append("?ID=");
                builder.append(params[1]);
                builder.append("&Password=");
                builder.append(params[2]);
                builder.append("&Description=");
                builder.append(params[3]);
                returnValue = readHTTPStream(builder.toString());

                if(returnValue.equals("true")){
                    return EVENT_UPDATE_USER_SUCCESS;
                }else {
                    return EVENT_UPDATE_USER_FAILED;
                }
            case CMD_TASK_LIST:
                builder = new StringBuilder();
                builder.append(URL_TASK_LIST);
                returnValue =readHTTPStream(builder.toString());
                return returnValue;

            case CMD_TASK_USERTASKS:
                builder = new StringBuilder();
                builder.append(URL_TASK_USERTASKS);
                builder.append("?UserID=");
                builder.append(params[1]);
                returnValue =readHTTPStream(builder.toString());
                return returnValue;
            case CMD_TASK_ADD:
                builder = new StringBuilder();
                builder.append(URL_TASK_ADD);
                builder.append("?Description=");
                builder.append(params[1]);
                builder.append("&Lon=");
                builder.append(params[2]);
                builder.append("&Lat=");
                builder.append(params[3]);
                builder.append("&Place=");
                builder.append(params[4]);
                returnValue = readHTTPStream(builder.toString());
                return returnValue;
            case CMD_TASK_START:
                builder = new StringBuilder();
                builder.append(URL_TASK_START);
                builder.append("?Id=");
                builder.append(params[1]);
                returnValue = readHTTPStream(builder.toString());
                Log.d("CMD_TASK_START::",returnValue);
                return returnValue;
            case CMD_TASK_STOP:
                builder = new StringBuilder();
                builder.append(URL_TASK_STOP);
                builder.append("?Id=");
                builder.append(params[1]);
                builder.append("&Explanation=");
                builder.append(params[2]);
                returnValue = readHTTPStream(builder.toString());
                Log.d("CMD_TASK_STOP::",returnValue);
                return returnValue;
            case CMD_TASK_RESERVE:
                builder = new StringBuilder();
                builder.append(URL_TASK_RESERVE);
                builder.append("?Id=");
                builder.append(params[1]);
                builder.append("&UserId=");
                builder.append(params[2]);
                returnValue = readHTTPStream(builder.toString());
                return returnValue;
            case CMD_TASK_DELETE:
                builder = new StringBuilder();
                builder.append(URL_TASK_DELETE);
                builder.append("?Id=");
                builder.append(params[1]);
                returnValue = readHTTPStream(builder.toString());
                return returnValue;
            default:
                return null;
        }
        return null;
    }

    private String readHTTPStream(String urlString) {
        String ret = "";
        try{
            URL url  =new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            InputStream stream = new BufferedInputStream(httpURLConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder stringBuilder = new StringBuilder();

            String inputString;
            while ((inputString = bufferedReader.readLine())!=null){
                stringBuilder.append(inputString);
            }
            Log.d("Response:",stringBuilder.toString());
            ret = stringBuilder.toString();
        }catch (Exception e){
            Log.d("Response:",e.getMessage());
        }
        return ret;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
       /*
        if (proDialog.isShowing()){
            proDialog.dismiss();
        }
*/
    }

}
