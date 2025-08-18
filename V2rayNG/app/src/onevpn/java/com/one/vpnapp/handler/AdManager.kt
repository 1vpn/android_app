package com.one.vpnapp.handler

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.v2ray.ang.BuildConfig


object AdManager {
    private var interstitialAd: InterstitialAd? = null

    fun loadInterstitialAd(context: Context, isPremium: Boolean = false) {
        if (isPremium) return

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            BuildConfig.ADMOB_INTERSTITIAL_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("InterstitialAd", "Ad failed to load: ${adError.message}")
                }
            }
        )
    }

    fun showInterstitialAdAndHandleVpn(
        context: Context,
        onVpnConnect: () -> Unit,
        onVpnCancel: () -> Unit,
        isPremium: Boolean
    ) {
        if (isPremium) {
            onVpnConnect()
            return
        }

        interstitialAd?.let { ad ->
            ad.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        onVpnConnect()
                        interstitialAd = null
                        loadInterstitialAd(context)
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        onVpnCancel()
                    }
                }

            ad.show(context as Activity)
        } ?: run {
            loadInterstitialAd(context)
            onVpnConnect()
        }
    }
}