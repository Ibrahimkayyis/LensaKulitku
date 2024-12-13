package com.capstone.lensakulitku.view.tracking

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrackingRepository(private val trackingDao: TrackingDao) {

    val allTrackingData: LiveData<List<TrackingData>> = trackingDao.getAllTrackingData()

    suspend fun insertTrackingData(trackingData: TrackingData) {
        withContext(Dispatchers.IO) {
            trackingDao.insertTrackingData(trackingData)
        }
    }

    suspend fun deleteTrackingData(trackingData: TrackingData) {
        withContext(Dispatchers.IO) {
            trackingDao.deleteTrackingData(trackingData)
        }
    }
}