package com.samkumo.etp4700_projekti;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.samkumo.etp4700_projekti.Constants.CMD_TASK_LIST;

/**
 * Created by Samuli on 11.4.2017.
 */

public class Tasks_TaskFragment extends ListFragment {
    public mTaskAdapter taskAdapter;
    private static UserObject currentUserObject;
    private Context context;
    public static List<TaskObject> taskObjects;

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        currentUserObject = new mPreferences(context).getActiveUser();
        updateList();

        taskAdapter = new mTaskAdapter(getActivity(),R.layout.item_task,taskObjects);
        //ArrayAdapter adapter = new ArrayAdapter<>(getActivity(),R.layout.item_task, taskObjects);
        setListAdapter(taskAdapter);
 }

    private void updateList()
    {
        //Fetching latest tasklist from server
        taskObjects = new ArrayList<TaskObject>();
        try {
            String json = new mAsyncTask(context.getApplicationContext()).execute(CMD_TASK_LIST, currentUserObject.getID()).get();
            List<TaskObject> importedTaskObjects = new mJSONParser().ParseJSONTasklist(json);
            if(importedTaskObjects != null){
                taskObjects = importedTaskObjects;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void reloadFragment(){
        MainActivity mainActivity = (MainActivity)getActivity();
        mainActivity.openTasks();

    }
}
