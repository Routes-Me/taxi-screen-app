package com.routesme.taxi.LocationTrackingService.Database

import android.content.Context
import androidx.room.Room

object TrackingDatabaseBuilder {

    private var INSTANCE: TrackingDatabase? = null

    fun getInstance(context: Context): TrackingDatabase {
        if (INSTANCE == null) {
            synchronized(TrackingDatabase::class) {
                INSTANCE = buildRoomDB(context)
            }
        }
        return INSTANCE!!
    }

    private fun buildRoomDB(context: Context) = Room.databaseBuilder(context.applicationContext, TrackingDatabase::class.java, "tracking.db").build()
}