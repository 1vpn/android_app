package com.one.vpnapp.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import com.one.vpnapp.handler.MmkvManager
import com.v2ray.ang.R

fun openPlayStoreReviewPage(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = "market://details?id=com.one.vpnapp".toUri()
        setPackage("com.android.vending")
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }

    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        val webIntent = Intent(Intent.ACTION_VIEW).apply {
            data = "https://play.google.com/store/apps/details?id=com.one.vpnapp".toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        context.startActivity(webIntent)
    }
}


@Composable
fun ReviewDialog(
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    BaseDialog(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.rate_your_connection),
                fontSize = 18.sp,
                color = black
            )
            Spacer(modifier = Modifier.padding(top = 24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { index ->
                    Image(
                        painter = painterResource(id = R.drawable.star_outline),
                        contentDescription = "Star Icon",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                MmkvManager.setHasGivenRating(true)
                                if (index == 4) {
                                    openPlayStoreReviewPage(context)
                                }
                                onDismiss()
                            }
                    )
                }
            }
        }
    }
}
