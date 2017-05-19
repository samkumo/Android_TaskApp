package com.samkumo.etp4700_projekti;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Samuli on 9.5.2017.
 */

public class mLogHandler {
    private static final String TAG = "mLogHandler";
    private boolean storageAvailable;
    private Activity activity;
    private Context context;


    public mLogHandler(Context context){
        this.context = context;
        if(!isExternalStorageAvailable() || isExternalStorageReadOnly()){
            storageAvailable = false;
            Log.d(TAG, "mLogHandler: External storage not available!");

        }else {
            storageAvailable = true;
        }
    }

    private boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(extStorageState)){
            return true;
        }else {
            return false;
        }
    }
    private boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)){
            return true;
        }else{
            return false;
        }
    }

    public File getFile(String filename){

        if(!isExternalStorageAvailable() || isExternalStorageReadOnly()){
            Log.d(TAG, "getFile: External storage not available!");
            return null;
        }{
            File file = new File(context.getExternalFilesDir(null),filename);
            return file;
        }

    }
    public void writeFile(String filename, String string){
        writeFile(filename,string,false);
    }
    public void writeFile(String filename,String string,boolean append){
        File file = getFile(filename);
        if(file != null){
            try {
                int mode;
                if(append){
                    mode = Context.MODE_APPEND;
                }else {
                    mode = Context.MODE_PRIVATE;
                }
                FileOutputStream fos = context.openFileOutput(filename,mode);
                fos.write(string.getBytes());
                fos.write(System.lineSeparator().getBytes());
                fos.close();
            } catch (IOException e) {
                Log.d(TAG, "writeFile: ");
                e.printStackTrace();
            }

        }else{
            Log.d(TAG, "writeFile: File could not be accessed");
        }
    }
    public String readFile(String filename){
        String output = "";
        File file = getFile(filename);
        if(file != null){
            try {
                String instream = "";
                String inbuffer = "";
                FileInputStream fis = context.openFileInput(filename);
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                while((instream = br.readLine())!= null){
                    inbuffer += instream + "\n";
                }
                br.close();
                output = inbuffer;
                return output;
            } catch (IOException e) {
                Log.d(TAG, "readFile: ");
                e.printStackTrace();
            }
            return output;
        }else {
            Log.d(TAG, "readFile: File could not be accessed");
            return output;
        }
    }
    public void deleteFile(String filename){
        context.deleteFile(filename);
    }
}
