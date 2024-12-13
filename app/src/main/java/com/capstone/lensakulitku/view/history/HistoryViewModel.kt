package com.capstone.lensakulitku.view.history

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.repository.UserRepository
import kotlinx.coroutines.launch

class HistoryViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _historyList = MutableLiveData<List<DataItem>>()
    val historyList: LiveData<List<DataItem>> = _historyList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun fetchHistory(context: Context) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val db = HistoryDatabase.getInstance(context)
                val entities = db.historyDao().getAllHistory()
                val dataItems = entities.map {
                    Log.d("HistoryViewModel", "ID: ${it.id}, Baseline URI: ${it.baselineImageUri}")
                    DataItem(
                        severity = it.severity,
                        severityLevel = it.severityLevel,
                        createdAt = it.createdAt,
                        userId = it.userId,
                        namaPenyakit = it.namaPenyakit,
                        suggestion = it.suggestion,
                        description = it.description,
                        id = it.id,
                        imageUri = it.imageUri,
                        baselineImageUri = it.baselineImageUri
                    )
                }
                _historyList.postValue(dataItems)
                _isLoading.postValue(false)
            } catch (e: Exception) {
                _errorMessage.postValue("Error loading history: ${e.message}")
                _isLoading.postValue(false)
            }
        }
    }

    // Fungsi untuk menyimpan analisis baru ke database
    fun saveAnalysisResult(
        context: Context,
        severity: String,
        severityLevel: Int,
        namaPenyakit: String,
        suggestion: String,
        description: String,
        imageUri: Uri,
        baselineImageUri: Uri?,
        userId: Int,
        createdAt: String
    ) {
        viewModelScope.launch {
            try {
                val db = HistoryDatabase.getInstance(context)

                // Simpan entitas baru ke database
                val historyEntity = HistoryEntity(
                    id = (db.historyDao().getLatestHistory()?.id ?: 0) + 1,
                    severity = severity,
                    severityLevel = severityLevel,
                    createdAt = createdAt,
                    userId = userId,
                    namaPenyakit = namaPenyakit,
                    suggestion = suggestion,
                    description = description,
                    imageUri = imageUri.toString(),
                    baselineImageUri = baselineImageUri?.toString()
                )
                db.historyDao().insertHistory(historyEntity)
            } catch (e: Exception) {
                _errorMessage.postValue("Error saving analysis: ${e.message}")
            }
        }
    }

    // Fungsi untuk memperbarui baselineImageUri
    fun updateBaselineImageUri(context: Context, historyId: Int, baselineImageUri: Uri) {
        viewModelScope.launch {
            try {
                val db = HistoryDatabase.getInstance(context)
                db.historyDao().updateBaselineImageUri(historyId, baselineImageUri.toString())
            } catch (e: Exception) {
                _errorMessage.postValue("Error updating baseline image: ${e.message}")
            }
        }
    }

}
