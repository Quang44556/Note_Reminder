package com.example.notereminder.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notereminder.R
import com.example.notereminder.data.NoteWithTags
import com.example.notereminder.data.entities.Tag
import com.example.notereminder.ui.AppViewModelProvider
import com.example.notereminder.ui.navigation.NavigationDestination
import java.text.DateFormat
import java.time.LocalTime
import java.util.Calendar
import java.util.Date

object NoteDetailDestination : NavigationDestination {
    override val route = "note_detail"
    const val ITEM_ID_ARG = "itemId"
    val routeWithArgs = "$route/{$ITEM_ID_ARG}"
}

@Composable
fun NoteDetailScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NoteDetailViewMode = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState = viewModel.noteDetailUiState

    // dialog to add tag
    if (uiState.isShowingDialogAddTag) {
        DialogAddTag(
            onDismiss = viewModel::updateShowingDialogAddTag,
            onConfirm = {
                viewModel.insertTagToUiState(it)
                viewModel.updateShowingDialogAddTag()
                if (!uiState.isShowingCheckIcon) {
                    viewModel.updateShowingCheckIcon()
                }
            }
        )
    }

    // dialog remind to save note
    if (uiState.isShowingDialogSaveNote) {
        DialogRemind(
            textRes = R.string.title_dialog_save_note,
            onDismiss = {
                viewModel.updateShowingDialogSaveNote()
                navigateBack()
            },
            onConfirm = {
                viewModel.updateShowingDialogSaveNote()
                viewModel.insertOrUpdateNoteWithTags()
                navigateBack()
            }
        )
    }

    // if user choose delete current note, show dialog to ask again
    if (uiState.isShowingDialogDeleteNote) {
        DialogRemind(
            textRes = R.string.title_dialog_delete_note,
            onDismiss = viewModel::updateShowingDialogDeleteNote,
            onConfirm = {
                viewModel.deleteNoteWithTags()
                viewModel.updateShowingDialogDeleteNote()
                navigateBack()
            },
        )
    }

    // dialog set reminder
    if (uiState.isShowingDialogSetReminder) {
        DialogSetReminder(
            timeReminder = uiState.timeReminder,
            onConfirm = {
                viewModel.updateNoteReminderDate()
                viewModel.updateShowingDialogSetReminder()
                if (!uiState.isShowingCheckIcon) {
                    viewModel.updateShowingCheckIcon()
                }
            },
            onDismiss = {
                viewModel.resetTimeReminder()
                viewModel.updateShowingDialogSetReminder()
            },
            onChooseDatePicker = {
                viewModel.updateShowingDialogSetReminder()
                viewModel.updateShowingDatePicker()
            },
            onChooseTimePicker = {
                viewModel.updateShowingDialogSetReminder()
                viewModel.updateShowingTimePicker()
            },
            showDeleteText = uiState.noteWithTags.note.reminderDate != null,
            onDelete = {
                viewModel.updateNoteReminderDate(delete = true)
                viewModel.updateShowingDialogSetReminder()
                if (!uiState.isShowingCheckIcon) {
                    viewModel.updateShowingCheckIcon()
                }
            }
        )
    }

    // date picker
    if (uiState.isShowingDatePicker) {
        MyDatePicker(
            onConfirm = {
                viewModel.updateTimeReminder(it)
                viewModel.updateShowingDialogSetReminder()
                viewModel.updateShowingDatePicker()
            },
            onDismiss = {
                viewModel.updateShowingDialogSetReminder()
                viewModel.updateShowingDatePicker()
            },
        )
    }

    // time picker
    if (uiState.isShowingTimePicker) {
        MyTimePicker(
            onConfirm = {
                viewModel.updateTimeReminder(it)
                viewModel.updateShowingDialogSetReminder()
                viewModel.updateShowingTimePicker()
            },
            onDismiss = {
                viewModel.updateShowingDialogSetReminder()
                viewModel.updateShowingTimePicker()
            }
        )
    }

    // user press back in phone
    BackHandler(enabled = true) {
        handleBackPress(
            uiState = uiState,
            navigateBack = navigateBack,
            deleteNote = viewModel::deleteNoteWithTags,
            updateShowingDialogSaveNote = viewModel::updateShowingDialogSaveNote
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            NoteDetailTopAppBar(
                uiState = uiState,
                navigateUp = {
                    handleBackPress(
                        uiState = uiState,
                        navigateBack = navigateBack,
                        deleteNote = viewModel::deleteNoteWithTags,
                        updateShowingDialogSaveNote = viewModel::updateShowingDialogSaveNote
                    )
                },
                onBookMarkClicked = {
                    viewModel.updateNoteWithTagsUi(it)
                    if (!uiState.isShowingCheckIcon) {
                        viewModel.updateShowingCheckIcon()
                    }
                },
                onAddTagClicked = viewModel::updateShowingDialogAddTag,
                onDeleteClicked = viewModel::updateShowingDialogDeleteNote,
                onSaveClicked = {
                    viewModel.insertOrUpdateNoteWithTags()
                },
                onSetReminderClicked = viewModel::updateShowingDialogSetReminder
            )
        },
    ) { innerPadding ->
        NoteDetailBody(
            noteWithTags = uiState.noteWithTags,
            onTextFieldChange = {
                viewModel.updateNoteWithTagsUi(it)
                if (!uiState.isShowingCheckIcon) {
                    viewModel.updateShowingCheckIcon()
                }
            },
            modifier = Modifier.padding(innerPadding),
            onClearTagClicked = {
                viewModel.deleteTagInUiState(it)
                if (!uiState.isShowingCheckIcon) {
                    viewModel.updateShowingCheckIcon()
                }
            }
        )
    }
}

/**
 * handle when user click back press or back icon in toolbar
 */
private fun handleBackPress(
    uiState: NoteDetailUiState,
    navigateBack: () -> Unit,
    deleteNote: () -> Unit,
    updateShowingDialogSaveNote: () -> Unit,
) {
    // delete note and navigate back if title and content of note is empty
    if (uiState.noteWithTags.note.title == "" && uiState.noteWithTags.note.content == "") {
        deleteNote()
        navigateBack()
    } else {
        // if user has not already saved note, show dialog remind to save not
        // otherwise, navigate back
        if (uiState.isShowingCheckIcon) {
            updateShowingDialogSaveNote()
        } else {
            navigateBack()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailTopAppBar(
    onBookMarkClicked: (NoteWithTags) -> Unit,
    onSetReminderClicked: () -> Unit,
    onAddTagClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onSaveClicked: () -> Unit,
    uiState: NoteDetailUiState,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                stringResource(id = R.string.note_detail_title),
                style = MaterialTheme.typography.displayLarge
            )
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.description_back_button)
                )
            }
        },
        actions = {
            BookMarkIcon(
                noteWithTags = uiState.noteWithTags,
                onBookMarkClicked = onBookMarkClicked,
            )
            IconButton(onClick = onAddTagClicked) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.description_add_tag_icon)
                )
            }
            IconButton(onClick = onSetReminderClicked) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Search"
                )
            }
            IconButton(onClick = onDeleteClicked) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(id = R.string.description_delete_note_icon)
                )
            }
            if (uiState.isShowingCheckIcon) {
                IconButton(onClick = onSaveClicked) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(id = R.string.description_save_icon),
                    )
                }
            }
        }
    )
}

@Composable
fun NoteDetailBody(
    noteWithTags: NoteWithTags,
    onTextFieldChange: (NoteWithTags) -> Unit,
    onClearTagClicked: (Tag) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        TagsListInNote(
            tags = noteWithTags.tags,
            modifier = Modifier.padding(5.dp),
            onClearTagClicked = onClearTagClicked,
        )
        OutlinedTextField(
            value = noteWithTags.note.title,
            modifier = Modifier
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
            ),
            onValueChange = {
                onTextFieldChange(
                    noteWithTags.copy(
                        note = noteWithTags.note.copy(
                            title = it
                        )
                    )
                )
            },
            textStyle = MaterialTheme.typography.titleLarge,
            placeholder = { Text(stringResource(id = R.string.hintTitle)) },
        )
        OutlinedTextField(
            value = noteWithTags.note.content,
            modifier = Modifier
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
            ),
            placeholder = { Text(stringResource(id = R.string.hintContent)) },
            onValueChange = {
                onTextFieldChange(
                    noteWithTags.copy(
                        note = noteWithTags.note.copy(
                            content = it
                        )
                    )
                )
            })
    }
}

@Composable
fun DialogAddTag(
    onDismiss: () -> Unit,
    onConfirm: (Tag) -> Unit
) {
    var text by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.add_tag_title),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(Tag(name = text))
                }
            ) {
                Text(text = stringResource(id = R.string.dialog_ok))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(id = R.string.dialog_cancel))
            }
        }
    )
}

@Composable
fun DialogRemind(
    textRes: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Text(text = stringResource(id = textRes))
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(stringResource(id = R.string.dialog_yes))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(id = R.string.dialog_no))
            }
        }
    )
}

@Composable
fun DialogSetReminder(
    showDeleteText: Boolean,
    timeReminder: TimeReminder,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onChooseDatePicker: () -> Unit,
    onChooseTimePicker: () -> Unit,
) {
    // format date
    val df = DateFormat.getDateInstance(DateFormat.DEFAULT)
    val formattedSelectedDate = df.format(timeReminder.date)

    //  format time
    val formattedSelectedTime =
        String.format("%02d:%02d", timeReminder.time.hour, timeReminder.time.minute)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.padding(15.dp)) {
                Text(
                    text = stringResource(id = R.string.title_dialog_set_reminder),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    style = MaterialTheme.typography.headlineLarge
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(bottom = 15.dp)
                        .fillMaxWidth(),
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 10.dp)
                    )
                    Text(
                        text = formattedSelectedDate,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            onChooseDatePicker()
                        }
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(bottom = 15.dp)
                        .fillMaxWidth(),
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.time_icon),
                        contentDescription = null,
                        modifier = Modifier.padding(end = 10.dp)
                    )
                    Text(
                        text = formattedSelectedTime,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            onChooseTimePicker()
                        }
                    )
                }

                Row {
                    if (showDeleteText) {
                        TextButton(onClick = onDelete) {
                            Text(
                                text = stringResource(id = R.string.delete_reminder),
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold
                                ),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismiss) {
                        Text(text = stringResource(id = R.string.dialog_cancel))
                    }
                    TextButton(onClick = onConfirm) {
                        Text(text = stringResource(id = R.string.dialog_ok))
                    }
                }
            }
        }
    }
}

/**
 * Disable past dates in date picker
 */
@OptIn(ExperimentalMaterial3Api::class)
object PresentOrFutureSelectableDates : SelectableDates {
    @ExperimentalMaterial3Api
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val today = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 0)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val selectedDate = Calendar.getInstance().apply {
            timeInMillis = utcTimeMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return selectedDate.after(today) || selectedDate.equals(today)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePicker(
    onConfirm: (Date) -> Unit,
    onDismiss: () -> Unit,
) {
    val datePickerState = rememberDatePickerState(selectableDates = PresentOrFutureSelectableDates)
    val confirmEnabled = remember { derivedStateOf { datePickerState.selectedDateMillis != null } }

    DatePickerDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.dialog_cancel))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (datePickerState.selectedDateMillis != null) {
                        val date = Date(datePickerState.selectedDateMillis!!)
                        onConfirm(date)
                    }
                },
                enabled = confirmEnabled.value
            ) {
                Text(text = stringResource(id = R.string.dialog_ok))
            }
        }) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTimePicker(
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit,
) {
    val timePickerState = rememberTimePickerState(is24Hour = false)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { TimePicker(state = timePickerState) },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(
                    LocalTime.of(
                        timePickerState.hour,
                        timePickerState.minute
                    )
                )
            }) {
                Text(text = stringResource(id = R.string.dialog_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.dialog_cancel))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun NoteDetailScreenPreview() {
    //DialogSetReminder()
    DatePickerDialog(
        onDismissRequest = { /*TODO*/ },
        confirmButton = { /*TODO*/ }) {

    }
}