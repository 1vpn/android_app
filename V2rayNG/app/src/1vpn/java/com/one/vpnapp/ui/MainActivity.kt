package com.one.vpnapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.revenuecat.purchases.Purchases
import com.one.vpnapp.api.RetrofitClient
import com.one.vpnapp.model.Location
import com.v2ray.ang.handler.V2RayServiceManager
import com.v2ray.ang.service.V2RayVpnService
import com.one.vpnapp.util.VpnServiceUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.one.vpnapp.handler.MmkvManager
import com.google.android.gms.ads.MobileAds

@OptIn(ExperimentalAnimationApi::class)
class MainActivity : ComponentActivity() {

    private val isVpnOnState = mutableStateOf(false)
    private lateinit var vpnPermissionLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MobileAds.initialize(this)

        vpnPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                isVpnOnState.value = true
                V2RayServiceManager.startVServiceFromToggle(this)
            } else {
                isVpnOnState.value = false
            }
        }

        isVpnOnState.value = VpnServiceUtil.isVpnServiceRunning(this, V2RayVpnService::class.java)

        fetchUserData(this)

        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(),
                typography = Typography()
            ) {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "main",
                    enterTransition = {
                        slideInHorizontally { it }
                    },
                    exitTransition = {
                        slideOutHorizontally { -it }
                    },
                    popEnterTransition = {
                        slideInHorizontally { -it }
                    },
                    popExitTransition = {
                        slideOutHorizontally { it }
                    }
                ) {
                    composable("main") {
                        MainScreen(
                            requestVpnPermission = { intent -> vpnPermissionLauncher.launch(intent) },
                            isVpnOnState = isVpnOnState,
                            navController = navController
                        )
                    }
                    composable("login") {
                        LoginScreen(navController = navController)
                    }
                    composable("signUp") {
                        SignUpScreen(navController = navController)
                    }
                    composable("upgrade") {
                        UpgradeScreen(navController = navController)
                    }
                }
            }
        }
    }
}

fun getAvailableLocations(): List<Location> {
    val userData = MmkvManager.getUserData()

    return if (userData != null && userData.isPremium && userData.locations.isNotEmpty()) {
        userData.locations
    } else {
        freeLocations
    }
}

fun fetchUserData(context: Context) {
    val userData = MmkvManager.getUserData()

    val sessionAuthToken = userData?.sessionAuthToken

    if (sessionAuthToken != null) {
        if (context is LifecycleOwner) {
            context.lifecycleScope.launch {
                try {
                    val headers = mapOf(
                        "Authorization" to "Token $sessionAuthToken"
                    )

                    val response = RetrofitClient.apiService.fetchUserData(headers)

                    if (response.isSuccessful) {
                        val userData = response.body()

                        userData?.let {
                            MmkvManager.setUserData(it)
                        }

                        refreshToken(context)
                    } else {
                        val errorBody = response.errorBody()?.string()
                        println("Error: $errorBody")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}

fun refreshToken(context: Context) {
    if (context is LifecycleOwner) {
        context.lifecycleScope.launch {
            try {
                val userData = MmkvManager.getUserData()
                val sessionAuthToken = userData?.sessionAuthToken

                if (!sessionAuthToken.isNullOrEmpty()) {
                    val response = withContext(Dispatchers.IO) {
                        RetrofitClient.apiService.refreshToken(
                            headers = mapOf(
                                "Content-Type" to "application/json",
                                "Authorization" to "Token $sessionAuthToken"
                            )
                        )
                    }

                    if (response.isSuccessful) {
                        val refreshTokenResponse = response.body()
                        refreshTokenResponse?.sessionAuthToken?.let { newToken ->
                            val updatedUserData = userData.copy(
                                sessionAuthToken = newToken
                            )
                            MmkvManager.setUserData(updatedUserData)
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        println("Error: $errorBody")
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

fun logout(context: Context, loggedOutText: String) {
    MmkvManager.removeUserData()
    MmkvManager.removeSelectedLocation()

    V2RayServiceManager.stopVService(context)
    Purchases.sharedInstance.logOut()
    Toast.makeText(context, loggedOutText, Toast.LENGTH_SHORT).show()

    if (context is MainActivity) {
        context.recreate()
    }
}