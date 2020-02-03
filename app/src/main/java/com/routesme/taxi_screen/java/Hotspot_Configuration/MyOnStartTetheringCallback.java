package com.routesme.taxi_screen.java.Hotspot_Configuration;

public abstract class MyOnStartTetheringCallback {
    /**
     * Called when tethering has been successfully started.
     */
    public abstract void onTetheringStarted();

    /**
     * Called when starting tethering failed.
     */
    public abstract void onTetheringFailed();

}
