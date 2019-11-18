package com.example.routesapp.AndroidVideoCache;

import android.app.Application;
import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;

import java.io.File;

public class App extends Application {

    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        App app = (App) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }
/*
    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer(this);
    }
*/
    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                //.maxCacheSize(  1024 * 1024 * 1024)// 1 Gb for cache
                .maxCacheSize(Long.valueOf(1024 * 1024 * 1024) * 30)
                .build();


    }


    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }


}
