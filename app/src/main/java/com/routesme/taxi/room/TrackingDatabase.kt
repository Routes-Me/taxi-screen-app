package com.routesme.taxi.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.routesme.taxi.room.doa.LocationFeedsDao
import com.routesme.taxi.room.entity.LocationFeed

@Database(entities = [LocationFeed::class], version = 11, exportSchema = false)
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