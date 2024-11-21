package com.capstone.lensakulitku.network

import data.model.LoginRequest
import data.model.LoginResponse
import data.model.RegisterRequest
import data.model.RegisterResponse

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/users")
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>
}