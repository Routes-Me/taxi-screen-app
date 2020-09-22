package com.routesme.taxi_screen.LocationTrackingService.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.routesme.taxi_screen.LocationTrackingService.Model.MessageFeed

//Message Feeds Table
@Dao
interface MessageFeedsDao {
    @Query("SELECT * FROM MessageFeeds ORDER BY ID ASC")
    fun getAllMessages(): List<MessageFeed>

    @Insert
    fun insertFeeds(feeds: List<MessageFeed>)

    @Delete
    fun delete(feed: MessageFeed)

    //Delete MessageFeed Data ...
    @Query("DELETE FROM MessageFeeds")
    fun clearMessageFeedsTable()
}