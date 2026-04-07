package com.one.vpnapp.ui

import android.content.Intent
import android.net.VpnService
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.v2ray.ang.R

@Composable
fun VpnToggle(
    startVpn: () -> Unit,
    stopVpn: () -> Unit,
    requestVpnPermission: (Intent) -> Unit,
    isVpnOnState: MutableState<Boolean>,
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ToggleSwitch(
                checked = isVpnOnState.value,
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
                text = if (isVpnOnState.value) stringResource(R.string.connected) else stringResource(
                    R.string.disconnected
                ),
                style = TextStyle(fontSize = 18.sp, color = black),
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
fun ToggleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val trackColor by animateColorAsState(
        targetValue = if (checked) Color(0xFF106CD5) else Color(0xFFc4cbd3)
    )
    val thumbSize = 100.dp
    val trackWidth = 200.dp
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) trackWidth - thumbSize else 0.dp
    )
    val interactionSource = remember { MutableInteractionSource() }
    val thumbPadding = 10.dp

    Box(
        modifier = modifier
            .width(trackWidth)
            .height(100.dp)
            .clickable(
                indication = null,
                interactionSource = interactionSource,
                onClick = { onCheckedChange(!checked) }
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(50))
                .background(color = trackColor)
        )
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(thumbSize),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(thumbPadding)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
    }
}