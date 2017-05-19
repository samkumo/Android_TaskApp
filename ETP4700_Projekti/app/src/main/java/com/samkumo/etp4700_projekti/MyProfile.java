package com.samkumo.etp4700_projekti;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.samkumo.etp4700_projekti.Constants.CMD_USER_UPDATE;
import static com.samkumo.etp4700_projekti.Constants.EVENT_UPDATE_USER_SUCCESS;


/**
 * Created by Samuli on 29.3.2017.
 */

public class MyProfile extends Fragment {
    private UserObject currentUserObject;
    private Context context;
    private TextView tvUserid;
    private TextView tvUsername;
    private TextView tvUserpasswd;
    private TextView tvUserdesc;
    private Button btnChangeDesc;
    private Button btnChangePwd;

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myprofile,container,false);
        tvUserid = (TextView)view.findViewById(R.id.tv_userid);
        tvUsername = (TextView)view.findViewById(R.id.tv_username);
        tvUserpasswd = (TextView)view.findViewById(R.id.tv_userpasswd);
        tvUserdesc = (TextView)view.findViewById(R.id.tv_userdesc);
        btnChangeDesc = (Button)view.findViewById(R.id.btn_changedesc);
        btnChangePwd = (Button)view.findViewById(R.id.btn_changepwd);
        currentUserObject = new mPreferences(context).getActiveUser();

        Init();
        return view;
    }

    private void Init() {
        mPreferences prefs = new mPreferences(context);
        String id = "ID: " + currentUserObject.getID();
        String name = "Username: " + currentUserObject.getName();
        String desc = "Description: " + currentUserObject.getDescription();
        tvUserid.setText(id);
        tvUsername.setText(name);
        tvUserdesc.setText(desc);

        btnChangePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View passwordPrompt = LayoutInflater.from(context).inflate(R.layout.prompt_setpassword,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(passwordPrompt.getContext(),android.R.style.Theme_Material_Light_Dialog_NoActionBar));
                builder.setView(passwordPrompt);

                final EditText et_oldpassword = (EditText)passwordPrompt.findViewById(R.id.et_oldpassword);
                final EditText et_newpassword = (EditText)passwordPrompt.findViewById(R.id.et_newpassword);
                builder
                        .setCancelable(true)
                        .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String correctPassword = currentUserObject.getPassword();
                                String oldPassword = et_oldpassword.getText().toString();
                                String newPassword = et_newpassword.getText().toString();
                                String result = "";

                                if(oldPassword.equals(correctPassword)){
                                    //UserObject input current password correctly
                                    if(newPassword.length()>0){
                                        //New password is at least 1 character long
                                        try{
                                            result = new mAsyncTask(context).execute(CMD_USER_UPDATE, currentUserObject.getID(),newPassword, currentUserObject.getDescription()).get();
                                        }catch (Exception e){
                                            Log.d("CHANGEPWD: ",e.getMessage());
                                        }
                                        if(result.equals(EVENT_UPDATE_USER_SUCCESS)){
                                            currentUserObject.setPassword(newPassword);
                                            new mPreferences(context).setActiveUser(currentUserObject);
                                            Log.d("MyProfile", "Description updated!");
                                        }else{
                                            Log.d("MyProfile", "Description update failed!");
                                        }
                                        reloadFragment();

                                    }else {
                                        //New password was too short or blank
                                        Toast.makeText(context,getResources().getString(R.string.invalidnewpassword),Toast.LENGTH_SHORT).show();
                                        et_newpassword.setText("");
                                        et_oldpassword.setText("");
                                    }
                                }else{
                                    //Old password was incorrect
                                    Toast.makeText(context,getResources().getString(R.string.invalidoldpassword),Toast.LENGTH_SHORT).show();
                                    et_newpassword.setText("");
                                    et_oldpassword.setText("");
                                }
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        btnChangeDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View descriptionPrompt = LayoutInflater.from(context).inflate(R.layout.prompt_setdescription,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(descriptionPrompt.getContext(), android.R.style.Theme_Material_Light_Dialog_NoActionBar));
                builder.setView(descriptionPrompt);

                final EditText inputfield = (EditText)descriptionPrompt.findViewById(R.id.et_newdescription);
                builder
                        .setCancelable(true)
                        .setPositiveButton("Confirm",new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){

                                String result = "";
                                String newDesc = inputfield.getText().toString();
                                try {
                                    result = new mAsyncTask(context).execute(CMD_USER_UPDATE, currentUserObject.getID(), currentUserObject.getPassword(), newDesc).get();
                                }catch (Exception e){
                                    Log.d("UPDATEDESC: ",e.getMessage());
                                }
                                if(result.equals(EVENT_UPDATE_USER_SUCCESS)) {
                                    currentUserObject.setDescription(newDesc);
                                    new mPreferences(context).setActiveUser(currentUserObject);
                                    Log.d("MyProfile", "Description updated!");
                                }else{
                                    Log.d("MyProfile", "Description update failed!");
                                }
                                reloadFragment();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                        }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });
    }


    private void reloadFragment(){
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }
}
