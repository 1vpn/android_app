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
import com.one.vpnapp.util.setupServerConfig
import com.v2ray.ang.R
import com.v2ray.ang.handler.V2RayServiceManager


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
        setupServerConfig(selectedLocation, userData, isPremium)
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
            locations = getAvailableLocations(),
            selectedCityCode = selectedLocation.cityCode,
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