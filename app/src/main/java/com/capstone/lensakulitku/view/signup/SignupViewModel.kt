package com.capstone.lensakulitku.view.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import data.model.RegisterRequest
import data.model.RegisterResponse
import data.repository.UserRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _registerResult = MutableLiveData<RegisterResponse>()
    val registerResult: LiveData<RegisterResponse> = _registerResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun registerUser(request: RegisterRequest) {
        _isLoading.value = true
        userRepository.registerUser(request).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val message = response.body()?.msg ?: "Unknown success response"
                    _registerResult.value = RegisterResponse(msg = message)
                    Log.d("SignupViewModel", "Response: $message")
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error occurred"
                    Log.e("SignupViewModel", "Error: $errorMessage")
                    _registerResult.value = RegisterResponse(msg = errorMessage)
                }
            }


            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e("SignupViewModel", "Request failed", t)
            }
        })
    }
}
