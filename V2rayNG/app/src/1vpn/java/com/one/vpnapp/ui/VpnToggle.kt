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
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.one.vpnapp.util.VpnConnectionStatus
import com.v2ray.ang.R

@Composable
fun VpnToggle(
    startVpn: () -> Unit,
    stopVpn: () -> Unit,
    requestVpnPermission: (Intent) -> Unit,
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
            checked = isVpnOnState.value,
            connectionStatus = connectionStatus,
            onCheckedChange = { isChecked ->
                if (isChecked) {
                    val intent = VpnService.prepare(context)
                    if (intent == null) {
                        isVpnOnState.value = true
                        startVpn()
                    } else {
                        requestVpnPermission(intent)
                        isVpnOnState.value = true
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
                VpnConnectionStatus.CONNECTING -> "Connecting..."
                VpnConnectionStatus.CONNECTED -> stringResource(R.string.connected)
                VpnConnectionStatus.NO_CONNECTION -> "No Connection"
            },
            style = TextStyle(fontSize = 18.sp, color = black),
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun ToggleSwitch(
    checked: Boolean,
    connectionStatus: VpnConnectionStatus,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val isConnecting = connectionStatus == VpnConnectionStatus.CONNECTING
    // Stay visually "on" even if isVpnOnState was reset by the broadcast receiver on failure
    val effectiveChecked = checked || connectionStatus == VpnConnectionStatus.NO_CONNECTION
    val thumbSize = 100.dp
    val animSpecDp = tween<androidx.compose.ui.unit.Dp>(500, easing = FastOutSlowInEasing)

    val thumbOffset by animateDpAsState(
        targetValue = if (effectiveChecked) 100.dp else 0.dp,
        animationSpec = animSpecDp
    )

    val targetColor = when {
        !effectiveChecked -> Color(0xFFc4cbd3)
        connectionStatus == VpnConnectionStatus.NO_CONNECTION -> Color(0xFFE53935)
        else -> Color(0xFF106CD5)
    }
    val trackColor by animateColorAsState(targetValue = targetColor)

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
                    .background(Color.White)
            )
        }
    }
}
