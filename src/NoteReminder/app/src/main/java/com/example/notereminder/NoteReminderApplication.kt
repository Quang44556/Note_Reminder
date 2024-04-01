package com.example.notereminder

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.example.notereminder.data.AppContainer
import com.example.notereminder.data.AppDataContainer
import com.example.notereminder.notification.AndroidAlarmScheduler

class NoteReminderApplication : Application() {
    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer
    lateinit var scheduler: AndroidAlarmScheduler

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        scheduler = AndroidAlarmScheduler(this)

        // create notification channel
        val notificationChannel = NotificationChannel(
            ID_CHANNEL,
            NAME_CHANNEL,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.description = DESCRIPTION_CHANNEL
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }
}