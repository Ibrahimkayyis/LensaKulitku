package data.repository

import com.capstone.lensakulitku.network.ApiConfig
import data.model.LoginRequest
import data.model.LoginResponse
import data.model.RegisterRequest
import data.model.RegisterResponse
import retrofit2.Call


class UserRepository {
    private val apiService = ApiConfig.getApiService()

    fun registerUser(request: RegisterRequest): Call<RegisterResponse> {
        return apiService.registerUser(request)
    }

    fun loginUser(request: LoginRequest): Call<LoginResponse> {
        return apiService.loginUser(request)
    }
}
