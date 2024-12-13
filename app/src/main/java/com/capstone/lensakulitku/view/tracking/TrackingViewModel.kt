package com.capstone.lensakulitku.view.tracking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TrackingViewModel(private val repository: TrackingRepository) : ViewModel() {

    // LiveData untuk semua data tracking
    val trackingList: LiveData<List<TrackingData>> = repository.allTrackingData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    // Fungsi untuk menyimpan data tracking baru
    fun insertTrackingData(trackingData: TrackingData) {
        viewModelScope.launch {
            try {
                _isLoading.postValue(true)
                repository.insertTrackingData(trackingData)
            } catch (e: Exception) {
                _errorMessage.postValue(e.message)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    // Fungsi untuk menghapus data tracking
    fun deleteTrackingData(trackingData: TrackingData) {
        viewModelScope.launch {
            try {
                _isLoading.postValue(true)
                repository.deleteTrackingData(trackingData)
            } catch (e: Exception) {
                _errorMessage.postValue(e.message)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}
