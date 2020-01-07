package com.routesme.taxi_screen.Tracking.Class;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.routesme.taxi_screen.Tracking.model.TrackingLocation;


public class LocationFinder extends Service implements LocationListener {


    //Tracking ... Room Database...
    private TrackingHandler trackingHandler;

    Context context;
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    // flag for GPS status
    boolean canGetLocation = false;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 2; // 2 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 100 * 10 * 1; // 1 seconds
    // Declaring a Location Manager
    protected LocationManager locationManager;


    //Constructor....
    public LocationFinder(Context context, TrackingHandler trackingHandler) {
        this.context = context;

      //  RequestLocationPermission();
        this.trackingHandler = trackingHandler;
        getLocation();

    }


    @Override
    public void onLocationChanged(Location location) {
       // latitude = location.getLatitude();
        //longitude = location.getLongitude();
      // Toast.makeText(context, "Changed Location:   lat-lng :  " + latitude + "  —  " + longitude, Toast.LENGTH_LONG).show();


        if (location != null) {
            try {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            trackingHandler.insertLocation(new TrackingLocation(latitude,longitude));
            Toast.makeText(context, "Updated Location ... lat-lng :  " + latitude + "  —  " + longitude, Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
            }


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @SuppressLint("MissingPermission")
    private Location getLocation() {
        try {


            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);




            if (!isGPSEnabled  &&  !isNetworkEnabled) {
                // no network provider is enabled
                 Log.e("Location-Network-GPS", "Disable");
            } else {

                this.canGetLocation = true;

                   if (isGPSEnabled) {
                    Log.e("Location-GPS", "Enabled");
                        if (location == null) {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            if (locationManager != null) {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }
                    }else if (isNetworkEnabled) {
                    Log.e("Location-Network", "Enabled");
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }

                }

/*
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    Log.e("Location-Network", "Enabled");
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                else if (isGPSEnabled) {
                    Log.e("Location-GPS", "Enabled");
                        if (location == null) {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            if (locationManager != null) {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }
                    }
*/

            }




        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }



    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }



    public boolean canGetLocation() {
        return this.canGetLocation;
    }



    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("GPS settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }






}
