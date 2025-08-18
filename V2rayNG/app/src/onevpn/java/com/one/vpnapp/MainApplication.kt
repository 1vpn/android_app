package com.one.vpnapp

import com.v2ray.ang.AngApplication
import com.v2ray.ang.BuildConfig
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration

class MainApplication : AngApplication() {
    override fun onCreate() {
        super.onCreate()

        Purchases.logLevel = com.revenuecat.purchases.LogLevel.VERBOSE

        val cfg = PurchasesConfiguration.Builder(this, BuildConfig.GOOGLE_API_KEY).build()
        Purchases.configure(cfg)
    }
}
