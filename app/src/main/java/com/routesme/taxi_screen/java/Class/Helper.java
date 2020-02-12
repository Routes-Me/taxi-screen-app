package com.routesme.taxi_screen.java.Class;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.routesme.taxi_screen.kotlin.Server.RetrofitService;
import com.routesme.taxiscreen.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Helper {
    private static final String TAG = "Helper";

    public static String getConfigValue(Context context, String name) {
        Resources resources = context.getResources();
        try {
            InputStream rawResource = resources.openRawResource(R.raw.config);
            Properties properties = new Properties();
            properties.load(rawResource);
            return properties.getProperty(name);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Unable to find the config file: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Failed to open config file.");
        }
        return null;
    }

    @Nullable
    public static String getConfigValue(@NotNull RetrofitService.Factory factory, @NotNull String s) {
        return null;
    }
}
