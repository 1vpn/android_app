package com.one.vpnapp.ui

import android.content.Intent
import android.net.VpnService
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.one.vpnapp.util.VpnConnectionStatus
import com.v2ray.ang.R
import kotlinx.coroutines.delay

@Composable
fun VpnToggle(
    startVpn: () -> Unit,
    stopVpn: () -> Unit,
    requestVpnPermission: (Intent, onGranted: () -> Unit) -> Unit,
    isVpnOnState: MutableState<Boolean>,
    connectionStatus: VpnConnectionStatus,
) {
    val context = LocalContext.current

    // Debounce status text — ignore transient states that resolve in < 300ms
    var displayedStatus by remember { mutableStateOf(connectionStatus) }
    LaunchedEffect(connectionStatus) {
        delay(300)
        displayedStatus = connectionStatus
    }

    Column(
        modifier = Modifier.padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ToggleSwitch(
            connectionStatus = connectionStatus,
            onCheckedChange = { isChecked ->
                if (isChecked) {
                    val intent = VpnService.prepare(context)
                    if (intent == null) {
                        isVpnOnState.value = true
                        startVpn()
                    } else {
                        requestVpnPermission(intent) {
                            isVpnOnState.value = true
                            startVpn()
                        }
                    }
                } else {
                    isVpnOnState.value = false
                    stopVpn()
                }
            }
        )

        Text(
            text = when (displayedStatus) {
                VpnConnectionStatus.DISCONNECTED -> stringResource(R.string.disconnected)
                VpnConnectionStatus.CONNECTING -> stringResource(R.string.connecting)
                VpnConnectionStatus.CONNECTED -> stringResource(R.string.connected)
                VpnConnectionStatus.NO_CONNECTION -> stringResource(R.string.no_connection)
            },
            style = TextStyle(fontSize = 18.sp, color = black),
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun ToggleSwitch(
    connectionStatus: VpnConnectionStatus,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val isConnecting = connectionStatus == VpnConnectionStatus.CONNECTING
    val effectiveChecked = connectionStatus != VpnConnectionStatus.DISCONNECTED
    val thumbSize = 100.dp
    val animDuration = 500

    val thumbOffset by animateDpAsState(
        targetValue = if (effectiveChecked) 100.dp else 0.dp,
        animationSpec = tween(animDuration, easing = FastOutSlowInEasing)
    )

    val targetColor = when {
        !effectiveChecked -> toggleGrey
        connectionStatus == VpnConnectionStatus.NO_CONNECTION -> red
        connectionStatus == VpnConnectionStatus.CONNECTED -> blue
        else -> toggleGrey
    }
    val trackColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(animDuration, easing = FastOutSlowInEasing)
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .width(200.dp)
            .height(thumbSize)
            .clickable(
                indication = null,
                interactionSource = interactionSource,
                onClick = { if (!isConnecting) onCheckedChange(!effectiveChecked) }
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        // Track
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(50))
                .background(color = trackColor)
        )

        // Thumb
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(thumbSize),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                    .clip(CircleShape)
                    .background(white)
            )
        }
    }
}
