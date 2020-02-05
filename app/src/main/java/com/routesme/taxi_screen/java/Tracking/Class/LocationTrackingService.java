package com.routesme.taxi_screen.java.Tracking.Class;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import com.routesme.taxi_screen.java.Class.Helper;

import java.net.URI;
import java.net.URISyntaxException;

import tech.gusavila92.websocketclient.WebSocketClient;

public class LocationTrackingService {

    private Context context;
    private SharedPreferences sharedPreferences;
    private WebSocketClient trackingWebSocket;
    private TrackingHandler trackingHandler;
    private boolean isHandlerTrackingRunning = false;
    private Handler handlerTracking;
    private Runnable runnableTracking;


    public LocationTrackingService(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("userData", Activity.MODE_PRIVATE);
        createWebSocket();
        trackingHandler = new TrackingHandler(context, trackingWebSocket);
        startTracking();
    }


    private void createWebSocket() {
        String tabletSerialNo = sharedPreferences.getString("tabletSerialNo", null);
        setTrackingWebSocketConfiguration(trackingWebSocketUri(),tabletSerialNo);
    }

    private URI trackingWebSocketUri() {
        URI trackingWebSocketUri = null;
        try {
            trackingWebSocketUri = new URI(Helper.getConfigValue(context, "trackingWebSocketUri"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return trackingWebSocketUri;
    }

    private void setTrackingWebSocketConfiguration(URI trackingWebSocketUri, final String tabletSerialNo) {
        trackingWebSocket = new WebSocketClient(trackingWebSocketUri) {
            @Override
            public void onOpen() {
                Log.i("trackingWebSocket:  ", "Opened");
                //Send deviceId to server (Device Identifier)
                sendMessageViaSocket("deviceId:" + tabletSerialNo);
                //Send offline locations to server if it exists....
                trackingHandler.sendOfflineTrackingToServer();
                //Start Tracking Timer after 500 melli seconds...
                handlerTracking.post(runnableTracking);
            }

            @Override
            public void onTextReceived(String message) {
                Log.i("trackingWebSocket:  ", "Received message:  " + message);
            }

            @Override
            public void onBinaryReceived(byte[] data) {
            }

            @Override
            public void onPingReceived(byte[] data) {
            }

            @Override
            public void onPongReceived(byte[] data) {
            }

            @Override
            public void onException(Exception e) {
                Log.i("trackingWebSocket:  ", "Exception Error:   " + e.getMessage());
                stopTrackingTimer();
            }

            @Override
            public void onCloseReceived() {
                Log.i("trackingWebSocket:  ", "Closed !");
                stopTrackingTimer();
            }
        };
        trackingWebSocket.setConnectTimeout(10000);
        trackingWebSocket.setReadTimeout(60000);
        trackingWebSocket.enableAutomaticReconnection(5000);
    }


    private void sendMessageViaSocket(String message) {
        trackingWebSocket.send(message);
        Log.i("trackingWebSocket:  ", "Send message:  " + message);
    }

    private void startTracking() {
        LocationFinder locationFinder = new LocationFinder(context, trackingHandler);
        if (locationFinder.canGetLocation()) {
            setupTrackingTimer();
            trackingWebSocket.connect();
        } else {
            locationFinder.showSettingsAlert();
        }
    }


    private void setupTrackingTimer() {
        runnableTracking = new Runnable() {
            @Override
            public void run() {
                isHandlerTrackingRunning = true;
                Log.i("trackingWebSocket:  ", "Tracking Timer running ...");
                trackingHandler.locationChecker();
                handlerTracking.postDelayed(runnableTracking, 5000);
            }
        };
        handlerTracking = new Handler();
    }

    private void stopTrackingTimer() {
        if (isHandlerTrackingRunning) {
            handlerTracking.removeCallbacks(runnableTracking);
            isHandlerTrackingRunning = false;
            Log.i("trackingWebSocket:  ", "Tracking Timer stop ...");
        }
    }

    public void stopLocationTrackingService(){
        if (trackingWebSocket != null) {
            trackingWebSocket.onCloseReceived();
        }
    }
}
