package com.routesme.taxi.LocationTrackingService.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.routesme.taxi.LocationTrackingService.Model.AdvertisementTracking
import com.routesme.taxi.LocationTrackingService.Model.LocationFeed

@Database(entities = [AdvertisementTracking::class], version = 1 ,exportSchema = false)
public abstract class AdvertisementDatabase  : RoomDatabase(){
    abstract fun advertisementTracking():AdvertisementDoa
    companion object {
        @Volatile private var instance: AdvertisementDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){ instance ?: buildDatabase(context).also { instance = it} }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context, AdvertisementDatabase::class.java, "advertisement.db").allowMainThreadQueries().build()
    }
}