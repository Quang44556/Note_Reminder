package com.example.notereminder.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
    override val titleRes = R.string.note_detail_title
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@Composable
fun NoteDetailScreen(
    navigateBack: () -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: NoteDetailViewMode = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState = viewModel.noteDetailUiState

    if (uiState.isShowingDialog) {
        MyDialog(
            onDismiss = viewModel::updateShowingDialog,
            onConfirm = viewModel::insertTag
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            NoteDetailTopAppBar(
                noteWithTags = uiState.noteWithTags,
                title = stringResource(id = NoteDetailDestination.titleRes),
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = navigateBack,
                onBookMarkClicked = viewModel::updateNote,
                onAddTagClicked = viewModel::updateShowingDialog
            )
        },
    ) { innerPadding ->
        NoteDetailBody(
            noteWithTags = uiState.noteWithTags,
            onTextFieldChange = viewModel::updateNote,
            modifier = Modifier.padding(innerPadding),
            onClearTagClicked = viewModel::updateTagsInNote
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailTopAppBar(
    onBookMarkClicked: (NoteWithTags) -> Unit,
    onAddTagClicked: () -> Unit,
    noteWithTags: NoteWithTags,
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {}
) {
    TopAppBar(
        title = { Text(title, style = MaterialTheme.typography.displayLarge) },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        },
        actions = {
            BookMarkIcon(
                noteWithTags = noteWithTags,
                onBookMarkClicked = onBookMarkClicked
            )
            IconButton(
                onClick = onAddTagClicked,
                enabled = noteWithTags.note.noteId != 0L
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Search"
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Search"
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Calendar"
                )
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
fun MyDialog(
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
                    onDismiss()
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

@Preview
@Composable
fun NoteDetailScreenPreview() {
    NoteDetailScreen({}, rememberNavController())
}