package com.samkumo.etp4700_projekti;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.samkumo.etp4700_projekti.Constants.CMD_TASK_USERTASKS;

/**
 * Created by Samuli on 31.3.2017.
 */

public class Overview_TaskFragment extends ListFragment {
    private static final String TAG = "Overview_TaskFragment";
    private UserObject currentUserObject;
    private Context context;
    private List<TaskObject> taskObjects;

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
        mTaskAdapter adapter = new mTaskAdapter(getActivity(),
                R.layout.fragment_overview_task, taskObjects);
        setListAdapter(adapter);
    }

    /*
    private List<HelperObject> buildTaskList() {
        //Passing taskObjects to helper objects for easier formatting
        List<HelperObject> objList = new ArrayList<>();
        if(taskObjects != null) {
            StringBuilder builder;
            for (int i = 0; i < taskObjects.size(); i++) {
                HelperObject obj = new HelperObject(taskObjects.get(i).getDescription(), taskObjects.get(i).getUserId());
                objList.add(obj);
            }
            //Sorting list, selected taskObjects up top, available after that and unavailable taskObjects at bottom
            Collections.sort(objList, new Comparator<HelperObject>() {
                public int compare(HelperObject o1, HelperObject o2) {
                    return Integer.compare(o1.priority, o2.priority);
                }
            });
        }else{
           // objList.add(new HelperObject("No taskObjects","None"));
        }
        return objList;
    }
*/



    public void updateList()
    {
        //Fetching latest tasklist from server
        this.taskObjects = new ArrayList<TaskObject>();
        try {
            String json = new mAsyncTask(context.getApplicationContext()).execute(CMD_TASK_USERTASKS, currentUserObject.getID()).get();
            List<TaskObject> importedTaskObjects = new mJSONParser().ParseJSONTasklist(json);
            if(importedTaskObjects != null){
                this.taskObjects = importedTaskObjects;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO implement some logic
    }

    /*
    public class HelperObject{
        public int priority;
        public String description;
        public String status;
        public HelperObject(String description, String userID){
            this.description = description;

            if(userID.equals(currentUserObject.getID())){
                this.priority = 0;
                this.status = "Selected";
            }else if(userID.equals("null")){
                this.priority = 1;
                this.status = "Available";
            }else{
                this.priority = 3;
                this.status = "Unavailable";
            }
        }
        @Override
        public String toString() {
            return "TaskObject: " + this.description + "\nStatus: " + this.status;
        }
    }

*/
}

