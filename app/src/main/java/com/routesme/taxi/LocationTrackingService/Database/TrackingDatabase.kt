package com.routesme.taxi.LocationTrackingService.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.routesme.taxi.LocationTrackingService.Model.LocationFeed
import com.routesme.taxi.LocationTrackingService.Model.MessageFeed

@Database(entities = [LocationFeed::class, MessageFeed::class], version = 1 ,exportSchema = false)
public abstract class TrackingDatabase  : RoomDatabase(){
    abstract fun locationFeedsDao(): LocationFeedsDao
    abstract fun messageFeedsDao(): MessageFeedsDao

/*
    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: TrackingDatabase? = null

        fun getDatabase(context: Context): TrackingDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, TrackingDatabase::class.java, "tracking_database").build()
                INSTANCE = instance
                return instance
            }
        }
    }
*/

companion object {
        @Volatile private var instance: TrackingDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){ instance ?: buildDatabase(context).also { instance = it} }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context, TrackingDatabase::class.java, "tracking.db").allowMainThreadQueries().build()
    }
}