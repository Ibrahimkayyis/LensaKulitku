package com.capstone.lensakulitku.view.tracking

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface TrackingDao {

    @Query("SELECT * FROM tracking_data ORDER BY createdAt DESC")
    fun getAllTrackingData(): LiveData<List<TrackingData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackingData(trackingData: TrackingData)

    @Delete
    suspend fun deleteTrackingData(trackingData: TrackingData)
}