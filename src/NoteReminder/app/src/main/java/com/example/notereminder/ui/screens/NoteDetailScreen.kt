package com.example.notereminder.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.notereminder.R
import com.example.notereminder.data.NoteWithTags
import com.example.notereminder.data.entities.Tag
import com.example.notereminder.ui.AppViewModelProvider
import com.example.notereminder.ui.navigation.NavigationDestination

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
            onConfirm = viewModel::insertTagToUiState
        )
    }

    // dialog remind to save note
    if (uiState.isShowingDialogSaveNote) {
        MyAlertDialog(
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
        MyAlertDialog(
            textRes = R.string.title_dialog_delete_note,
            onDismiss = viewModel::updateShowingDialogDeleteNote,
            onConfirm = {
                viewModel.deleteNoteWithTags()
                viewModel.updateShowingDialogDeleteNote()
                navigateBack()
            },
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
                onBookMarkClicked = viewModel::updateNoteDetailUiState,
                onAddTagClicked = viewModel::updateShowingDialogAddTag,
                onDeleteClicked = viewModel::updateShowingDialogDeleteNote,
                onSaveClicked = {
                    viewModel.insertOrUpdateNoteWithTags()
                }
            )
        },
    ) { innerPadding ->
        NoteDetailBody(
            noteWithTags = uiState.noteWithTags,
            onTextFieldChange = viewModel::updateNoteDetailUiState,
            modifier = Modifier.padding(innerPadding),
            onClearTagClicked = viewModel::deleteTagInUiState
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
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_button)
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
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.DateRange,
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
            Button(
                onClick = {
                    onConfirm(Tag(name = text))
                }
            ) {
                Text(text = stringResource(id = R.string.dialog_ok))
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text(text = stringResource(id = R.string.dialog_cancel))
            }
        }
    )
}

@Composable
fun MyAlertDialog(
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
            Button(
                onClick = onConfirm
            ) {
                Text(stringResource(id = R.string.dialog_yes))
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text(text = stringResource(id = R.string.dialog_no))
            }
        }
    )
}

@Preview
@Composable
fun NoteDetailScreenPreview() {
    NoteDetailScreen({})
}