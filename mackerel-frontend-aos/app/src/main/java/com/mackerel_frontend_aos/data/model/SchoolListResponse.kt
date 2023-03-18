package com.mackerel_frontend_aos.data.model

data class SchoolListResponse (
    val id: String,
    val createAt: String,
    val data: List<SchoolListDataList>
)

data class SchoolListDataList (
    val name: String,
    val address: String
)