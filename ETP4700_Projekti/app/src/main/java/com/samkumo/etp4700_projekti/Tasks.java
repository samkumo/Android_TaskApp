package com.samkumo.etp4700_projekti;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static android.app.Activity.RESULT_OK;
import static com.samkumo.etp4700_projekti.Constants.CMD_TASK_ADD;
import static com.samkumo.etp4700_projekti.Constants.KEY_SELECTED_LOCATION;
import static com.samkumo.etp4700_projekti.Constants.TASK_LOCATION_REQUEST;

/**
 * Created by Samuli on 31.3.2017.
 *
 * TODO: Find a way to color or otherwise indicate task status
 *
 */

public class Tasks extends Fragment{
    private UserObject currentUserObject;
    private Context context;
    private FloatingActionButton addButton;
    private static View createTaskPrompt;



    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks,container,false);
        Tasks_TaskFragment fragment1 = new Tasks_TaskFragment();
        getFragmentManager().beginTransaction().replace(R.id.tasks_content_frame,fragment1).commit();
        currentUserObject = new mPreferences(context).getActiveUser();
        addButton = (FloatingActionButton)view.findViewById(R.id.btn_addbutton);
        Init();
        return view;
    }


    void Init(){

        //Hoverbutton that opens up Create Task dialog
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tasks.createTaskPrompt = LayoutInflater.from(context).inflate(R.layout.prompt_createtask,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(createTaskPrompt.getContext(),android.R.style.Theme_Material_Light_Dialog));
                builder.setView(createTaskPrompt);

                final EditText et_description = (EditText)createTaskPrompt.findViewById(R.id.et_createtask_description);
                final TextView tv_lon = (TextView)createTaskPrompt.findViewById(R.id.tv_createtask_lon);
                final TextView tv_lat = (TextView)createTaskPrompt.findViewById(R.id.tv_createtask_lat);
                final EditText et_place = (EditText)createTaskPrompt.findViewById(R.id.et_createtask_place);
                final EditText et_adminname = (EditText)createTaskPrompt.findViewById(R.id.et_createtask_adminname);
                final EditText et_adminpassword = (EditText)createTaskPrompt.findViewById(R.id.et_createtask_adminpassword);
                final Button btn_currentLoc = (Button)createTaskPrompt.findViewById(R.id.btn_setlocation_current);
                final Button btn_mapLoc = (Button)createTaskPrompt.findViewById(R.id.btn_setlocation_map);

                //Using helper class to get current location coordinates
                //If user does not select location via buttons, default it to current location
                final mGPSHandler gpsHandler = new mGPSHandler(getActivity());
                Double lon = gpsHandler.getLon();
                Double lat = gpsHandler.getLat();
                tv_lon.setText(lon.toString());
                tv_lat.setText(lat.toString());


                builder
                        .setCancelable(true)
                        .setTitle("Create new task")
                        .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String inputAdminname = et_adminname.getText().toString();
                                String inputAdminpassword = et_adminpassword.getText().toString();

                                //Check admin credentials
                                if(inputAdminname.equals(getString(R.string.LOCALADMIN)) && inputAdminpassword.equals(getString(R.string.LOCALADMINPWD))){
                                    String inputDescription = et_description.getText().toString();
                                    String inputPlace = et_place.getText().toString();
                                    String inputLon = tv_lon.getText().toString();
                                    String inputLat = tv_lat.getText().toString();

                                    try {
                                        // Create new task on remote server using mAsyncTask
                                        String result = new mAsyncTask(Tasks.createTaskPrompt.getContext()).execute(CMD_TASK_ADD,inputDescription,inputLon,inputLat,inputPlace).get();
                                        if(result.equals("true")){
                                            Log.d("TASKS::","TASK ADDED");
                                            //Update the list of tasks
                                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                                            ft.detach(Tasks.this).attach(Tasks.this).commit();
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }


                                }else{
                                    // Login was unsuccessful, clear login fields
                                    Log.d("TASKS::","Incorrect admin or password");
                                    et_adminname.setText("");
                                    et_adminpassword.setText("");
                                }

                            }
                        })

                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });


                //Button listener for setting current GPS location as task location
                btn_currentLoc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("Tasks::","btn_currentLoc CLICK");
                        //Using helper class to get current location coordinates
                        Double lon = gpsHandler.getLon();
                        Double lat = gpsHandler.getLat();

                        TextView tv_lon = (TextView)createTaskPrompt.findViewById(R.id.tv_createtask_lon);
                        TextView tv_lat = (TextView)createTaskPrompt.findViewById(R.id.tv_createtask_lat);
                        tv_lon.setText(lon.toString());
                        tv_lat.setText(lat.toString());
                    }
                });

                //Button listener for opening map activity that allows user to choose task location
                btn_mapLoc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("Tasks::","btn_mapLoc CLICK");
                        Double lon = gpsHandler.getLon();
                        Double lat = gpsHandler.getLat();
                        // Build query string for maps activity: "geo:lat,lon?z=zoom"
                        // For zoom levels, refer to Google documentation for more details
                        StringBuilder querybuilder = new StringBuilder();
                        querybuilder.append("geo:");
                        querybuilder.append(lon.toString());
                        querybuilder.append(",");
                        querybuilder.append(lat.toString());
                        querybuilder.append("?z=");
                        querybuilder.append("8");
                        // Create an Intent using MapsActivity class
                        Intent mapIntent = new Intent(getActivity(),MapsActivity.class);
                        // Get current coordinates to be used as starting point on the map
                        ArrayList<String> coordinates = new ArrayList<String>();
                        coordinates.add(lon.toString());
                        coordinates.add(lat.toString());
                        mapIntent.putStringArrayListExtra("COORDINATES",coordinates);
                        // Make the Intent explicit by setting the Google Maps package
                        mapIntent.setPackage("com.google.android.apps.maps");
                        // Attempt to start an activity that can return a result we need
                        startActivityForResult(mapIntent, TASK_LOCATION_REQUEST);

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });




    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == TASK_LOCATION_REQUEST){
            //If MapsActivity returned valid location coordinates, use them for creating new Task
            if(resultCode == RESULT_OK){
                Log.d("Tasks::","RESULT OK");
                final TextView tv_lon = (TextView)createTaskPrompt.findViewById(R.id.tv_createtask_lon);
                final TextView tv_lat = (TextView)createTaskPrompt.findViewById(R.id.tv_createtask_lat);
                double[] lonlat = data.getExtras().getDoubleArray(KEY_SELECTED_LOCATION);
                Log.d("DATA::",lonlat.toString());
                tv_lon.setText(String.valueOf(lonlat[0]));
                tv_lat.setText(String.valueOf(lonlat[1]));

            }
        }
    }

}
