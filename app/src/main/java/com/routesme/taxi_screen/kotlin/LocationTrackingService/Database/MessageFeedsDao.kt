package com.routesme.taxi_screen.kotlin.LocationTrackingService.Database

import androidx.room.*
import com.routesme.taxi_screen.kotlin.Model.LocationFeed
import com.routesme.taxi_screen.kotlin.Model.MessageFeed

//Message Feeds Table
@Dao
interface MessageFeedsDao {
    @Query("SELECT * FROM MessageFeeds ORDER BY ID")
    fun loadAllMessages(): List<MessageFeed>

    @Insert
    fun insertMessage(locationFeed: MessageFeed)

    @Update
    fun updateMessage(locationFeed: MessageFeed)

    @Delete
    fun delete(locationFeed: MessageFeed)

    @Query("SELECT * FROM MessageFeeds WHERE id LIKE :id")
    fun loadMessageById(id:Int) : MessageFeed

    //retrieve first MessageFeed
    @Query("SELECT * FROM MessageFeeds ORDER BY id ASC LIMIT 1")
    fun loadFirstMessage(): MessageFeed

    //retrieve last MessageFeed
    @Query("SELECT * FROM MessageFeeds ORDER BY id DESC LIMIT 1")
    fun loadLastMessage(): MessageFeed

    //Delete MessageFeed Data ...
    @Query("DELETE FROM MessageFeeds")
    fun clearMessageFeedsTable()
}