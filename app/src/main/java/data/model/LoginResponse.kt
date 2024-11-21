package data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("acssesToken")
    val accessToken: String? = null
)
