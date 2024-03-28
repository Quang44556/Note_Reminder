package com.example.notereminder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.notereminder.ui.theme.NoteReminderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoteReminderTheme {
                NoteReminderApp()
                //CalendarScreen()
            }
        }
    }
}