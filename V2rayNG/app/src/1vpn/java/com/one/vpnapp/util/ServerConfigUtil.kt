package com.one.vpnapp.util

import android.content.Context
import com.one.vpnapp.model.Location
import com.one.vpnapp.model.UserData
import com.v2ray.ang.fmt.CustomFmt
import com.v2ray.ang.handler.MmkvManager

private const val FREE_UUID = "44ae52b9-76fc-444d-8e43-186b4384b80a"

fun setupServerConfig(
    selectedLocation: Location,
    userData: UserData?,
    isPremium: Boolean,
    context: Context
) {
    val template = context.assets.open("xray_config_template.json")
        .bufferedReader()
        .use { it.readText() }

    val uuid = if (isPremium) userData?.uuid ?: FREE_UUID else FREE_UUID
    val publicKey = if (isPremium) userData?.publicKey
        ?: selectedLocation.publicKey else selectedLocation.publicKey
    val shortId =
        if (isPremium) userData?.shortId ?: selectedLocation.shortId else selectedLocation.shortId

    val config = template
        .replace("{{xrayHost}}", selectedLocation.xrayHost ?: "")
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
