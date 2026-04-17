package com.one.vpnapp.ui

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.one.vpnapp.handler.AdManager
import com.one.vpnapp.handler.MmkvManager
import com.one.vpnapp.util.VpnConnectionManager
import com.one.vpnapp.util.VpnConnectionStatus
import com.v2ray.ang.R
import com.v2ray.ang.handler.V2RayServiceManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    requestVpnPermission: (Intent) -> Unit,
    isVpnOnState: androidx.compose.runtime.MutableState<Boolean>,
    navController: NavController
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val userData = remember { MmkvManager.getUserData() }
    val isLoggedIn = remember { userData?.sessionAuthToken?.isNotEmpty() == true }
    val isPremium = remember { userData?.isPremium == true }
    val hasGivenRating = remember { MmkvManager.hasGivenRating() }
    val availableLocations = remember { getAvailableLocations() }

    var selectedLocation by remember {
        mutableStateOf(
            MmkvManager.getSelectedLocation()
                ?: availableLocations.first()
        )
    }
    var connectionStatus by remember { mutableStateOf(VpnConnectionStatus.DISCONNECTED) }
    var connectionJob by remember { mutableStateOf<Job?>(null) }
    var showLocationDialog by remember { mutableStateOf(false) }
    var showMenuDialog by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }

    LaunchedEffect(selectedLocation) {
        MmkvManager.setSelectedLocation(selectedLocation)
        AdManager.loadInterstitialAd(context, isPremium)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {},
            navigationIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .height(28.dp)
                        .padding(horizontal = 24.dp)
                )
            },
            actions = {
                Box(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { showMenuDialog = true }
                        .padding(horizontal = 24.dp, vertical = 12.dp)
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
        HorizontalDivider(color = darkBorderGrey)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            VpnToggle(
                startVpn = {
                    AdManager.showInterstitialAdAndHandleVpn(
                        context,
                        onVpnConnect = {
                            isVpnOnState.value = true
                            connectionStatus = VpnConnectionStatus.CONNECTING
                            connectionJob = coroutineScope.launch {
                                VpnConnectionManager.connectWithFallback(
                                    context = context,
                                    location = selectedLocation,
                                    userData = userData,
                                    isPremium = isPremium,
                                    onConnected = {
                                        connectionStatus = VpnConnectionStatus.CONNECTED
                                    },
                                    onNoConnection = {
                                        connectionStatus = VpnConnectionStatus.NO_CONNECTION
                                    }
                                )
                            }
                        },
                        onVpnCancel = {
                            isVpnOnState.value = false
                            connectionStatus = VpnConnectionStatus.DISCONNECTED
                        },
                        isPremium
                    )
                },
                stopVpn = {
                    connectionJob?.cancel()
                    connectionJob = null
                    V2RayServiceManager.stopVService(context)
                    AdManager.loadInterstitialAd(context, isPremium)
                    isVpnOnState.value = false
                    connectionStatus = VpnConnectionStatus.DISCONNECTED
                    showReviewDialog = true
                },
                requestVpnPermission = requestVpnPermission,
                isVpnOnState = isVpnOnState,
                connectionStatus = connectionStatus,
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            LocationButton(
                selectedLocation = selectedLocation,
                onClick = { showLocationDialog = true },
                context = context,
            )
            if (!isPremium) {
                UpgradeButton(onClick = { navController.navigate("upgrade") })
            }
            BackupDomainBanner()
        }
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
            locations = availableLocations,
            selectedCityCode = selectedLocation.cityCode,
            onOptionSelected = { country ->
                connectionJob?.cancel()
                connectionJob = null
                selectedLocation = availableLocations.first { it.country == country }
                isVpnOnState.value = false
                connectionStatus = VpnConnectionStatus.DISCONNECTED
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