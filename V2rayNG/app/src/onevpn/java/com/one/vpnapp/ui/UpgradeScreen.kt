package com.one.vpnapp.ui

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.Offerings
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.interfaces.PurchaseCallback
import com.one.vpnapp.handler.MmkvManager
import com.v2ray.ang.R

@Composable
fun UpgradeScreen(navController: NavController) {
    val context = LocalContext.current
    val activity = context as? Activity
    var selectedPlan by remember { mutableStateOf<String?>(null) }
    var availablePackages by remember {
        mutableStateOf<List<com.revenuecat.purchases.Package>>(
            emptyList()
        )
    }

    LaunchedEffect(Unit) {
        Purchases.sharedInstance.getOfferings(object :
            com.revenuecat.purchases.interfaces.ReceiveOfferingsCallback {
            override fun onReceived(offerings: Offerings) {
                val packages = offerings.current?.availablePackages ?: emptyList()
                availablePackages = packages
                selectedPlan =
                    packages.firstOrNull { it.packageType == com.revenuecat.purchases.PackageType.ANNUAL }?.identifier
            }

            override fun onError(error: PurchasesError) {
                Log.e(
                    "PackageInfo",
                    "Error fetching packages: Code ${error.code}, Message: ${error.message}"
                )
            }
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .imePadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = stringResource(R.string.upgrade),
            fontSize = 26.sp,
            modifier = Modifier.padding(top = 24.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.width(IntrinsicSize.Max),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.Start
            ) {
                FeatureRow(text = stringResource(R.string.unlock_all_locations))
                FeatureRow(text = stringResource(R.string.no_advertisements))
                FeatureRow(text = stringResource(R.string.faster_server_speeds))
                FeatureRow(text = stringResource(R.string.bittorrent_support))
            }
        }

        availablePackages
            .sortedByDescending { it.packageType == com.revenuecat.purchases.PackageType.ANNUAL }
            .forEach { pkg ->
                val product = pkg.product
                val isYearly = pkg.packageType == com.revenuecat.purchases.PackageType.ANNUAL

                val monthlyPrice = if (isYearly) {
                    product.price.amountMicros / 12 / 1_000_000.0
                } else {
                    product.price.amountMicros / 1_000_000.0
                }

                val price = if (isYearly) {
                    "${"%.2f".format(monthlyPrice)} ${product.price.currencyCode}  / ${
                        stringResource(
                            R.string.month_short
                        )
                    }"
                } else {
                    "${"%.2f".format(product.price.amountMicros / 1_000_000.0)} ${product.price.currencyCode} / ${
                        stringResource(
                            R.string.month_short
                        )
                    }"
                }

                PlanOption(
                    planName = if (isYearly) stringResource(R.string.yearly_plan) else stringResource(
                        R.string.monthly_plan
                    ),
                    price,
                    discountText = if (isYearly) stringResource(R.string.save_60_percent) else null,
                    isSelected = selectedPlan == pkg.identifier,
                    onClick = { selectedPlan = pkg.identifier }
                )
            }

        Button(
            onClick = {
                if (activity == null) {
                    return@Button
                }

                val selectedPkg = availablePackages.firstOrNull { it.identifier == selectedPlan }
                if (selectedPkg != null) {
                    Purchases.sharedInstance.purchasePackage(
                        activity = activity,
                        packageToPurchase = selectedPkg,
                        listener = object : PurchaseCallback {
                            override fun onCompleted(
                                storeTransaction: com.revenuecat.purchases.models.StoreTransaction,
                                customerInfo: CustomerInfo
                            ) {
                                val userData = MmkvManager.getUserData()

                                val sessionAuthToken = userData?.sessionAuthToken

                                if (sessionAuthToken.isNullOrEmpty()) {
                                    navController.navigate("signUp")

                                } else {
                                    val updatedUserData = userData.copy(isPremium = true)
                                    MmkvManager.setUserData(updatedUserData)
                                    fetchUserData(context)
                                    navController.navigate("main")
                                }

                            }

                            override fun onError(error: PurchasesError, userCancelled: Boolean) {
                                if (userCancelled) {
                                    Log.d("PurchaseInfo", "User cancelled the purchase")
                                } else {
                                    Log.e("PurchaseInfo", "Error: ${error.message}")
                                }
                            }
                        }
                    )
                } else {
                    Log.e("PurchaseError", "No package selected")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF106CD5)),
            shape = RoundedCornerShape(6.dp),
            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = stringResource(R.string.upgrade),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White,
            )
        }

        Text(
            text = stringResource(R.string.continue_with_limited_plan),
            color = Color(0xFF333333),
            modifier = Modifier
                .padding(bottom = 24.dp)
                .clickable {
                    navController.navigate("main")
                }
        )
    }
}

@Composable
fun FeatureRow(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.check),
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            color = Color(0xFF333333),
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@Composable
fun PlanOption(
    planName: String,
    price: String,
    discountText: String? = null,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) Color(0xFF106CD5) else Color(0xFFc4cbd3)
    val borderWidth = if (isSelected) 2.dp else 1.dp

    Box(
        modifier = Modifier
            .clickable(
                onClick = { onClick() },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .background(Color.Transparent, RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = planName,
                    color = Color(0xFF333333),
                )
                Text(
                    text = price,
                    color = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            discountText?.let {
                Box(
                    modifier = Modifier
                        .background(Color(0xFF106CD5), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = it,
                        color = Color.White,
                        fontSize = 14.sp,
                    )
                }
            }
        }
    }
}