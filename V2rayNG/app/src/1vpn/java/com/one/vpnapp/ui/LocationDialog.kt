package com.one.vpnapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.one.vpnapp.util.getFlag
import com.one.vpnapp.util.getCountryName
import com.one.vpnapp.model.Location
import com.v2ray.ang.R

@Composable
fun LocationDialog(
    locations: List<Location>,
    onOptionSelected: (String) -> Unit,
    onPremiumSelected: () -> Unit,
    onDismiss: () -> Unit,
    context: android.content.Context,
) {
    val scrollState = rememberScrollState()

    BaseDialog(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(vertical = 18.dp)
        ) {
            locations.forEach { location ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (location.isPremium) {
                                onPremiumSelected()
                            } else {
                                onOptionSelected(location.country)
                            }
                            onDismiss()
                        }
                        .padding(horizontal = 24.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val flagId = getFlag(location.countryCode)
                    Image(
                        painter = painterResource(id = flagId),
                        contentDescription = "\${location.country} Flag",
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                    Text(
                        text = getCountryName(context, countryCode = location.countryCode),
                        fontSize = 18.sp,
                        color = Color(0xFF333333)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (location.isPremium) {
                        Image(
                            painter = painterResource(id = R.drawable.star),
                            contentDescription = "Star Icon",
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}