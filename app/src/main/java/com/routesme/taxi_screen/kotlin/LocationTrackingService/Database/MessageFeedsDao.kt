package com.routesme.taxi_screen.kotlin.LocationTrackingService.Database

import androidx.room.*
import com.routesme.taxi_screen.kotlin.Model.MessageFeed

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