package com.one.vpnapp.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.one.vpnapp.api.RetrofitClient
import com.v2ray.ang.R

@Composable
fun BackupDomainBanner() {
    val backupDomain by RetrofitClient.usingBackupDomain
    if (backupDomain != null) {
        val annotatedText = buildAnnotatedString {
            append(stringResource(R.string.backup_domain_prefix) + " ")
            pushLink(androidx.compose.ui.text.LinkAnnotation.Url("https://$backupDomain"))
            withStyle(SpanStyle(color = blue, textDecoration = TextDecoration.Underline)) {
                append(backupDomain!!)
            }
            pop()
            append(" " + stringResource(R.string.backup_domain_suffix))
        }
        Text(
            text = annotatedText,
            fontSize = 12.sp,
            color = grey,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, borderGrey, RoundedCornerShape(8.dp))
                .padding(vertical = 16.dp, horizontal = 16.dp)
        )
    }
}
