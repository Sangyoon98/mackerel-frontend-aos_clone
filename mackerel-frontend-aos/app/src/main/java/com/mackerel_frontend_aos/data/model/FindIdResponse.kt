package com.mackerel_frontend_aos.data.model

data class FindIdResponse (
    val id: String,
    val createAt: String,
    val data: FindIdData
)

data class FindIdData(
    val memberId: String,
    val createAt: String
)