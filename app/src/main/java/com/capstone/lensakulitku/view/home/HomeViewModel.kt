package com.capstone.lensakulitku.view.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.lensakulitku.view.history.DataItem
import com.capstone.lensakulitku.view.history.HistoryDatabase
import kotlinx.coroutines.launch

class HomeViewModel(private val context: Context) : ViewModel() {
    private val _latestAnalysis = MutableLiveData<DataItem?>()
    val latestAnalysis: LiveData<DataItem?> = _latestAnalysis

    fun fetchLatestAnalysis() {
        viewModelScope.launch {
            val db = HistoryDatabase.getInstance(context)
            val latestEntity = db.historyDao().getLatestHistory()
            val item = latestEntity?.let {
                DataItem(
                    severity = it.severity,
                    severityLevel = it.severityLevel,
                    createdAt = it.createdAt,
                    userId = it.userId,
                    namaPenyakit = it.namaPenyakit,
                    suggestion = it.suggestion,
                    description = it.description,
                    id = it.id,
                    imageUri = it.imageUri
                )
            }
            _latestAnalysis.postValue(item)
        }
    }
}
