package com.example.serviceyt

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.serviceyt.Constant.CHANNEL_ID
import com.example.serviceyt.Constant.MUSIC_NOTIFICATION_ID

class MyService :Service(){
    private lateinit var musicPlayer: MediaPlayer

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        initMusic()
        notificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent?.action=="PLAY_PAUSE"){
            musicPlayer.pause()
        }else{
            musicPlayer.start()
        }
        showNotification()
        return START_STICKY
    }

    private fun showNotification() {
        val notificationIntent = Intent(this,MainActivity::class.java)
        val pendingIntent=PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE
        )
        //show action pause and play in notification
        val playPauseIntent = Intent(this, MyService::class.java)
        playPauseIntent.action = "PLAY_PAUSE"
        val playPausePendingIntent = PendingIntent.getService(
            this,
            0,
            playPauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val icon = if (musicPlayer.isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        val title = if (musicPlayer.isPlaying) "Pause" else "Play"

        val notification = NotificationCompat
            .Builder(this, CHANNEL_ID)
            .setContentText("Music Player")
            .setSmallIcon(R.drawable.music_vector)
            .setContentIntent(pendingIntent)
            .addAction(icon,title,playPausePendingIntent)
            .build()
        startForeground(MUSIC_NOTIFICATION_ID,notification)
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun notificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val serviceChannel=NotificationChannel(
                CHANNEL_ID,"My Service Channel",NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager=getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun initMusic() {
        musicPlayer= MediaPlayer.create(this,R.raw.ring)
        musicPlayer.isLooping=true
        musicPlayer.setVolume(100F,100F)
    }

    override fun onDestroy() {
        super.onDestroy()
        musicPlayer.release()
    }

}