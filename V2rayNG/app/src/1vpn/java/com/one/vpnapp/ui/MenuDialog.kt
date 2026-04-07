package com.one.vpnapp.ui

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.one.vpnapp.model.UserData
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import com.v2ray.ang.R

@Composable
fun MenuDialog(
    userData: UserData?,
    isLoggedIn: Boolean,
    navController: NavController,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val upgradeText = stringResource(R.string.upgrade)
    val loginText = stringResource(R.string.login)
    val contactUsText = stringResource(R.string.contact_us)
    val logoutText = stringResource(R.string.logout)
    val accountText = stringResource(R.string.account)
    val loggedOutText = stringResource(R.string.logged_out)

    val options = if (isLoggedIn) {
        if (userData?.isPremium == true) {
            listOf(accountText, contactUsText, logoutText)
        } else {
            listOf(upgradeText, accountText, contactUsText, logoutText)
        }
    } else {
        listOf(upgradeText, loginText, contactUsText)
    }

    var showAccountDialog by remember { mutableStateOf(false) }

    BaseDialog(onDismiss = onDismiss) {
        Column(modifier = Modifier.padding(vertical = 12.dp)) {
            options.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            when (option) {
                                upgradeText -> {
                                    navController.navigate("upgrade")
                                    onDismiss()
                                }

                                loginText -> {
                                    navController.navigate("login")
                                    onDismiss()
                                }

                                logoutText -> {
                                    logout(context, loggedOutText)
                                    onDismiss()
                                }

                                contactUsText -> {
                                    val browserIntent = Intent(
                                        Intent.ACTION_VIEW,
                                        "https://1vpn.org/contact_us".toUri()
                                    )
                                    context.startActivity(browserIntent)
                                    onDismiss()
                                }

                                accountText -> {
                                    showAccountDialog = true
                                }
                            }
                        }
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = option,
                        fontSize = 16.sp,
                        color = black
                    )
                }
            }
        }
    }

    if (showAccountDialog) {
        AccountDialog(
            userData = userData,
            onDismiss = { showAccountDialog = false }
        )
    }
}

@Composable
fun AccountDialog(
    userData: UserData?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val options = listOf(
        stringResource(R.string.email) to (if (userData?.email.isNullOrEmpty()) stringResource(R.string.no_email) else userData.email),
        stringResource(R.string.plan) to (if (userData?.isPremium == true) stringResource(R.string.premium) else stringResource(
            R.string.free
        ))
    )

    BaseDialog(onDismiss = onDismiss) {
        Column(modifier = Modifier.padding(vertical = 12.dp)) {
            options.forEach { (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        fontSize = 16.sp,
                        color = black,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = value,
                        fontSize = 16.sp,
                        color = black.copy(alpha = 0.6f),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val browserIntent = Intent(
                            Intent.ACTION_VIEW,
                            "https://1vpn.org/account".toUri()
                        )
                        context.startActivity(browserIntent)
                    }
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.edit_info),
                    fontSize = 16.sp,
                    color = black
                )
            }
        }
    }
}