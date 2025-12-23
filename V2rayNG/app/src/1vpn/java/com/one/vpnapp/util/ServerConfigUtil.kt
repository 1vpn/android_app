package com.one.vpnapp.util

import com.v2ray.ang.handler.MmkvManager
import com.v2ray.ang.fmt.CustomFmt
import com.one.vpnapp.model.Location
import com.one.vpnapp.model.UserData

fun setupServerConfig(selectedLocation: Location, userData: UserData?, isPremium: Boolean) {
    val config = """
        {
          "log": {
            "loglevel": "warning"
          },
          "inbounds": [
            {
              "listen": "127.0.0.1",
              "port": 10808,
              "protocol": "socks",
              "settings": {
                "udp": true
              },
              "sniffing": {
                "enabled": true,
                "destOverride": ["http", "tls"]
              }
            },
            {
              "listen": "127.0.0.1",
              "port": 10809,
              "protocol": "http",
              "sniffing": {
                "enabled": true,
                "destOverride": ["http", "tls"]
              }
            }
          ],
          "routing": {
            "domainStrategy": "IPIfNonMatch",
            "rules": [
              {
                "type": "field",
                "domain": ["geosite:geolocation-!cn"],
                "outboundTag": "proxy"
              },
              {
                "type": "field",
                "domain": ["geosite:cn"],
                "outboundTag": "direct"
              }
            ]
          },
          "outbounds": [
            {
              "protocol": "vless",
              "tag": "proxy",
              "settings": {
                "vnext": [
                  {
                    "address": "${selectedLocation.xrayHost}",
                    "port": 443,
                    "users": [
                      {
                        "id": "${if (isPremium) userData?.uuid else "44ae52b9-76fc-444d-8e43-186b4384b80a"}",
                        "flow": "xtls-rprx-vision",
                        "encryption": "none"
                      }
                    ]
                  }
                ]
              },
              "streamSettings": {
                "network": "tcp",
                "security": "reality",
                "realitySettings": {
                  "fingerprint": "chrome",
                  "serverName": "www.cloudflare.com",
                  "publicKey": "${if (isPremium) userData?.publicKey else selectedLocation.publicKey}",
                  "shortId": "${if (isPremium) userData?.shortId else selectedLocation.shortId}",
                  "spiderX": ""
                }
              }
            },
            {
              "protocol": "freedom",
              "tag": "direct"
            },
            {
              "protocol": "blackhole",
              "tag": "block"
            }
          ]
        }
    """.trimIndent()

    val profileItem = CustomFmt.parse(config)

    if (profileItem != null) {
        profileItem.remarks = "1VPN"
        val guid = MmkvManager.encodeServerConfig("", profileItem)
        MmkvManager.encodeServerRaw(guid, config)
        MmkvManager.setSelectServer(guid)
    }
}