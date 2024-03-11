package com.example.notereminder

import android.app.Application
import com.example.notereminder.data.AppContainer
import com.example.notereminder.data.AppDataContainer

class NoteReminderApplication : Application() {
    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}