package com.routesme.taxi_screen.java.Tracking.Class;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.routesme.taxi_screen.java.Tracking.database.AppDatabase;
import com.routesme.taxi_screen.java.Tracking.database.AppExecutors;
import com.routesme.taxi_screen.java.Tracking.database.TrackingDao;
import com.routesme.taxi_screen.java.Tracking.model.Tracking;
import com.routesme.taxi_screen.java.Tracking.model.TrackingLocation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import tech.gusavila92.websocketclient.WebSocketClient;

public class TrackingHandler {
    private static final String TAG = "TrackingHandler";
    private WebSocketClient trackingWebSocket;
    private SimpleDateFormat dateformat;
    private String currentStamptime;
    private AppDatabase mDb;
    private TrackingDao trackingDao;
    private Tracking tracking;
    private TrackingLocation trackingFirstLocation, trackingLastLocation;
    private Location firstLocation, lastLocation;


    public TrackingHandler() {
    }

    public TrackingHandler(Context context, WebSocketClient trackingWebSocket) {
        this.trackingWebSocket = trackingWebSocket;
        dateformat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
        //Tracking ... Room Database...
        mDb = AppDatabase.getInstance(context.getApplicationContext());
        trackingDao = mDb.trackingDao();

    }


    public void sendOfflineTrackingToServer() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (trackingDao.loadAllLocations() != null) {
                    final List<Tracking> trackings = trackingDao.loadAllLocations();
                    if (!trackings.isEmpty()) {
                        for (int l = 0; l < trackings.size(); l++) {
                            tracking = trackings.get(l);
                            sendLocationViaSocket(tracking);
                        }
                        //clear Tracking Table....
                        clearTrackingTable();
                    } else {
                        Log.d(TAG, "trackingWebSocket ... no offline location is exists!");
                    }
                }
            }
        });
    }

    public void insertLocation(final TrackingLocation newLocation) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                currentStamptime = dateformat.format(new Date());
                final Tracking tracking = new Tracking(newLocation, currentStamptime);
                final List<Tracking> trackings = trackingDao.loadAllLocations();
                if (!trackings.isEmpty()) {
                    TrackingLocation lastTabletLocation = trackingDao.loadLastLocation().getLocation();
                    if (!newLocation.getLatitude().equals(lastTabletLocation.getLatitude()) && !newLocation.getLongitude().equals(lastTabletLocation.getLongitude())) {
                        trackingDao.insertLocation(tracking);
                    }
                } else {
                    trackingDao.insertLocation(tracking);
                }
            }
        });
    }


    public void locationChecker() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (trackingDao.loadAllLocations() != null) {
                    final List<Tracking> trackings = trackingDao.loadAllLocations();
                    if (!trackings.isEmpty()) {
                        trackingFirstLocation = new TrackingLocation(trackingDao.loadFirstLocation().getLocation().getLatitude(), trackingDao.loadFirstLocation().getLocation().getLongitude());
                        trackingLastLocation = new TrackingLocation(trackingDao.loadLastLocation().getLocation().getLatitude(), trackingDao.loadLastLocation().getLocation().getLongitude());
                        if (!trackingFirstLocation.getLatitude().equals(trackingLastLocation.getLatitude()) && !trackingFirstLocation.getLongitude().equals(trackingLastLocation.getLongitude())) {
                            float distance = getDistance();
                            if (distance >= 2) {
                                sendLocationViaSocket(trackingDao.loadLastLocation());
                                clearTrackingTable();
                            }
                        }

                    }
                }
            }
        });
    }

    private float getDistance() {
        firstLocation = new Location("First Location");
        lastLocation = new Location("Last Location");
        firstLocation.setLatitude(trackingFirstLocation.getLatitude());
        firstLocation.setLongitude(trackingFirstLocation.getLongitude());
        lastLocation.setLatitude(trackingLastLocation.getLatitude());
        lastLocation.setLongitude(trackingLastLocation.getLongitude());

        //Calculate distance between two locations...
        //Distance in Meter
        float distance = firstLocation.distanceTo(lastLocation);
        return distance;
    }



    private void sendLocationViaSocket(Tracking tracking) {
        String timestamp = tracking.getTimestamp();
        TrackingLocation trackingLocation = tracking.getLocation();
        String message = "location:" + trackingLocation.getLatitude() + "," + trackingLocation.getLongitude() + ";timestamp:" + timestamp;
        trackingWebSocket.send(message);
        Log.i("trackingWebSocket:  ", "Send message:  " + message);
    }

    public void clearTrackingTable() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                trackingDao.clearTrackingData();
            }
        });
    }


}
