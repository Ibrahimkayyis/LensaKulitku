package com.capstone.lensakulitku.view.loading

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.lensakulitku.network.ApiConfig
import com.capstone.lensakulitku.view.loading.PredictionResponse
import data.repository.UserRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AnalysisViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _predictionResult = MutableLiveData<PredictionResponse?>()
    val predictionResult: LiveData<PredictionResponse?> = _predictionResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun predictDisease(imageFile: File) {
        // Retrieve token from UserRepository
        val token = userRepository.getToken() ?: run {
            _errorMessage.value = "Authorization token is missing."
            return
        }

        // Prepare image file as multipart form-data
        val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageFile)
        val filePart = MultipartBody.Part.createFormData("file", imageFile.name, requestBody)

        // Show loading indicator
        _isLoading.value = true

        // Call predictDisease API using ApiConfig's analysis service
        ApiConfig.getAnalysisApiService(userRepository.getContext()).predictDisease(filePart)
            .enqueue(object : Callback<PredictionResponse> {
                override fun onResponse(
                    call: Call<PredictionResponse>,
                    response: Response<PredictionResponse>
                ) {
                    // Hide loading indicator
                    _isLoading.value = false

                    if (response.isSuccessful) {
                        // Update prediction result on success
                        _predictionResult.value = response.body()
                    } else {
                        // Handle failed response
                        _errorMessage.value = "Failed to analyze image: ${response.errorBody()?.string()}"
                    }
                }

                override fun onFailure(call: Call<PredictionResponse>, t: Throwable) {
                    // Hide loading indicator
                    _isLoading.value = false
                    // Handle network or other failures
                    _errorMessage.value = "Error: ${t.message}"
                }
            })
    }
}
