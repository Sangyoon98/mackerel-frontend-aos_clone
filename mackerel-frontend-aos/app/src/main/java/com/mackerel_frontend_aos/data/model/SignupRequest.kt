package com.mackerel_frontend_aos.data.model

data class SignupRequest(
    val memberId: String,
    val password: String,
    val nickname: String,
    val school: String,
    val grade: String,
    val name: String,
    val phone: String,
    val agreements: SignupAgreements
)

data class SignupAgreements(
    val adNotifications: Boolean
)