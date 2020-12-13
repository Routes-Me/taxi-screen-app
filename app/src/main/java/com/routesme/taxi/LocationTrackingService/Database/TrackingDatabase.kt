package com.routesme.taxi.LocationTrackingService.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.routesme.taxi.LocationTrackingService.Model.LocationFeed
import com.routesme.taxi.LocationTrackingService.Model.VideoTracking

@Database(entities = [LocationFeed::class,VideoTracking::class], version = 3 ,exportSchema = false)
public abstract class TrackingDatabase  : RoomDatabase(){
    abstract fun locationFeedsDao(): LocationFeedsDao
    abstract fun videoTrackingDeo():VideoTrackingDeo
companion object {
        @Volatile private var instance: TrackingDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){ instance ?: buildDatabase(context).also { instance = it} }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context, TrackingDatabase::class.java, "tracking.db").allowMainThreadQueries()
                .build()
    }


}