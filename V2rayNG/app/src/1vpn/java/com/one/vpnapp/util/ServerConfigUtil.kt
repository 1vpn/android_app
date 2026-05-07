package com.one.vpnapp.util

import android.content.Context
import com.one.vpnapp.model.Server
import com.one.vpnapp.model.UserData
import com.v2ray.ang.fmt.CustomFmt
import com.v2ray.ang.handler.MmkvManager

private const val FREE_UUID = "44ae52b9-76fc-444d-8e43-186b4384b80a"
private const val FREE_PUBLIC_KEY = "PryGoq51ilG0eLUPl9i0xCvmk1xpwkyFSr_tG4GNLlU"
private const val FREE_SHORT_ID = "1d86d17709852910"

fun setupServerConfig(
    server: Server,
    userData: UserData?,
    isPremium: Boolean,
    context: Context
) {
    val template = context.assets.open("xray_config_template.json")
        .bufferedReader()
        .use { it.readText() }

    val premiumData = if (isPremium) userData else null
    val uuid = premiumData?.uuid ?: FREE_UUID
    val publicKey = premiumData?.publicKey ?: FREE_PUBLIC_KEY
    val shortId = premiumData?.shortId ?: FREE_SHORT_ID
    val serverName = server.realityServerName ?: "www.cloudflare.com"

    val config = template
        .replace("{{xrayHost}}", server.host)
        .replace("{{uuid}}", uuid)
        .replace("{{publicKey}}", publicKey)
        .replace("{{shortId}}", shortId)
        .replace("{{serverName}}", serverName)

    val profileItem = CustomFmt.parse(config)

    if (profileItem != null) {
        profileItem.remarks = "1VPN"
        val guid = MmkvManager.encodeServerConfig("", profileItem)
        MmkvManager.encodeServerRaw(guid, config)
        MmkvManager.setSelectServer(guid)
    }
}
