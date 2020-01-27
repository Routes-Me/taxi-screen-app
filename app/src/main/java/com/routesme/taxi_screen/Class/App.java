package com.routesme.taxi_screen.Class;

import android.app.Application;
import android.content.Context;
import com.danikula.videocache.HttpProxyCacheServer;
import com.routesme.taxi_screen.Detect_Network_Connection_Status.ConnectivityReceiver;

public class App extends Application {

    private HttpProxyCacheServer proxy;
    private String technicalSupportUserName, technicalSupportPassword;
    private boolean newLogin = false;
    private int taxiOfficeId = 0;
    private String taxiPlateNumber = null, taxiOfficeName = null;
    //Detect Internet Connection Status...
    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized App getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    public static HttpProxyCacheServer getProxy(Context context) {
        App app = (App) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .maxCacheSize(Long.valueOf(1024 * 1024 * 1024) * 30)
                .build();
    }



    //Getter...
    public String getTechnicalSupportUserName() {
        return technicalSupportUserName;
    }

    public String getTechnicalSupportPassword() {
        return technicalSupportPassword;
    }

    public boolean isNewLogin() {
        return newLogin;
    }

    public int getTaxiOfficeId() {
        return taxiOfficeId;
    }

    public String getTaxiOfficeName() {
        return taxiOfficeName;
    }

    public String getTaxiPlateNumber() {
        return taxiPlateNumber;
    }



    //Setter...
    public void setTechnicalSupportUserName(String technicalSupportUserName) {
        this.technicalSupportUserName = technicalSupportUserName;
    }

    public void setTechnicalSupportPassword(String technicalSupportPassword) {
        this.technicalSupportPassword = technicalSupportPassword;
    }

    public void setNewLogin(boolean newLogin) {
        this.newLogin = newLogin;
    }

    public void setTaxiOfficeId(int taxiOfficeId) {
        this.taxiOfficeId = taxiOfficeId;
    }

    public void setTaxiOfficeName(String taxiOfficeName) {
        this.taxiOfficeName = taxiOfficeName;
    }

    public void setTaxiPlateNumber(String taxiPlateNumber) {
        this.taxiPlateNumber = taxiPlateNumber;
    }
}
