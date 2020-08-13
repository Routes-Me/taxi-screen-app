package com.routesme.taxi_screen.kotlin.LocationTrackingService.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.routesme.taxi_screen.kotlin.Model.LocationFeed
import com.routesme.taxi_screen.kotlin.Model.MessageFeed

@Database(entities = [LocationFeed::class, MessageFeed::class], version = 1 ,exportSchema = false)
abstract class TrackingDatabase  : RoomDatabase(){
    abstract fun locationFeedsDao(): LocationFeedsDao
    abstract fun messageFeedsDao(): MessageFeedsDao

    companion object {
        @Volatile private var instance: TrackingDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){ instance ?: buildDatabase(context).also { instance = it} }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context, TrackingDatabase::class.java, "todo-list.db").allowMainThreadQueries().build()
    }
}