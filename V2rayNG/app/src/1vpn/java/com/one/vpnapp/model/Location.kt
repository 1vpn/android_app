package com.one.vpnapp.model

data class Server(
    val host: String,
    val realityServerName: String? = null,
)

data class Location(
    val city: String? = null,
    val cityCode: String? = null,
    val country: String,
    val countryCode: String,
    val servers: List<Server>? = null,
    val isPremium: Boolean = false,
)
