package com.example.notereminder.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notereminder.DEFAULT_ID
import com.example.notereminder.convertToTimeReminder
import com.example.notereminder.data.NoteWithTags
import com.example.notereminder.data.NotesRepository
import com.example.notereminder.data.entities.Tag
import com.example.notereminder.notification.AndroidAlarmScheduler
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date

class NoteDetailViewMode(
    private val scheduler: AndroidAlarmScheduler,
    savedStateHandle: SavedStateHandle,
    private val notesRepository: NotesRepository,
) : ViewModel() {
    private val noteId: Int = checkNotNull(savedStateHandle[NoteDetailDestination.ITEM_ID_ARG])

    /**
     * Holds current ui state
     */
    var noteDetailUiState: NoteDetailUiState by mutableStateOf(NoteDetailUiState())

    /**
     * Holds deleted tags in current note
     */
    private val deletedTags: MutableList<Tag> = mutableListOf()

    init {
        getNoteWithTags()
    }

    /**
     * get current [NoteWithTags] in database
     */
    private fun getNoteWithTags() {
        viewModelScope.launch {
            noteDetailUiState = notesRepository.getNoteWithTagsStream(noteId)
                .filterNotNull()
                .map {
                    NoteDetailUiState(
                        noteWithTags = it,
                        timeReminder = if (noteDetailUiState.noteWithTags.note.reminderDate != null) {
                            convertToTimeReminder(noteDetailUiState.noteWithTags.note.reminderDate)
                        } else {
                            convertToTimeReminder(it.note.reminderDate)
                        }
                    )
                }
                .first()
        }
    }

    /**
     * update NoteWithTags ui state
     */
    fun updateNoteWithTagsUi(noteWithTags: NoteWithTags) {
        noteDetailUiState = noteDetailUiState.copy(noteWithTags = noteWithTags)
    }

    /**
     * show or hide dialog add tag
     */
    fun updateShowingDialogAddTag() {
        noteDetailUiState =
            noteDetailUiState.copy(isShowingDialogAddTag = !noteDetailUiState.isShowingDialogAddTag)
    }

    /**
     * show or hide dialog delete current note
     */
    fun updateShowingDialogDeleteNote() {
        noteDetailUiState =
            noteDetailUiState.copy(isShowingDialogDeleteNote = !noteDetailUiState.isShowingDialogDeleteNote)
    }

    /**
     * show or hide dialog remind to save note
     */
    fun updateShowingDialogSaveNote() {
        noteDetailUiState =
            noteDetailUiState.copy(isShowingDialogSaveNote = !noteDetailUiState.isShowingDialogSaveNote)
    }

    /**
     * show or hide check icon
     */
    fun updateShowingCheckIcon() {
        noteDetailUiState =
            noteDetailUiState.copy(isShowingCheckIcon = !noteDetailUiState.isShowingCheckIcon)
    }

    /**
     * show or hide dialog set reminder
     */
    fun updateShowingDialogSetReminder() {
        noteDetailUiState =
            noteDetailUiState.copy(isShowingDialogSetReminder = !noteDetailUiState.isShowingDialogSetReminder)
    }

    /**
     * reset time reminder equal to reminder date of current note
     */
    fun resetTimeReminder() {
        noteDetailUiState =
            noteDetailUiState.copy(timeReminder = convertToTimeReminder(noteDetailUiState.noteWithTags.note.reminderDate))
    }

    /**
     * show or hide date picker, time picker and dialog set reminder
     */
    fun updateShowingDatePicker() {
        noteDetailUiState =
            noteDetailUiState.copy(isShowingDatePicker = !noteDetailUiState.isShowingDatePicker)
    }

    /**
     * show or hide date picker, time picker and dialog set reminder
     */
    fun updateShowingTimePicker() {
        noteDetailUiState =
            noteDetailUiState.copy(isShowingTimePicker = !noteDetailUiState.isShowingTimePicker)
    }

    /**
     * update property date in [TimeReminder]
     */
    fun updateTimeReminder(date: Date? = null) {
        noteDetailUiState = noteDetailUiState.copy(
            timeReminder = noteDetailUiState.timeReminder.copy(
                date = date ?: noteDetailUiState.timeReminder.date,
            )
        )
    }

    /**
     * update property time in [TimeReminder]
     */
    fun updateTimeReminder(time: LocalTime? = null) {
        noteDetailUiState = noteDetailUiState.copy(
            timeReminder = noteDetailUiState.timeReminder.copy(
                time = time ?: noteDetailUiState.timeReminder.time
            )
        )
    }

    /**
     * if [NoteWithTags] has not been already in database, insert it to database
     * Otherwise, update it in database
     */
    fun insertOrUpdateNoteWithTags() {
        if (noteDetailUiState.noteWithTags.note.noteId == DEFAULT_ID) {
            insertNoteWithTags()
        } else {
            updateNoteWithTags()
        }
        updateShowingCheckIcon()

        // schedule notification
        scheduleNotification()
    }

    private fun scheduleNotification() {
        val note = noteDetailUiState.noteWithTags.note
        if (note.reminderDate != null) {
            // schedule new notification
            note.let(scheduler::schedule)
        } else {
            // cancel scheduled notification
            note.let(scheduler::cancel)
        }
    }

    /**
     * update property reminderDate in Note in [NoteWithTags]
     * if [delete] = true, reminderDate = null
     */
    fun updateNoteReminderDate(delete: Boolean = false) {
        if (delete) {
            noteDetailUiState.noteWithTags.note.reminderDate = null
        } else {
            val date = noteDetailUiState.timeReminder.date
            val time = noteDetailUiState.timeReminder.time

            val calendar = Calendar.getInstance()
            calendar.time = date

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val localDateTime = LocalDateTime.of(year, month + 1, day, time.hour, time.minute)

            noteDetailUiState.noteWithTags.note.reminderDate =
                Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
        }
    }

    /**
     *insert [NoteWithTags] to database
     */
    private fun insertNoteWithTags() {
        viewModelScope.launch {
            noteDetailUiState.noteWithTags.note.noteId =
                notesRepository.insertNote(noteDetailUiState.noteWithTags.note)
            insertTagsToDatabase()
            deleteTagsInDatabase()
        }
    }

    /**
     *Update [NoteWithTags] to database
     */
    private fun updateNoteWithTags() {
        viewModelScope.launch {
            notesRepository.updateNoteAndTags(noteDetailUiState.noteWithTags)
            insertTagsToDatabase()
            deleteTagsInDatabase()
        }
    }

    /**
     *insert all [Tag] in current [NoteWithTags] to database
     */
    private suspend fun insertTagsToDatabase() {
        noteDetailUiState.noteWithTags.tags.forEach { tag ->
            tag.noteBelongedToId = noteDetailUiState.noteWithTags.note.noteId
            notesRepository.insertTag(tag)
        }
    }

    /**
     *Delete all [Tag] in [deletedTags] from database
     */
    private suspend fun deleteTagsInDatabase() {
        deletedTags.forEach { tag ->
            notesRepository.deleteTag(tag)
        }
    }

    /**
     *Insert a [Tag] to [NoteDetailUiState]
     */
    fun insertTagToUiState(tag: Tag) {
        tag.noteBelongedToId = noteDetailUiState.noteWithTags.note.noteId

        noteDetailUiState.noteWithTags.tags += Tag(
            name = tag.name,
            noteBelongedToId = noteDetailUiState.noteWithTags.note.noteId,
        )
    }

    /**
     *Delete a [Tag] from [NoteDetailUiState]
     */
    fun deleteTagInUiState(tag: Tag) {
        deletedTags.add(tag)

        noteDetailUiState = noteDetailUiState.copy(
            noteWithTags = noteDetailUiState.noteWithTags.copy(
                tags = noteDetailUiState.noteWithTags.tags.toMutableList().apply { remove(tag) },
            )
        )
    }

    /**
     *Delete a [NoteWithTags] from database
     */
    fun deleteNoteWithTags() {
        viewModelScope.launch {
            notesRepository.deleteNoteWithTags(noteDetailUiState.noteWithTags)
        }
    }
}

data class NoteDetailUiState(
    val noteWithTags: NoteWithTags = NoteWithTags(),
    val isShowingDialogAddTag: Boolean = false,
    val isShowingDialogDeleteNote: Boolean = false,
    val isShowingCheckIcon: Boolean = false,
    val isShowingDialogSaveNote: Boolean = false,
    val isShowingDialogSetReminder: Boolean = false,
    val isShowingDatePicker: Boolean = false,
    val isShowingTimePicker: Boolean = false,
    val timeReminder: TimeReminder = TimeReminder()
)

/**
 * object in dialog set reminder
 */
data class TimeReminder(
    val date: Date = Date(),
    val time: LocalTime = LocalTime.now()
) {
    fun compareTo(otherDate: Date): Int {
        val calendar = Calendar.getInstance()
        val calendarOtherDate = Calendar.getInstance()
        calendar.time = date
        calendarOtherDate.time = otherDate

        // compare year
        val yearDiff = calendar.get(Calendar.YEAR) - calendarOtherDate.get(Calendar.YEAR)
        if (yearDiff != 0) {
            return yearDiff
        }

        // compare month
        val monthDiff = calendar.get(Calendar.MONTH) - calendarOtherDate.get(Calendar.MONTH)
        if (monthDiff != 0) {
            return monthDiff
        }

        // compare day
        val dayDiff =
            calendar.get(Calendar.DAY_OF_MONTH) - calendarOtherDate.get(Calendar.DAY_OF_MONTH)
        if (dayDiff != 0) {
            return dayDiff
        }

        // compare hour
        val hourDiff = time.hour - calendarOtherDate.get(Calendar.HOUR_OF_DAY)
        if (hourDiff != 0) {
            return hourDiff
        }

        // compare minute
        return time.minute - calendarOtherDate.get(Calendar.MINUTE)
    }
}