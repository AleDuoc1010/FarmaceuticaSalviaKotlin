package com.example.farmaceuticasalvia.data.local.storage

import android.content.Context

class IpStorage(context: Context) {
    private val prefs = context.getSharedPreferences("app_config", Context.MODE_PRIVATE)

    private val DEFAULT_IP = "192.168.100.218"

    fun getBaseIp(): String {
        return prefs.getString("server_ip", DEFAULT_IP) ?: DEFAULT_IP
    }

    fun saveBaseIp(ip: String) {
        prefs.edit().putString("server_ip", ip).apply()
    }
}