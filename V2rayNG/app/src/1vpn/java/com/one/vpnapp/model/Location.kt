package com.one.vpnapp.model

data class Location(
    val city: String,
    val cityCode: String,
    val country: String,
    val countryCode: String,
    val xrayHost: String? = null,   // single host from backend API
    val xrayHosts: List<String>? = null, // multiple hosts for local free locations
    val publicKey: String? = null,
    val shortId: String? = null,
    val realityServerName: String? = null,
    val isPremium: Boolean = false,
)