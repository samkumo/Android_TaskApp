package com.samkumo.etp4700_projekti;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import static com.samkumo.etp4700_projekti.Constants.MY_PERMISSION_ACCESS_FINE_LOCATION;

/**
 * Created by Samuli on 13.4.2017.
 */

public class mGPSHandler {
    Activity activity;
    Context context;
    LocationManager locationManager;
    LocationListener locationListener;
    Location location;
    boolean initialized = false;

    public mGPSHandler(Context context){
        this.context = context;
        InitGPS();

    }
    public mGPSHandler(Activity activity){
        this.activity = activity;
        this.context = activity.getApplicationContext();
        InitGPS();
    }
    void InitGPS() {
        //Initialize variables and locationlistener
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);

            CheckPermission();
            this.locationListener = new mLocationListener();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this.locationListener);
            this.location = locationManager.getLastKnownLocation(provider);
            if(this.location != null){
                this.initialized = true;
            }
        }catch (Exception e){
            Log.d("mGPSHandler::",e.getMessage());
        }
    }

    public double getLat(){
        //Returns latest lateral coordinates
        UpdateLocation();
        double lat = this.location.getLatitude();
        return lat;
    }
    public double getLon(){
        //Returns latest longitudal coordinates
        UpdateLocation();
        double lon = this.location.getLongitude();
        return lon;
    }

    private void CheckPermission(){
        //Helper class to check GPS permissions and display popup if needed
        if(activity != null){
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
            }
        }

    }

    private void UpdateLocation(){
        //Update GPS position

        if(!this.initialized){
            InitGPS();
        }

        CheckPermission();

        try {
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);
            this.location = locationManager.getLastKnownLocation(provider);
        }catch (Exception e){
            Log.d("mGPSHandler::",e.getMessage());
        }

    }

}
