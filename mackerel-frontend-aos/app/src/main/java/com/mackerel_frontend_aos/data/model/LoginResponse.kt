package com.mackerel_frontend_aos.data.model

data class LoginResponse(
    val id: String,
    val createAt: String,
    val data: LoginData
)

data class LoginData(
    val accessToken: String,
    val refreshToken: String
)