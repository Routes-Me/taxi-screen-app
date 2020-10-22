package com.routesme.taxi.LocationTrackingService.Database

import androidx.room.*
import com.routesme.taxi.LocationTrackingService.Model.MessageFeed

//Message Feeds Table
@Dao
interface MessageFeedsDao {
    @Transaction
    @Query("SELECT * FROM MessageFeeds ORDER BY ID ASC")
    fun getAllMessages(): List<MessageFeed>

    @Transaction
    @Insert
    fun insertFeeds(feeds: List<MessageFeed>)

    @Transaction
    @Delete
    fun delete(feed: MessageFeed)

    //Delete MessageFeed Data ...
    @Transaction
    @Query("DELETE FROM MessageFeeds")
    fun clearMessageFeedsTable()
}