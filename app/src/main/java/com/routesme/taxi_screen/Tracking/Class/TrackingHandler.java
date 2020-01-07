package com.routesme.taxi_screen.Tracking.Class;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.routesme.taxi_screen.Tracking.database.AppDatabase;
import com.routesme.taxi_screen.Tracking.database.AppExecutors;
import com.routesme.taxi_screen.Tracking.database.TrackingDao;
import com.routesme.taxi_screen.Tracking.model.Tracking;
import com.routesme.taxi_screen.Tracking.model.TrackingLocation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TrackingHandler {
    private static final String TAG = "TrackingHandler";

    private Context context;

    private SimpleDateFormat dateformat;
    private String currentStamptime;

    private AppDatabase mDb;
    private TrackingDao trackingDao ;

    private TrackingLocation trackingFirstLocation, trackingLastLocation;
    private Location firstLocation, lastLocation;


    public TrackingHandler() {
    }

    public TrackingHandler(Context context) {
        this.context = context;
        dateformat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");

        //Tracking ... Room Database...
        mDb = AppDatabase.getInstance(context.getApplicationContext());
        trackingDao = mDb.trackingDao();


    }


    public void insertLocation(final TrackingLocation newLocation) {


            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    currentStamptime = dateformat.format(new Date());
                    final Tracking tracking = new Tracking(newLocation, currentStamptime);
                    final List<Tracking> trackings = trackingDao.loadAllLocations();
                    if (!trackings.isEmpty()){
                        TrackingLocation lastTabletLocation = trackingDao.loadLastLocation().getLocation();
                        if (!newLocation.getLatitude().equals(lastTabletLocation.getLatitude()) && !newLocation.getLongitude().equals(lastTabletLocation.getLongitude())) {

                            trackingDao.insertLocation(tracking);
                            Log.d(TAG, "Tracking ... Insert New Location:  Lat-Long" + newLocation.getLatitude() + " - " + newLocation.getLongitude());
                        }else {
                            Log.d(TAG, "Tracking ... this location is equal to last location in table");
                        }
                    }else {
                        trackingDao.insertLocation(tracking);
                        Log.d(TAG, "Tracking ... Insert new location as a first location in table :  Lat-Long" + newLocation.getLatitude() + " - " + newLocation.getLongitude());
                    }




                }
            });


    }


    public void locationChecker() {

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<Tracking> trackings = trackingDao.loadAllLocations();
                // final int LocationSize = trackingDao.loadAllLocations().size();

                if (!trackings.isEmpty()){
                    Log.d(TAG, "Tracking ... Location size: " + trackings.size());
                    trackingFirstLocation = new TrackingLocation(trackingDao.loadFirstLocation().getLocation().getLatitude(),trackingDao.loadFirstLocation().getLocation().getLongitude());
                    trackingLastLocation = new TrackingLocation(trackingDao.loadLastLocation().getLocation().getLatitude(),trackingDao.loadLastLocation().getLocation().getLongitude());

                    if (!trackingFirstLocation.getLatitude().equals(trackingLastLocation.getLatitude()) && !trackingFirstLocation.getLongitude().equals(trackingLastLocation.getLongitude())){
                        float distance = getDistance();

                        if (distance >= 2){
                            sendLocationViaSocket(trackingDao.loadLastLocation());
                            clearTrackingTable();
                        }else {
                            Log.d(TAG, "Tracking ... Distance is:  " + distance +"m is  less than 2 meter ");
                        }
                    }else {
                        Log.d(TAG, "Tracking ... Distance not changed!");
                    }

                }else {
                    Log.d(TAG, "Tracking ... Location table is empty");
                }

            }
        });

    }

    private float getDistance(){

        firstLocation = new Location("First Location");
        lastLocation = new Location("Last Location");
        firstLocation.setLatitude(trackingFirstLocation.getLatitude());
        firstLocation.setLongitude(trackingFirstLocation.getLongitude());
        lastLocation.setLatitude(trackingLastLocation.getLatitude());
        lastLocation.setLongitude(trackingLastLocation.getLongitude());

        //Calculate distance between two locations...

        //Distance in Meter
        float distance = firstLocation.distanceTo(lastLocation);

        //Distance in KM
        // float distance =firstLocation.distanceTo(lastLocation) / 1000; // in km

        return distance;
    }


    private void sendLocationViaSocket(Tracking tracking) {
        String timestamp = tracking.getTimestamp();
        TrackingLocation trackingLocation = tracking.getLocation();

        Log.d(TAG, "Tracking ... Distance ... Send new location to server via socket:  ( Timestamp:  " + timestamp + "  ,Location... Lat:  " + trackingLocation.getLatitude() + " ,Long:  "+ trackingLocation.getLongitude());
        //then send timestamp & trackingLocation to server via socket....
    }

    private void clearTrackingTable() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                trackingDao.clearTrackingData();
                Log.d(TAG, "Tracking .... Clear Tracking Table");
            }
        });


    }

}
