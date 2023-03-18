package com.mackerel_frontend_aos.data.model

data class FindPasswordGetTokenRequest(
    val memberId: String,
    val name: String,
    val phone: String
)
