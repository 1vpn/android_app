package com.one.vpnapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.one.vpnapp.model.Location
import com.one.vpnapp.util.getFlag
import com.one.vpnapp.util.getCountryName
import com.v2ray.ang.R

@Composable
fun LocationButton(
    modifier: Modifier = Modifier,
    selectedLocation: Location,
    onClick: () -> Unit,
    context: android.content.Context,
) {
    val flagId = getFlag(selectedLocation.countryCode)

    Box(modifier = modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = androidx.compose.ui.graphics.Color(
                    0xFFe7eaed
                )
            ),
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = flagId),
                    contentDescription = "\${selectedLocation.country} Flag",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                Text(
                    text = getCountryName(
                        context,
                        countryCode = selectedLocation.countryCode
                    ),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Normal,
                    color = androidx.compose.ui.graphics.Color(0xFF333333)
                )
                Image(
                    painter = painterResource(id = R.drawable.chevron_right),
                    contentDescription = "Chevron Right",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}