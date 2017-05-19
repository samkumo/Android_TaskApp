package com.samkumo.etp4700_projekti;

import android.Manifest;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import static com.samkumo.etp4700_projekti.Constants.DEFAULT_UPDATE_INTERVAL;
import static com.samkumo.etp4700_projekti.Constants.EXTRA_USER_DETAILS;
import static com.samkumo.etp4700_projekti.Constants.KEY_LOGIN_STATUS;
import static com.samkumo.etp4700_projekti.Constants.KEY_TOGGLE_BACKGROUNDSERVICE;
import static com.samkumo.etp4700_projekti.Constants.KEY_UPDATE_INTERVAL;
import static com.samkumo.etp4700_projekti.Constants.MY_PERMISSION_ACCESS_FINE_LOCATION;
import static com.samkumo.etp4700_projekti.Constants.MY_PERMISSION_READ_EXTERNAL_STORAGE;
import static com.samkumo.etp4700_projekti.Constants.MY_PERMISSION_WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{
    private String TAG = "MainActivity";
    private UserObject userObject;
    private TextView tvTitle;
    private ImageButton toolbarButton;
    public PopupMenu popupMenu;

    private DrawerLayout mDrawerLayout;
    private String[] mMenuTitles;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private static CharSequence mTitle;

    Fragment mainFragment;
    mGPSHandler gpsHandler;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 10000; //10s
    private long FAST_UPDATE_INTERVAL = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check storage permissions
        checkStoragePermissions();

        //Setting up drawer menu
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String jsonString = intent.getStringExtra(EXTRA_USER_DETAILS);
        setUserObject(jsonString);

        mTitle = mDrawerTitle = getResources().getString(R.string.apptitle);
        mMenuTitles = getResources().getStringArray(R.array.menu_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, mMenuTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        getSupportActionBar().setTitle(getResources().getString(R.string.overview));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_menu_black_24dp));

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                //getSupportActionBar().setTitle(mDrawerTitle);
                //getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        if (savedInstanceState == null) {
            selectItem(0);
        }
        //Initialize GoogleApiClient for GPS
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        CheckPermission();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){ //Service needs location permission
            if(new mPreferences(this).getValueBool(KEY_LOGIN_STATUS)) { //..and user needs to be logged in
                if(new mPreferences(this).getValueBool(KEY_TOGGLE_BACKGROUNDSERVICE)){ //..and background service must be enabled in settings,
                  startPHPService();
                }
            }
        }else{//else, stop service if it was running
            stopPHPService();
        }
    }

    public void startPHPService() {
        mPreferences mprefs = new mPreferences(this);
        mprefs.setValueBool(KEY_TOGGLE_BACKGROUNDSERVICE,true);
        int interval = DEFAULT_UPDATE_INTERVAL;
        try {
            interval = Integer.parseInt(mprefs.getValue(KEY_UPDATE_INTERVAL));
        }catch (Exception e){
            Log.d(TAG, "startPHPService: " + e.getMessage());
        }
        Intent serviceIntent = new Intent(this,PHPService.class);
        PendingIntent pendingServiceIntent = PendingIntent.getService(this,(int)System.currentTimeMillis(),serviceIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)MainActivity.this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingServiceIntent);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,0,interval,pendingServiceIntent);
        Log.d(TAG, "startPHPService()");
    }
    public void stopPHPService(){
        new mPreferences(this).setValueBool(KEY_TOGGLE_BACKGROUNDSERVICE,false);
        stopService(new Intent(this,PHPService.class));
        Log.d(TAG, "stopPHPService()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mCurrentLocation != null) {
            Log.d(TAG, "onConnected:: Current location:" + mCurrentLocation.toString());
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        }
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FAST_UPDATE_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }
    private void setUserObject(String jsonString) {
        userObject = new mJSONParser().JSONtoUser(jsonString);
    }

    @Override
    public void onBackPressed() {
        openOverview();
    }

    private void openOverview() {
        mTitle = getResources().getString(R.string.overview);
        Overview fragment = new Overview();
        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

    }

    private void openMyprofile() {
        mTitle = getResources().getString(R.string.myprofile);
        MyProfile fragment = new MyProfile();
        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

    }

    public void openTasks() {
        mTitle = getResources().getString(R.string.tasks);
        Tasks fragment = new Tasks();
        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

    }

    private void openSettings() {
        mTitle = getResources().getString(R.string.settings);
        SettingsFragment fragment = new SettingsFragment();
        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    private void logout() {
        mPreferences prefs = new mPreferences(this);
        prefs.clearActiveUser();
        prefs.setValueBool(KEY_LOGIN_STATUS, false);
        stopPHPService();
        Intent intent = new Intent(this, LoginActivity.class);
        finishAffinity();
        startActivity(intent);
        Toast.makeText(this, "You have been logged out", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: " + connectionResult.getErrorMessage());
    }

    @Override
    public void onLocationChanged(Location location) {
        // New location has now been determined
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Log.d(TAG,"onLocationChanged::" + msg);
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
    private void selectItem(int position) {
        // update the main content by replacing fragments
        android.app.Fragment fragment = new MenuItemFragment();
        Bundle args = new Bundle();
        args.putInt(MenuItemFragment.ARG_ITEM_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mMenuTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
        switch (position){
            case 0:
                openOverview();
                return;
            case 1:
                openMyprofile();
                return;
            case 2:
                openTasks();
                return;
            case 3:
                openSettings();
                return;
            case 4:
                logout();
                return;
            default:
                return;
        }


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void CheckPermission(){
        //Helper class to check GPS permissions and display popup if needed
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
        }

    }

    private void checkStoragePermissions(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSION_READ_EXTERNAL_STORAGE);
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSION_WRITE_EXTERNAL_STORAGE);
        }
    }

}
