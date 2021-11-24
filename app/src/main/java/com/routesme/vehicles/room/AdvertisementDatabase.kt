package com.routesme.vehicles.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.routesme.vehicles.room.dao.AdvertisementDoa
import com.routesme.vehicles.room.entity.AdvertisementTracking

@Database(entities = [AdvertisementTracking::class], version = 14, exportSchema = false)
abstract class AdvertisementDatabase : RoomDatabase() {
    abstract fun advertisementTracking(): AdvertisementDoa

    companion object {
        @Volatile
        private var instance: AdvertisementDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance
                ?: synchronized(LOCK) {
                    instance
                            ?: buildDatabase(context).also { instance = it }
                }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context, AdvertisementDatabase::class.java, "db_advertisement.db").fallbackToDestructiveMigration().build()
    }
}
