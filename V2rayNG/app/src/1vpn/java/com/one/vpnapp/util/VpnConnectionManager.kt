package com.one.vpnapp.util

import android.content.Context
import com.one.vpnapp.model.Location
import com.one.vpnapp.model.UserData
import com.v2ray.ang.handler.V2RayServiceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

enum class VpnConnectionStatus { DISCONNECTED, CONNECTING, CONNECTED, NO_CONNECTION }

object VpnConnectionManager {

    suspend fun connectWithFallback(
        context: Context,
        location: Location,
        userData: UserData?,
        isPremium: Boolean,
        onConnected: () -> Unit,
        onNoConnection: () -> Unit
    ) {
        val hosts = (location.xrayHosts ?: listOfNotNull(location.xrayHost)).shuffled()

        // Try to find a reachable host
        for (host in hosts) {
            if (!isHostReachable(host)) continue

            try {
                withContext(Dispatchers.IO) {
                    setupServerConfig(location, host, userData, isPremium, context)
                }
                withContext(Dispatchers.Main) {
                    V2RayServiceManager.startVServiceFromToggle(context)
                    onConnected()
                }
                return
            } catch (_: Exception) {
                continue
            }
        }

        // No reachable host found — start VPN anyway with first available host
        val fallbackHost = hosts.firstOrNull()
        if (fallbackHost != null) {
            try {
                withContext(Dispatchers.IO) {
                    setupServerConfig(location, fallbackHost, userData, isPremium, context)
                }
            } catch (_: Exception) { }
        }

        withContext(Dispatchers.Main) {
            V2RayServiceManager.startVServiceFromToggle(context)
            onNoConnection()
        }
    }

    private suspend fun isHostReachable(host: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Socket().use { it.connect(InetSocketAddress(host, 443), 5000) }
            true
        } catch (_: Exception) {
            false
        }
    }
}
