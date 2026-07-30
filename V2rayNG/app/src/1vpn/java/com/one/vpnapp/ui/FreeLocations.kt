package com.one.vpnapp.ui

import com.one.vpnapp.model.Location
import com.one.vpnapp.model.Server

val freeLocations = listOf(
    Location(
        city = "Amsterdam",
        cityCode = "ams",
        country = "Netherlands",
        countryCode = "nl",
        servers = listOf(
            Server(
                host = "free-amsterdam-node-1.cloudwidecdn.com",
                realityServerName = "www.apple.com",
            ),
            Server(
                host = "free-amsterdam-node-2.cloudwidecdn.com",
                realityServerName = "www.cloudflare.com",
            ),
        ),
    ),
    Location(
        city = "Singapore",
        cityCode = "sgp",
        country = "Singapore",
        countryCode = "sg",
        servers = listOf(
            Server(
                host = "free-singapore-node-1.cloudwidecdn.com",
                realityServerName = "www.apple.com",
            ),
            Server(
                host = "free-singapore-node-2.cloudwidecdn.com",
                realityServerName = "www.cloudflare.com",
            )
        ),
    ),
    Location(
        city = "Los Angeles",
        cityCode = "lax",
        country = "United States",
        countryCode = "us",
        servers = listOf(
            Server(
                host = "free-los-angeles-node-1.cloudwidecdn.com",
                realityServerName = "www.apple.com",
            ),
            Server(
                host = "free-los-angeles-node-2.cloudwidecdn.com",
                realityServerName = "www.cloudflare.com",
            ),
        ),
    ),
    Location(
        city = "Melbourne",
        cityCode = "mel",
        country = "Australia",
        countryCode = "au",
        isPremium = true
    ),
    Location(
        city = "Sydney",
        cityCode = "syd",
        country = "Australia",
        countryCode = "au",
        isPremium = true
    ),
    Location(
        city = "Sao Paulo",
        cityCode = "sao",
        country = "Brazil",
        countryCode = "br",
        isPremium = true
    ),
    Location(
        city = "Toronto",
        cityCode = "yto",
        country = "Canada",
        countryCode = "ca",
        isPremium = true
    ),
    Location(
        city = "Santiago",
        cityCode = "scl",
        country = "Chile",
        countryCode = "cl",
        isPremium = true
    ),
    Location(
        city = "Paris",
        cityCode = "cdg",
        country = "France",
        countryCode = "fr",
        isPremium = true
    ),
    Location(
        city = "Frankfurt",
        cityCode = "fra",
        country = "Germany",
        countryCode = "de",
        isPremium = true
    ),
    Location(
        city = "Delhi",
        cityCode = "del",
        country = "India",
        countryCode = "in",
        isPremium = true
    ),
    Location(
        city = "Mumbai",
        cityCode = "bom",
        country = "India",
        countryCode = "in",
        isPremium = true
    ),
    Location(
        city = "Bangalore",
        cityCode = "blr",
        country = "India",
        countryCode = "in",
        isPremium = true
    ),
    Location(
        city = "Tel Aviv",
        cityCode = "tlv",
        country = "Israel",
        countryCode = "il",
        isPremium = true
    ),
    Location(
        city = "Osaka",
        cityCode = "itm",
        country = "Japan",
        countryCode = "jp",
        isPremium = true
    ),
    Location(
        city = "Tokyo",
        cityCode = "nrt",
        country = "Japan",
        countryCode = "jp",
        isPremium = true
    ),
    Location(
        city = "Seoul",
        cityCode = "icn",
        country = "South Korea",
        countryCode = "kr",
        isPremium = true
    ),
    Location(
        city = "Mexico City",
        cityCode = "mex",
        country = "Mexico",
        countryCode = "mx",
        isPremium = true
    ),
    Location(
        city = "Warsaw",
        cityCode = "waw",
        country = "Poland",
        countryCode = "pl",
        isPremium = true
    ),
    Location(
        city = "Singapore",
        cityCode = "sgp",
        country = "Singapore",
        countryCode = "sg",
        isPremium = true
    ),
    Location(
        city = "Johannesburg",
        cityCode = "jnb",
        country = "South Africa",
        countryCode = "za",
        isPremium = true
    ),
    Location(
        city = "Madrid",
        cityCode = "mad",
        country = "Spain",
        countryCode = "es",
        isPremium = true
    ),
    Location(
        city = "Stockholm",
        cityCode = "sto",
        country = "Sweden",
        countryCode = "se",
        isPremium = true
    ),
    Location(
        city = "London",
        cityCode = "lhr",
        country = "United Kingdom",
        countryCode = "uk",
        isPremium = true
    ),
    Location(
        city = "Manchester",
        cityCode = "man",
        country = "United Kingdom",
        countryCode = "uk",
        isPremium = true
    ),
    Location(
        city = "Chicago",
        cityCode = "ord",
        country = "United States",
        countryCode = "us",
        isPremium = true
    ),
    Location(
        city = "Dallas",
        cityCode = "dfw",
        country = "United States",
        countryCode = "us",
        isPremium = true
    ),
    Location(
        city = "Seattle",
        cityCode = "sea",
        country = "United States",
        countryCode = "us",
        isPremium = true
    ),
    Location(
        city = "San Francisco",
        cityCode = "sjc",
        country = "United States",
        countryCode = "us",
        isPremium = true
    ),
    Location(
        city = "Miami",
        cityCode = "mia",
        country = "United States",
        countryCode = "us",
        isPremium = true
    ),
    Location(
        city = "Honolulu",
        cityCode = "hnl",
        country = "United States",
        countryCode = "us",
        isPremium = true
    ),
    Location(
        city = "New York",
        cityCode = "ewr",
        country = "United States",
        countryCode = "us",
        isPremium = true
    ),
    Location(
        city = "Atlanta",
        cityCode = "atl",
        country = "United States",
        countryCode = "us",
        isPremium = true
    ),
)
