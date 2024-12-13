package com.capstone.lensakulitku.network

import com.capstone.lensakulitku.view.history.HistoryResponse
import com.capstone.lensakulitku.view.loading.PredictionResponse
import data.model.LoginRequest
import data.model.LoginResponse
import data.model.MedicalProfileRequest
import data.model.RegisterRequest
import data.model.RegisterResponse
import data.model.UpdateResponse
import okhttp3.MultipartBody

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @POST("/users")
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @PUT("users/{id}")
    fun updateUserProfile(
        @Path("id") userId: String,
        @Body profile: MedicalProfileRequest
    ): Call<UpdateResponse>

    @GET("data")
    fun getUserProfile(): Call<MedicalProfileRequest>

    @Multipart
    @POST("/predict")
    fun predictDisease(
        @Part file: MultipartBody.Part
    ): Call<PredictionResponse>

    @GET("predictions")
    fun getHistory(): Call<HistoryResponse>
}
