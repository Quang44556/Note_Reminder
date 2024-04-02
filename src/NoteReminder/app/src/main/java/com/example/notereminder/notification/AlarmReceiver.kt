package com.example.notereminder.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.notereminder.CONTENT
import com.example.notereminder.NOTE_ID
import com.example.notereminder.TITLE

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            val id = intent.getLongExtra(NOTE_ID, 0)
            val title = intent.getStringExtra(TITLE) ?: ""
            val content = intent.getStringExtra(CONTENT) ?: ""

            val notificationService = context?.let { NotificationService(it) }
            notificationService?.showBasicNotification(
                id = id,
                title = title,
                content = content
            )
        }
    }
}