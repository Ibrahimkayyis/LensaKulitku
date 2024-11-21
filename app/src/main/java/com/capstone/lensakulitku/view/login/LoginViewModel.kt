package com.capstone.lensakulitku.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import data.model.LoginRequest
import data.model.LoginResponse
import data.repository.UserRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<String>() // Menyimpan token
    val loginResult: LiveData<String> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loginUser(request: LoginRequest) {
        _isLoading.value = true
        userRepository.loginUser(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        val token = loginResponse.accessToken
                        if (!token.isNullOrEmpty()) {
                            _loginResult.value = token
                            _errorMessage.value = null
                            Log.d("LoginViewModel", "Login successful: $token")
                        } else {
                            _errorMessage.value = "Login failed. No token received."
                            Log.e("LoginViewModel", "Login failed: No token in response body.")
                        }
                    } else {
                        _errorMessage.value = "Login failed. Empty response body."
                        Log.e("LoginViewModel", "Login failed: Response body is null.")
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    _errorMessage.value = "Login failed: $errorMessage"
                    Log.e("LoginViewModel", "Login failed: $errorMessage")
                }
            }


            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "An error occurred: ${t.message}"
                Log.e("LoginViewModel", "Login error: ${t.message}")
            }
        })
    }
}
