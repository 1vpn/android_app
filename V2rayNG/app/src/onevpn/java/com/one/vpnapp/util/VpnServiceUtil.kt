package com.one.vpnapp.util

import android.app.ActivityManager
import android.content.Context

object VpnServiceUtil {
    fun isVpnServiceRunning(context: Context, vpnServiceClass: Class<*>): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
            if (service.service.className == vpnServiceClass.name) {
                return true
            }
        }
        return false
    }
}