package com.one.vpnapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.one.vpnapp.util.getFlag
import com.one.vpnapp.util.getLocationName
import com.one.vpnapp.model.Location
import com.v2ray.ang.R

@Composable
fun LocationDialog(
    locations: List<Location>,
    selectedCityCode: String?,
    onOptionSelected: (Location) -> Unit,
    onPremiumSelected: () -> Unit,
    onDismiss: () -> Unit,
    context: android.content.Context,
) {
    val scrollState = rememberScrollState()

    BaseDialog(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(top = 2.dp)
        ) {
            locations.forEach { location ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (location.isPremium) {
                                onPremiumSelected()
                            } else {
                                onOptionSelected(location)
                            }
                            onDismiss()
                        }
                        .padding(horizontal = 24.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val flagId = getFlag(location.countryCode)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        Image(
                            painter = painterResource(id = flagId),
                            contentDescription = "${location.countryCode} Flag",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                        Column {
                            Text(
                                text = getLocationName(context, location.cityCode),
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Medium,
                                color = black
                            )
                            Text(
                                text = getLocationName(context, location.countryCode),
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Normal,
                                color = grey
                            )
                        }
                    }
                    if (location.isPremium) {
                        Image(
                            painter = painterResource(id = R.drawable.star),
                            contentDescription = "Star Icon",
                            modifier = Modifier.size(20.dp)
                        )
                    } else if (location.cityCode == selectedCityCode) {
                        Image(
                            painter = painterResource(id = R.drawable.check),
                            contentDescription = "Selected",
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.chevron_right),
                            contentDescription = "Chevron Right",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                HorizontalDivider(color = borderGrey)
            }
        }
    }
}