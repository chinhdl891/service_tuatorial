package com.example.serviec_totuarial

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class GlobalApp : Application() {
    companion object {
        const val channelID = "channelService"
        const val SEND_DATA = "send_data"
        const val SEND_OBJ = "obj_music"
        const val SEND_STATUS = "send_status"
        const val SEND_ACTION = "action_music"
        const val ACTION_SV_TO_ACT = "send_data_to_activity"
        const val ACTION_ACT_TO_SV = "action"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelID,
                "channel service",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.setSound(null, null)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }
}