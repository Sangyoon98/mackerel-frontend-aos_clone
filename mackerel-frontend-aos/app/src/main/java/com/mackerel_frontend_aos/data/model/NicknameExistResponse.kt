package com.mackerel_frontend_aos.data.model

data class NicknameExistResponse(
    val id: String,
    val createAt: String,
    val data: NicknameExistData
)

data class NicknameExistData(
    val isExistence: Boolean
)