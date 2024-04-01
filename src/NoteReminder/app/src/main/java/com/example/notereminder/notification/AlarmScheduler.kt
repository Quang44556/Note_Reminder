package com.example.notereminder.notification

import com.example.notereminder.data.entities.Note

interface AlarmScheduler {
    fun schedule(note: Note)
    fun cancel(note: Note)
}