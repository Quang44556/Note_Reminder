package com.example.notereminder.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.example.notereminder.ID_CHANNEL
import com.example.notereminder.MainActivity
import com.example.notereminder.R
import com.example.notereminder.URI
import com.example.notereminder.ui.screens.HomeDestination
import com.example.notereminder.ui.screens.NoteDetailDestination
import com.example.notereminder.ui.screens.SearchDestination

class NotificationService(private val context: Context) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    fun showBasicNotification(
        id: Long,
        title: String,
        content: String,
    ) {
        // setting to move to composable when click to notification
        val deeplink = "$URI${NoteDetailDestination.route}/${id}"
        val intent = Intent(Intent.ACTION_VIEW, deeplink.toUri(), context, MainActivity::class.java)

        val pendingIntent: PendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(id.toInt(), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        // create notification
        val notification = NotificationCompat.Builder(context, ID_CHANNEL)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(
            id.toInt(),
            notification
        )
    }
}