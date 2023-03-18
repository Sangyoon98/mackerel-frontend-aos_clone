package com.mackerel_frontend_aos.data.model

data class IdExistResponse(
    val id: String,
    val createAt: String,
    val data: IdExistData
)

data class IdExistData(
    val isExistence: Boolean
)