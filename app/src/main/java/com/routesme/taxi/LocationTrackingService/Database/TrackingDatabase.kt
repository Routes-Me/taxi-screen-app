package com.routesme.taxi.LocationTrackingService.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.routesme.taxi.LocationTrackingService.Model.AdvertisementTracking
import com.routesme.taxi.LocationTrackingService.Model.LocationFeed
@Database(entities = [LocationFeed::class], version = 5 ,exportSchema = false)
public abstract class TrackingDatabase  : RoomDatabase(){
    abstract fun locationFeedsDao(): LocationFeedsDao
companion object {
        @Volatile private var instance: TrackingDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){ instance ?: buildDatabase(context).also { instance = it} }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context, TrackingDatabase::class.java, "tracking.db").allowMainThreadQueries().build()
    }
}