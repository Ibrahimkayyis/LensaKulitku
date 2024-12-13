package data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.capstone.lensakulitku.network.ApiConfig
import data.model.LoginRequest
import data.model.LoginResponse
import data.model.MedicalProfileRequest
import data.model.RegisterRequest
import data.model.RegisterResponse
import data.model.UpdateResponse
import retrofit2.Call

class UserRepository(private val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

    // Instance ApiService yang dikonfigurasi dengan Context
    private val apiService = ApiConfig.getApiService(context)

    // Fungsi untuk mendaftarkan pengguna baru
    fun registerUser(request: RegisterRequest): Call<RegisterResponse> {
        return apiService.registerUser(request)
    }

    // Fungsi untuk login pengguna
    fun loginUser(request: LoginRequest): Call<LoginResponse> {
        return apiService.loginUser(request)
    }

    // Fungsi untuk memperbarui profil pengguna
    fun updateUserProfile(userId: String, profile: MedicalProfileRequest): Call<UpdateResponse> {
        Log.d("API_REQUEST", "Updating user profile with ID: $userId")
        Log.d("API_REQUEST", "Payload: $profile")
        return apiService.updateUserProfile(userId, profile)
    }

    // Fungsi untuk mengambil profil pengguna
    fun getUserProfile(): Call<MedicalProfileRequest> {
        Log.d("API_REQUEST", "Fetching user profile")
        return apiService.getUserProfile()
    }
    // Fungsi untuk menyimpan token ke SharedPreferences
    fun saveToken(token: String) {
        sharedPreferences.edit().putString("auth_token", token).apply()
    }

    // Fungsi untuk mengambil token dari SharedPreferences
    fun getToken(): String? {
        val token = sharedPreferences.getString("auth_token", null)
        Log.d("UserRepository", "Retrieved token: $token")
        return token
    }

    // Fungsi untuk mengembalikan context
    fun getContext(): Context {
        return context
    }
}
