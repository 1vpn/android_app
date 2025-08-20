package com.one.vpnapp.model

data class UserData(
    val username: String,
    val email: String,
    val isPremium: Boolean,
    var sessionAuthToken: String,
    var uuid: String,
    var publicKey: String,
    var shortId: String,
    var inboundId: String,
    val locations: List<Location>
)

data class ErrorResponse(
    val error: String,
    val code: Int? = null
)

