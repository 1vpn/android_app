package com.one.vpnapp.util

import android.content.Context
import com.one.vpnapp.model.Location
import com.one.vpnapp.model.UserData
import com.v2ray.ang.fmt.CustomFmt
import com.v2ray.ang.handler.MmkvManager

private const val FREE_UUID = "44ae52b9-76fc-444d-8e43-186b4384b80a"

fun setupServerConfig(
    selectedLocation: Location,
    host: String,
    userData: UserData?,
    isPremium: Boolean,
    context: Context
) {
    val template = context.assets.open("xray_config_template.json")
        .bufferedReader()
        .use { it.readText() }

    val premiumData = if (isPremium) userData else null
    val uuid = premiumData?.uuid ?: FREE_UUID
    val publicKey = premiumData?.publicKey ?: selectedLocation.publicKey
    val shortId = premiumData?.shortId ?: selectedLocation.shortId

    val config = template
        .replace("{{xrayHost}}", host)
        .replace("{{uuid}}", uuid)
        .replace("{{publicKey}}", publicKey ?: "")
        .replace("{{shortId}}", shortId ?: "")

    val profileItem = CustomFmt.parse(config)

    if (profileItem != null) {
        profileItem.remarks = "1VPN"
        val guid = MmkvManager.encodeServerConfig("", profileItem)
        MmkvManager.encodeServerRaw(guid, config)
        MmkvManager.setSelectServer(guid)
    }
}
