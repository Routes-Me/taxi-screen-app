package com.routesme.vehicles.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.routesme.vehicles.room.dao.LocationFeedsDao
import com.routesme.vehicles.room.entity.LocationFeed

@Database(entities = [LocationFeed::class], version = 18, exportSchema = false)
abstract class TrackingDatabase : RoomDatabase() {
    abstract fun locationFeedsDao(): LocationFeedsDao

    companion object {
        @Volatile
        private var instance: TrackingDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance
                ?: synchronized(LOCK) {
                    instance
                            ?: buildDatabase(context).also { instance = it }
                }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context, TrackingDatabase::class.java, "tracking.db").fallbackToDestructiveMigration().build()
    }
}