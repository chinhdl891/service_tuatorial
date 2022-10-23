package com.example.serviec_totuarial

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val actionMusic = intent?.getIntExtra(GlobalApp.SEND_ACTION, 0)
        val intentService = Intent(context, MyService::class.java)
        intentService.putExtra(GlobalApp.ACTION_ACT_TO_SV, actionMusic)
        context.startService(intentService)
    }
}