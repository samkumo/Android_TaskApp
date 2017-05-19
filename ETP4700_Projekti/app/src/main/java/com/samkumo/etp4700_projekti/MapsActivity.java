package com.samkumo.etp4700_projekti;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import static com.samkumo.etp4700_projekti.Constants.KEY_SELECTED_LOCATION;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ArrayList<String> coordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        coordinates = getIntent().getStringArrayListExtra("COORDINATES");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //Clicking on map creates a marker and centers the view on it
                //TODO: Implement smooth panning?
                mMap.clear(); //remove previous marker
                mMap.addMarker(new MarkerOptions().position(latLng));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                //Clicking on marker to get it's location for task
                View createMapPrompt = LayoutInflater.from(MapsActivity.this).inflate(R.layout.prompt_picklocation,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(createMapPrompt.getContext(),android.R.style.Theme_Material_Light_Dialog));
                builder.setView(createMapPrompt);

                final TextView tv_lon = (TextView)createMapPrompt.findViewById(R.id.tv_mapprompt_lon);
                final TextView tv_lat = (TextView)createMapPrompt.findViewById(R.id.tv_mapprompt_lat);

                final LatLng latlon = marker.getPosition();
                double lon = latlon.longitude;
                double lat = latlon.latitude;

                tv_lon.setText(String.format("LON: %.5f",lon));
                tv_lat.setText(String.format("LAT: %.5f",lat));
                builder
                        .setCancelable(true)
                        .setTitle(getResources().getString(R.string.askselectlocation))
                        .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("MapsActivity::","Location confirmed CLICK");
                                LatLng selectedLatlon = marker.getPosition();
                                double[] lonlat = {selectedLatlon.longitude,selectedLatlon.latitude};
                                Intent result = new Intent();
                                result.putExtra(KEY_SELECTED_LOCATION,lonlat);
                                setResult(Activity.RESULT_OK,result);
                                finish();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("MapsActivity::","Cancel CLICK");
                                dialog.cancel();
                            }

                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return false;
            }
        });
        //Additional UI settings for the map
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setCompassEnabled(true);

        mMap.clear();
        double lat = Double.parseDouble(coordinates.get(1));
        double lon = Double.parseDouble(coordinates.get(0));
        LatLng currentLoc = new LatLng(lat,lon);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(8));

        mMap.addMarker(new MarkerOptions().position(currentLoc));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLoc));


    }


}
