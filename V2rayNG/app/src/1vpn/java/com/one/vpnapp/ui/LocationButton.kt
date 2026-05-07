package com.one.vpnapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.one.vpnapp.model.Location
import com.one.vpnapp.util.getFlag
import com.one.vpnapp.util.getLocationName
import com.v2ray.ang.R

@Composable
fun LocationButton(
    modifier: Modifier = Modifier,
    selectedLocation: Location,
    onClick: () -> Unit,
    context: android.content.Context,
) {
    val flagId = getFlag(selectedLocation.countryCode)

    Box(modifier = modifier) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = white
            ),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, darkBorderGrey),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Image(
                        painter = painterResource(id = flagId),
                        contentDescription = "${selectedLocation.countryCode} Flag",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(6.dp))
                    )
                    Column {
                        Text(
                            text = getLocationName(context, selectedLocation.cityCode),
                            fontSize = 14.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Medium,
                            color = black
                        )
                        Text(
                            text = getLocationName(context, selectedLocation.countryCode),
                            fontSize = 13.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Normal,
                            color = grey
                        )
                    }
                }
                Image(
                    painter = painterResource(id = R.drawable.chevron_right),
                    contentDescription = "Chevron Right",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}