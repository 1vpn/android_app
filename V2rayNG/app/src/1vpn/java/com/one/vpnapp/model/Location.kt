package com.one.vpnapp.model

data class Location(
    val country: String,
    val countryCode: String,
    val xrayHost: String? = null,
    val publicKey: String? = null,
    val shortId: String? = null,
    val isPremium: Boolean = false,
)