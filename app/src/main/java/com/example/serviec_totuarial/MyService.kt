package com.example.serviec_totuarial

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class MyService : Service() {
    companion object {
        const val ACTION_PAUSE = 1
        const val ACTION_RESUME = 2
        const val ACTION_CLEAR = 3
        const val ACTION_START = 4
    }

    private var mediaPlayer = MediaPlayer()
    private var mMusic = Music()
    private var isPlaying = false
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val bundle = intent?.extras
        if (bundle != null) {
            val music = bundle.getSerializable(GlobalApp.SEND_DATA)
            if (music != null && music is Music) {
                mMusic = music
                startMusic(music)
                sendNotification(music)
                sendActionToActivity(ACTION_START)
            }
        }
        val action = intent?.getIntExtra(GlobalApp.ACTION_ACT_TO_SV, 0)
        if (action != null && action != 0) {
            handleActionMusic(action)
        }

        return START_STICKY
    }

    private fun startMusic(song: Music) {
        mediaPlayer = MediaPlayer.create(applicationContext, song.resource)
        mediaPlayer.start()
        isPlaying = true
    }

    private fun sendNotification(data: Music) {
        val remoteViews = RemoteViews(packageName, R.layout.notifiacation_mp3)
        remoteViews.apply {
            setImageViewResource(R.id.imv_notification_song, data.image)
            setTextViewText(R.id.tv_notification_title_single, data.single)
            setTextViewText(R.id.tv_notification_title_song, data.title)
            setImageViewResource(R.id.imv_notification_pause_or_play, R.drawable.pause)
        }
        if (isPlaying) {
            remoteViews.setImageViewResource(R.id.imv_notification_pause_or_play, R.drawable.pause)
            remoteViews.setOnClickPendingIntent(
                R.id.imv_notification_pause_or_play,
                getPendingIntent(this, ACTION_PAUSE)
            )
        } else {
            remoteViews.setImageViewResource(R.id.imv_notification_pause_or_play, R.drawable.play)
            remoteViews.setOnClickPendingIntent(
                R.id.imv_notification_pause_or_play,
                getPendingIntent(this, ACTION_RESUME)
            )
        }
        remoteViews.setOnClickPendingIntent(
            R.id.imv_notification_clear,
            getPendingIntent(this, ACTION_CLEAR)
        )

        val notification = NotificationCompat.Builder(this, GlobalApp.channelID).apply {
            setContentTitle(getString(R.string.txt_title_notification))
            setCustomContentView(remoteViews)
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setSound(null)
        }
        startForeground(1, notification.build())
    }

    private fun getPendingIntent(context: Context, action: Int): PendingIntent {
        val intent = Intent(this, MyReceiver::class.java)
        intent.putExtra(GlobalApp.SEND_ACTION, action)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getBroadcast(
                context.applicationContext,
                action,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                context.applicationContext,
                action,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    private fun handleActionMusic(action: Int) {
        when (action) {
            ACTION_CLEAR -> {
                stopSelf()
                sendActionToActivity(ACTION_CLEAR)
            }
            ACTION_PAUSE -> {
                pauseMusic()
                sendNotification(mMusic)
                sendActionToActivity(ACTION_PAUSE)
            }
            ACTION_RESUME -> {
                resumeMusic()
                sendNotification(mMusic)
                sendActionToActivity(ACTION_RESUME)
            }
        }
    }

    private fun resumeMusic() {
        if (mediaPlayer != null && !isPlaying) {
            mediaPlayer.start()
            isPlaying = true
        }
    }

    private fun pauseMusic() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause()
            isPlaying = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun sendActionToActivity(action: Int) {
        val intent = Intent(GlobalApp.ACTION_SV_TO_ACT)
        val bundle = Bundle()
        bundle.putSerializable(GlobalApp.SEND_OBJ, mMusic)
        bundle.putBoolean(GlobalApp.SEND_STATUS, isPlaying)
        bundle.putInt(GlobalApp.SEND_ACTION, action)
        intent.putExtras(bundle)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}