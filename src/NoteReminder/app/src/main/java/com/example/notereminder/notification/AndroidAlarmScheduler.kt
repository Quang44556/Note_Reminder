package com.example.notereminder.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.notereminder.ACTION_SCHEDULE_NOTIFICATION
import com.example.notereminder.CONTENT
import com.example.notereminder.NOTE_ID
import com.example.notereminder.TITLE
import com.example.notereminder.data.entities.Note

class AndroidAlarmScheduler(
    private val context: Context
): AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(note: Note) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(TITLE, note.title)
            putExtra(CONTENT, note.content)
            putExtra(NOTE_ID, note.noteId)
        }

        note.reminderDate?.let {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                it.time,
                PendingIntent.getBroadcast(
                    context,
                    note.noteId.toInt(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        }
    }

    override fun cancel(note: Note) {
        val intent = Intent(context, AlarmReceiver::class.java)
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                note.noteId.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}