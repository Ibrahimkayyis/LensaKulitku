package com.capstone.lensakulitku.view.tracking

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TrackingAnalysisViewModel : ViewModel() {

    private val _comparisonResult = MutableLiveData<TrackingResultData>()
    val comparisonResult: LiveData<TrackingResultData> = _comparisonResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun processTrackingData(
        baselineImageUri: Uri,
        baselineSeverity: String,
        newImageUri: Uri,
        newSeverity: String
    ) {
        _isLoading.value = true

        // Simulate processing or API request if necessary
        try {
            val trackingResultData = compareResults(
                baselineImageUri,
                baselineSeverity,
                newImageUri,
                newSeverity
            )
            _comparisonResult.postValue(trackingResultData)
        } catch (e: Exception) {
            _errorMessage.postValue("Failed to process tracking data: ${e.message}")
        } finally {
            _isLoading.postValue(false)
        }
    }

    private fun compareResults(
        baselineImageUri: Uri,
        baselineSeverity: String,
        newImageUri: Uri,
        newSeverity: String
    ): TrackingResultData {
        val severityComparison = when {
            newSeverity == baselineSeverity -> "same"
            newSeverity > baselineSeverity -> "worsened"
            else -> "improved"
        }

        val recommendation = when (severityComparison) {
            "same" -> "Maintain your current treatment plan."
            "worsened" -> "Consider consulting a dermatologist for further advice."
            "improved" -> "Great progress! Keep following your care plan."
            else -> "Unknown status."
        }

        return TrackingResultData(
            baselineImageUri = baselineImageUri,
            baselineSeverity = baselineSeverity,
            newImageUri = newImageUri,
            newSeverity = newSeverity,
            severityComparison = severityComparison,
            recommendation = recommendation
        )
    }
}

data class TrackingResultData(
    val baselineImageUri: Uri,
    val baselineSeverity: String,
    val newImageUri: Uri,
    val newSeverity: String,
    val severityComparison: String,
    val recommendation: String
)