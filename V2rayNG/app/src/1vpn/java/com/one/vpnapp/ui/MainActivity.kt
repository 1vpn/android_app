package com.one.vpnapp.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import com.one.vpnapp.api.RetrofitClient
import com.one.vpnapp.handler.MmkvManager
import com.one.vpnapp.model.Location
import com.one.vpnapp.util.VpnServiceUtil
import com.revenuecat.purchases.Purchases
import com.v2ray.ang.AppConfig
import com.v2ray.ang.handler.V2RayServiceManager
import com.v2ray.ang.service.V2RayVpnService
import com.v2ray.ang.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalAnimationApi::class)
class MainActivity : ComponentActivity() {

    private val isVpnOnState = mutableStateOf(false)
    private lateinit var vpnPermissionLauncher: ActivityResultLauncher<Intent>

    private val v2rayStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.getIntExtra("key", 0)) {
                AppConfig.MSG_STATE_RUNNING,
                AppConfig.MSG_STATE_START_SUCCESS -> isVpnOnState.value = true

                AppConfig.MSG_STATE_NOT_RUNNING,
                AppConfig.MSG_STATE_STOP_SUCCESS,
                AppConfig.MSG_STATE_START_FAILURE -> isVpnOnState.value = false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        try {
            val filter = IntentFilter(AppConfig.BROADCAST_ACTION_ACTIVITY)
            ContextCompat.registerReceiver(this, v2rayStateReceiver, filter, Utils.receiverFlags())
        } catch (_: Exception) {
            // no-op
        }
        isVpnOnState.value = VpnServiceUtil.isVpnServiceRunning(this, V2RayVpnService::class.java)
    }

    override fun onStop() {
        super.onStop()
        try {
            unregisterReceiver(v2rayStateReceiver)
        } catch (_: Exception) {
            // no-op
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.statusBarColor = android.graphics.Color.WHITE
        window.navigationBarColor = android.graphics.Color.WHITE
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

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
                colorScheme = lightColorScheme(
                    background = Color.White,
                    surface = Color.White
                ),
                typography = Typography()
            ) {
                val navController = rememberNavController()

                LaunchedEffect(Unit) {
                    val userData = MmkvManager.getUserData()
                    val isPremium = userData?.isPremium == true
                    if (!isPremium) {
                        val lastShown = MmkvManager.getLastUpgradeScreenTime()
                        val now = System.currentTimeMillis()
                        val twelveHoursMillis = 12 * 60 * 60 * 1000L
                        if (lastShown == 0L || now - lastShown >= twelveHoursMillis) {
                            MmkvManager.setLastUpgradeScreenTime(now)
                            navController.navigate("upgrade")
                        }
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
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
                                requestVpnPermission = { intent ->
                                    vpnPermissionLauncher.launch(
                                        intent
                                    )
                                },
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
}

fun getAvailableLocations(): List<Location> {
    val userData = MmkvManager.getUserData()

    return if (userData != null && userData.isPremium && userData.locations.isNotEmpty()) {
        userData.locations.filter { it.xrayHost != null }
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