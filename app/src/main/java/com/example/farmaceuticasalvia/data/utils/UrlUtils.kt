package com.example.farmaceuticasalvia.data.utils

import android.content.Context
import com.example.farmaceuticasalvia.data.local.storage.IpStorage

fun fixImageUrl(context: Context, url: String): String {
    val ipStorage = IpStorage(context)
    val miIp = ipStorage.getBaseIp()

    return if (url.contains("localhost")) {
        url.replace("localhost", miIp)
    } else if (url.contains("10.0.2.2")) {
        url.replace("10.0.2.2", miIp)
    } else {
        url
    }
}