package com.one.vpnapp

import com.v2ray.ang.AngApplication
import com.v2ray.ang.BuildConfig
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration

class MainApplication : AngApplication() {
    override fun onCreate() {
        super.onCreate()

        Purchases.logLevel = if (BuildConfig.DEBUG) com.revenuecat.purchases.LogLevel.VERBOSE else com.revenuecat.purchases.LogLevel.ERROR

        val cfg = PurchasesConfiguration.Builder(this, BuildConfig.REVENUECAT_API_KEY).build()
        Purchases.configure(cfg)
    }
}
