package com.example.serviec_totuarial

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var mMusic = Music()
    private var isPlaying = false
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            val bundle = intent?.extras
            if (bundle != null) {
                mMusic = bundle.getSerializable(GlobalApp.SEND_OBJ) as Music
                isPlaying = bundle.getBoolean(GlobalApp.SEND_STATUS)
                val action = bundle.getInt(GlobalApp.SEND_ACTION)
                handleLayoutMusic(action)
            }
        }

    }

    private fun handleLayoutMusic(action: Int) {
        when (action) {
            MyService.ACTION_CLEAR -> {
                layout_main_bottom.visibility = View.GONE
            }
            MyService.ACTION_PAUSE -> {
                setStatus()
            }
            MyService.ACTION_START -> {
                layout_main_bottom.visibility = View.VISIBLE
                showInfo()
                setStatus()
            }
            MyService.ACTION_RESUME -> {
                setStatus()
            }

        }
    }

    private fun showInfo() {
        if (mMusic == null) {
            return
        } else {
            tv_main_title_single.text = mMusic.single
            tv_main_title_song.text = mMusic.title
            imv_main_song.setImageResource(mMusic.image)
        }
        imv_main_pause_or_play.setOnClickListener {
            if (isPlaying) {
                sendActionToService(MyService.ACTION_PAUSE)
            } else {
                sendActionToService(MyService.ACTION_RESUME)
            }
        }
        imv_main_clear.setOnClickListener {
            sendActionToService(MyService.ACTION_CLEAR)
        }
    }

    private fun setStatus() {
        if (isPlaying) {
            imv_main_pause_or_play.setImageResource(R.drawable.pause)
        } else {
            imv_main_pause_or_play.setImageResource(R.drawable.play)
        }
    }

    private fun sendActionToService(action: Int) {
        val intent = Intent(this, MyService::class.java)
        intent.putExtra(GlobalApp.ACTION_ACT_TO_SV, action)
        startService(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadcastReceiver, IntentFilter(GlobalApp.ACTION_SV_TO_ACT))
        btn_main_start_service.setOnClickListener {
            clickStartService()
        }

        btn_main_stop_service.setOnClickListener {
            clickStopService()
        }
    }

    private fun clickStopService() {
        val intent = Intent(this, MyService::class.java)
        stopService(intent)
    }

    private fun clickStartService() {
        val music = Music("Sugar", "MIT", R.drawable.img_music, R.raw.music)
        val intent = Intent(this, MyService::class.java)
        val bundle = Bundle()
        bundle.putSerializable(GlobalApp.SEND_DATA, music)
        intent.putExtras(bundle)
        startService(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }
}