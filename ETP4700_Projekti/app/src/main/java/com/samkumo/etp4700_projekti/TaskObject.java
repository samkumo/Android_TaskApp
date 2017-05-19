package com.samkumo.etp4700_projekti;

/**
 * Created by Samuli on 31.3.2017.
 */

public class TaskObject {
    private String id;
    private String userId;
    private String start;
    private String stop;
    private String explanation;
    private String description;
    private String lon;
    private String lat;
    private String place;

    public TaskObject(){}
    public TaskObject(String id, String userId, String start, String stop, String explanation, String description, String lon, String lat, String place){
        setId(id);
        setUserId(userId);
        setStart(start);
        setStop(stop);
        setExplanation(explanation);
        setDescription(description);
        setLon(lon);
        setLat(lat);
        setPlace(place);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    @Override
    public String toString() {
        return "TaskID: " + getId() + "\nAssigned to: " + getUserId() + "\nDescription: " + getDescription() + "\nPlace: " + getPlace();
    }
}
