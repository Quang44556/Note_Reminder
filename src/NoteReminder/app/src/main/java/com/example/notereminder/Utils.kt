package com.example.notereminder

import com.example.notereminder.ui.screens.TimeReminder
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date

fun convertToTimeReminder(reminderDate: Date?): TimeReminder {
    if (reminderDate == null) {
        return TimeReminder(date = Date(), time = LocalTime.now())
    }
    val localDateTime = LocalDateTime.ofInstant(reminderDate.toInstant(), ZoneId.systemDefault())
    val time = LocalTime.of(localDateTime.hour, localDateTime.minute)
    return TimeReminder(reminderDate, time)
}