package com.one.vpnapp.ui

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.one.vpnapp.handler.MmkvManager
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Offerings
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.interfaces.PurchaseCallback
import com.v2ray.ang.R

@Composable
fun UpgradeScreen(navController: NavController) {
    val context = LocalContext.current
    val activity = context as? Activity
    var selectedPlan by remember { mutableStateOf<String?>(null) }
    var availablePackages by remember {
        mutableStateOf<List<com.revenuecat.purchases.Package>>(emptyList())
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
            .imePadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Blue header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(blue)
                .padding(horizontal = 24.dp)
                .padding(top = 36.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.star_outline),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = stringResource(R.string.choose_your_plan),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }

        // White bottom section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Features list
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                FeatureRow(
                    text = stringResource(R.string.unlock_all_locations),
                    icon = R.drawable.globe
                )
                FeatureRow(
                    text = stringResource(R.string.no_advertisements),
                    icon = R.drawable.block
                )
                FeatureRow(
                    text = stringResource(R.string.faster_server_speeds),
                    icon = R.drawable.lightning
                )
                FeatureRow(
                    text = stringResource(R.string.bittorrent_support),
                    icon = R.drawable.magnet
                )
                FeatureRow(
                    text = stringResource(R.string.money_back_guarantee),
                    icon = R.drawable.blue_check
                )
            }

            // Plan options — stacked
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                availablePackages
                    .sortedByDescending { it.packageType == com.revenuecat.purchases.PackageType.ANNUAL }
                    .forEach { pkg ->
                        val product = pkg.product
                        val isYearly =
                            pkg.packageType == com.revenuecat.purchases.PackageType.ANNUAL

                        val monthlyPrice = if (isYearly) {
                            product.price.amountMicros / 12 / 1_000_000.0
                        } else {
                            product.price.amountMicros / 1_000_000.0
                        }

                        val price = if (isYearly) {
                            "${"%.2f".format(monthlyPrice)} ${product.price.currencyCode} / ${
                                stringResource(R.string.month_short)
                            }"
                        } else {
                            "${"%.2f".format(product.price.amountMicros / 1_000_000.0)} ${product.price.currencyCode} / ${
                                stringResource(R.string.month_short)
                            }"
                        }

                        val currency = product.price.currencyCode
                        val rawOriginal =
                            (if (isYearly) monthlyPrice else product.price.amountMicros / 1_000_000.0) / 0.67
                        val roundedOriginal = kotlin.math.round(rawOriginal * 10) / 10.0
                        val originalPrice = "${"%.2f".format(roundedOriginal)} $currency"

                        PlanOption(
                            planName = if (isYearly) stringResource(R.string.yearly_plan) else stringResource(
                                R.string.monthly_plan
                            ),
                            price = price,
                            originalPrice = originalPrice,
                            discountText = if (isYearly) "33% Sale + 50% Yearly" else "33% Sale",
                            isSelected = selectedPlan == pkg.identifier,
                            onClick = { selectedPlan = pkg.identifier }
                        )
                    }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        if (activity == null) return@Button

                        val selectedPkg =
                            availablePackages.firstOrNull { it.identifier == selectedPlan }
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

                                    override fun onError(
                                        error: PurchasesError,
                                        userCancelled: Boolean
                                    ) {
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
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = blue),
                    shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = stringResource(R.string.upgrade),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                    )
                }

                Button(
                    onClick = { navController.navigate("main") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, darkBorderGrey),
                    elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = stringResource(R.string.continue_free),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = black,
                    )
                }
            }
        }
    }
}

@Composable
fun FeatureRow(text: String, icon: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(blue10, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            text = text,
            fontSize = 14.sp,
            color = black,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@Composable
fun PlanOption(
    planName: String,
    price: String,
    originalPrice: String? = null,
    discountText: String? = null,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) blue else borderGrey
    val bgColor = if (isSelected) blue10 else Color.White

    Box(
        modifier = Modifier
            .clickable(
                onClick = { onClick() },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .background(bgColor, RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Row 1: radio + plan name + badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .border(
                                width = if (isSelected) 5.dp else 1.5.dp,
                                color = if (isSelected) blue else borderGrey,
                                shape = CircleShape
                            )
                    )
                    Text(
                        text = planName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = black,
                    )
                }
                discountText?.let {
                    Box(
                        modifier = Modifier
                            .background(blue, shape = RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = it,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }

            // Row 2: crossed-out original + current price
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (originalPrice != null) {
                    Text(
                        text = originalPrice,
                        fontSize = 11.sp,
                        color = Color.Red,
                        textDecoration = TextDecoration.LineThrough,
                    )
                }
                Text(
                    text = price,
                    fontSize = 12.sp,
                    color = grey,
                )
            }
        }
    }
}
