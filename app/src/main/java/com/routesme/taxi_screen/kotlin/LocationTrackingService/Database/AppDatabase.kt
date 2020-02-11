package com.routesme.taxi_screen.kotlin.LocationTrackingService.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.routesme.taxi_screen.kotlin.Model.Tracking

@Database(entities = arrayOf(Tracking::class), version = 1 ,exportSchema = false)
abstract class AppDatabase  : RoomDatabase(){
    abstract fun trackingDao(): TrackingDao


    companion object {
        @Volatile private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context)= instance
                ?: synchronized(LOCK){
            instance
                    ?: buildDatabase(context).also { instance = it}
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
                AppDatabase::class.java, "todo-list.db").allowMainThreadQueries()
                .build()
    }

}