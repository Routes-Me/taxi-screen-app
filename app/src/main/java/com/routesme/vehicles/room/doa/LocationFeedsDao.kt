package com.routesme.vehicles.room.doa

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.routesme.vehicles.room.entity.LocationFeed

@Dao
interface LocationFeedsDao {

    @Transaction
    @Query("SELECT * FROM LocationFeeds ORDER BY id DESC LIMIT 100")
    suspend fun getFeeds(): List<LocationFeed>

    @Query("DELETE FROM LocationFeeds WHERE id BETWEEN :id2 AND :id1")
    suspend fun deleteFeeds(id1: Int, id2: Int)

    @Insert
    suspend fun insertLocation(locationFeed: LocationFeed)

    @Transaction
    @Query("SELECT * FROM LocationFeeds ORDER BY id")
    suspend fun getAllFeeds(): List<LocationFeed>
}