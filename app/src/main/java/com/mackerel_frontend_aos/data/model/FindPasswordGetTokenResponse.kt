package com.mackerel_frontend_aos.data.model

data class FindPasswordGetTokenResponse(
    val id: String,
    val createAt: String,
    val data: FindPasswordGetTokenData
)

data class FindPasswordGetTokenData(
    val passwordAuthToken: String,
    val passwordAuthTokenExpiresIn: Int
)
