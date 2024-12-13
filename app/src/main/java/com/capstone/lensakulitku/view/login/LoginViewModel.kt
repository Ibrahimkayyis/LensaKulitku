package com.capstone.lensakulitku.view.login

import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import data.model.LoginRequest
import data.model.LoginResponse
import data.repository.UserRepository
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    // Data class to hold both token and userId
    data class LoginResult(val token: String, val userId: String)

    private val _loginResult = MutableLiveData<LoginResult?>()
    val loginResult: LiveData<LoginResult?> = _loginResult

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
                    val token = response.body()?.accessToken
                    if (!token.isNullOrEmpty()) {
                        val userId = extractUserIdFromToken(token)
                        if (userId != null) {
                            // Save the token to SharedPreferences
                            userRepository.saveToken(token)

                            _loginResult.value = LoginResult(token, userId)
                            Log.d("LoginViewModel", "Login successful. Token saved and User ID retrieved.")
                        } else {
                            Log.e("LoginViewModel", "Failed to extract User ID from token.")
                            _errorMessage.value = "Failed to retrieve User ID."
                        }
                    } else {
                        _errorMessage.value = "Login failed. Token is null."
                        Log.e("LoginViewModel", "Login failed: Token is null.")
                    }
                } else {
                    _errorMessage.value = "Login failed. Please check your credentials."
                    Log.e("LoginViewModel", "Login failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "An error occurred: ${t.message}"
                Log.e("LoginViewModel", "Login error: ${t.message}")
            }
        })
    }

    private fun extractUserIdFromToken(token: String): String? {
        val payloadJson = decodeJWT(token)
        Log.d("LoginViewModel", "Decoded Payload: $payloadJson")
        return payloadJson?.optString("userId")
    }

    private fun decodeJWT(token: String): JSONObject? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) {
                throw IllegalArgumentException("Invalid JWT token format")
            }
            val payload = parts[1]
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_WRAP)
            val decodedPayload = String(decodedBytes, Charsets.UTF_8)
            JSONObject(decodedPayload)
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Error decoding JWT: ${e.message}")
            null
        }
    }
}
