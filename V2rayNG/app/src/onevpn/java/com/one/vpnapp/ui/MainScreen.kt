package com.one.vpnapp.ui

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.one.vpnapp.handler.MmkvManager
import com.v2ray.ang.handler.V2RayServiceManager
import com.v2ray.ang.R
import com.one.vpnapp.handler.AdManager
import com.v2ray.ang.fmt.CustomFmt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    requestVpnPermission: (Intent) -> Unit,
    isVpnOnState: androidx.compose.runtime.MutableState<Boolean>,
    navController: NavController
) {
    val context = LocalContext.current

    val userData = MmkvManager.getUserData()
    val isLoggedIn = userData?.sessionAuthToken?.isNotEmpty() == true
    val isPremium = userData?.isPremium == true
    val hasGivenRating = MmkvManager.hasGivenRating()

    var selectedLocation by remember {
        mutableStateOf(
            MmkvManager.getSelectedLocation()
                ?: getAvailableLocations().first()
        )
    }
    var showLocationDialog by remember { mutableStateOf(false) }
    var showMenuDialog by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }

    LaunchedEffect(selectedLocation) {
        MmkvManager.setSelectedLocation(selectedLocation)
        AdManager.loadInterstitialAd(context, isPremium)

        val userData = MmkvManager.getUserData()
        val isPremium = userData?.isPremium == true

        val hardcodedConfig = """
        {
          "log": {
            "loglevel": "warning"
          },
          "inbounds": [
            {
              "listen": "127.0.0.1",
              "port": 10808,
              "protocol": "socks",
              "settings": {
                "udp": true
              },
              "sniffing": {
                "enabled": true,
                "destOverride": ["http", "tls"]
              }
            },
            {
              "listen": "127.0.0.1",
              "port": 10809,
              "protocol": "http",
              "sniffing": {
                "enabled": true,
                "destOverride": ["http", "tls"]
              }
            }
          ],
          "routing": {
            "domainStrategy": "IPIfNonMatch",
            "rules": [
              {
                "type": "field",
                "domain": ["geosite:geolocation-!cn"],
                "outboundTag": "proxy"
              },
              {
                "type": "field",
                "domain": ["geosite:cn"],
                "outboundTag": "direct"
              }
            ]
          },
          "outbounds": [
            {
              "protocol": "vless",
              "tag": "proxy",
              "settings": {
                "vnext": [
                  {
                    "address": "${selectedLocation.xrayHost}",
                    "port": 443,
                    "users": [
                      {
                        "id": "${if (isPremium) userData.uuid else "e493f498-8794-40eb-88d4-3befb950743c"}",
                        ${if (isPremium) "\"flow\": \"xtls-rprx-vision\", " else ""}
                        "encryption": "none"
                      }
                    ]
                  }
                ]
              },
              "streamSettings": {
                "network": "tcp",
                "security": "reality",
                "realitySettings": {
                  "fingerprint": "chrome",
                  "serverName": "www.msu.ru",
                  "publicKey": "${if (isPremium) userData.publicKey else selectedLocation.publicKey}",
                  "shortId": "${if (isPremium) userData.shortId else selectedLocation.shortId}",
                  "spiderX": "${if (isPremium) "" else "/"}"
                }
              }
            },
            {
              "protocol": "freedom",
              "tag": "direct"
            },
            {
              "protocol": "blackhole",
              "tag": "block"
            }
          ]
        }
    """.trimIndent()

        val profileItem = CustomFmt.parse(hardcodedConfig)

        if (profileItem != null) {
            val guid = com.v2ray.ang.handler.MmkvManager.encodeServerConfig("", profileItem)
            com.v2ray.ang.handler.MmkvManager.encodeServerRaw(guid, hardcodedConfig)
            com.v2ray.ang.handler.MmkvManager.setSelectServer(guid)
        }
    }

    TopAppBar(
        title = {},
        navigationIcon = {
            Icon(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                tint = Color.Unspecified,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
            )
        },
        actions = {
            Box(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { showMenuDialog = true }
                    .padding(horizontal = 24.dp, vertical = 4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.menu),
                    contentDescription = "Menu",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )

    VpnToggle(
        startVpn = {
            AdManager.showInterstitialAdAndHandleVpn(
                context,
                onVpnConnect = {
                    V2RayServiceManager.startVServiceFromToggle(context)
                    isVpnOnState.value = true
                },
                onVpnCancel = {
                    isVpnOnState.value = false
                },
                isPremium
            )
        },
        stopVpn = {
            V2RayServiceManager.stopVService(context)
            AdManager.loadInterstitialAd(context, isPremium)
            isVpnOnState.value = false
            showReviewDialog = true
        },
        requestVpnPermission = requestVpnPermission,
        isVpnOnState = isVpnOnState,
    )

    Box(modifier = Modifier.fillMaxSize()) {
        LocationButton(
            modifier = Modifier.align(Alignment.BottomCenter),
            selectedLocation = selectedLocation,
            onClick = { showLocationDialog = true },
            context = context,
        )
    }

    if (showMenuDialog) {
        MenuDialog(
            userData,
            isLoggedIn,
            navController = navController,
            onDismiss = { showMenuDialog = false }
        )
    }

    if (showLocationDialog) {
        LocationDialog(
            locations = getAvailableLocations(),
            onOptionSelected = { country ->
                selectedLocation = getAvailableLocations().first { it.country == country }
                isVpnOnState.value = false
                V2RayServiceManager.stopVService(context)
            },
            onPremiumSelected = { navController.navigate("upgrade") },
            onDismiss = { showLocationDialog = false },
            context = context,
        )
    }

    if (!hasGivenRating && showReviewDialog) {
        ReviewDialog(
            onDismiss = { showReviewDialog = false }
        )
    }
}