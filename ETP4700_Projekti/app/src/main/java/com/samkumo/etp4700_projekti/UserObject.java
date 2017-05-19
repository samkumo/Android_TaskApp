package com.samkumo.etp4700_projekti;

/**
 * Created by Samuli on 24.3.2017.
 */

public class UserObject {
    public String ID;
    public String Name;
    public String Password;
    public String Description;

    public UserObject(){}
    public UserObject(String id, String name, String password, String description){
        this.ID = id;
        this.Name = name;
        this.Password = password;
        this.Description = description;
    }

    public String getID() {
        return ID;
    }

    public void setID(String newID) {
        ID = newID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String newName) {
        Name = newName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String newPassword) {
        Password = newPassword;
    }

    public String getDescription() {
        return Description;
    }
    public void setDescription(String newDescription){
        Description = newDescription;
    }
}

