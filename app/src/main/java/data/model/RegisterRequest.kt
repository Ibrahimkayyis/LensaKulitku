package data.model

data class RegisterRequest(
    val fullname: String,
    val email: String,
    val password: String,
    val confirmpw: String
)