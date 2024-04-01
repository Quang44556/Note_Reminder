package com.example.notereminder.notification

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.notereminder.ID_CHANNEL
import com.example.notereminder.R

class NotificationService(private val context: Context) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    fun showBasicNotification(id: Long, title: String, content: String) {
        val notification = NotificationCompat.Builder(context, ID_CHANNEL)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(
            id.toInt(),
            notification
        )
    }
}