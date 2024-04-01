package com.example.notereminder.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notereminder.DEFAULT_ID
import com.example.notereminder.R
import com.example.notereminder.data.NoteWithTags
import com.example.notereminder.data.entities.Tag
import com.example.notereminder.ui.AppViewModelProvider
import com.example.notereminder.ui.navigation.NavigationDestination
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object HomeDestination : NavigationDestination {
    override val route = "home"
}

@Composable
fun HomeScreen(
    navigateToSearch: () -> Unit,
    navigateToNoteDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()

    // if user choose delete selected notes, show dialog to ask again
    if (homeUiState.isShowingDialogDeleteNotes) {
        DialogRemind(
            textRes = R.string.title_dialog_delete_notes,
            onDismiss = viewModel::updateShowingDialogDeleteNotes,
            onConfirm = {
                viewModel.deleteSelectedNotes()
                viewModel.updateShowingDialogDeleteNotes()
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            HomeTopAppBar(
                title = stringResource(id = R.string.app_name),
                isInMultiSelectMode = homeUiState.selectedNotes.isNotEmpty(),
                exitMultiSelectMode = viewModel::exitMultiSelectMode,
                deleteAllNotes = viewModel::updateShowingDialogDeleteNotes,
                navigateToSearch = navigateToSearch
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToNoteDetail(DEFAULT_ID) },
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.clip(shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.description_add_note)
                )
            }
        }
    ) { innerPadding ->
        HomeBody(
            homeUiState = homeUiState,
            modifier = Modifier.padding(innerPadding),
            onNoteClicked = { noteId ->
                if (homeUiState.selectedNotes.isNotEmpty()) {
                    homeUiState.noteWithTagsList.forEach {
                        if (it.note.noteId == noteId) {
                            viewModel.putToSelectedNotes(it)
                        }
                    }
                } else {
                    navigateToNoteDetail(noteId)
                }
            },
            onBookMarkClicked = viewModel::updateNote,
            onClearTagClicked = viewModel::deleteTag,
            onNoteLongClicked = viewModel::putToSelectedNotes
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    title: String,
    navigateToSearch: () -> Unit,
    isInMultiSelectMode: Boolean,
    exitMultiSelectMode: () -> Unit,
    deleteAllNotes: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = { Text(title, style = MaterialTheme.typography.displayLarge) },
        modifier = modifier,
        actions = {
            if (isInMultiSelectMode) {
                IconButton(onClick = deleteAllNotes) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(id = R.string.description_delete_selected_notes_icon)
                    )
                }
                IconButton(onClick = exitMultiSelectMode) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(id = R.string.description_exit_multiselect_icon)
                    )
                }
            } else {
                IconButton(onClick = navigateToSearch) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(id = R.string.description_search_icon)
                    )
                }
            }
        }
    )
}

@Composable
fun HomeBody(
    homeUiState: HomeUiState,
    onNoteClicked: (Long) -> Unit,
    onBookMarkClicked: (NoteWithTags) -> Unit,
    onClearTagClicked: (Tag) -> Unit,
    onNoteLongClicked: (NoteWithTags) -> Unit,
    modifier: Modifier = Modifier
) {
    NoteWithTagsList(
        selectedNotes = homeUiState.selectedNotes,
        noteWithTagsList = homeUiState.noteWithTagsList,
        onNoteClicked = { onNoteClicked(it.note.noteId) },
        onBookMarkClicked = { onBookMarkClicked(it) },
        modifier = modifier,
        onClearTagClicked = onClearTagClicked,
        onNoteLongClicked = onNoteLongClicked,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteWithTagsList(
    selectedNotes: List<NoteWithTags>,
    noteWithTagsList: List<NoteWithTags>,
    onNoteClicked: (NoteWithTags) -> Unit,
    onNoteLongClicked: (NoteWithTags) -> Unit,
    onBookMarkClicked: (NoteWithTags) -> Unit,
    onClearTagClicked: (Tag) -> Unit,
    modifier: Modifier = Modifier
) {
    // sort list
    // marked notes is on top of list
    // among marked notes, notes having reminder date close to current date is on top, and notes don't have reminder date is at bottom
    val sortedList = noteWithTagsList.sortedWith(
        compareByDescending<NoteWithTags> { it.note.isMarked }
            .thenByDescending { noteWithTags ->
                noteWithTags.note.reminderDate?.let { it > Date() } ?: false
            }
    )

    LazyColumn(modifier = modifier) {
        items(sortedList.size) { index ->
            NoteItem(
                noteWithTags = sortedList[index],
                isLongClicked = selectedNotes.any { noteWithTag ->
                    noteWithTag == sortedList[index]
                },
                onBookMarkClicked = { onBookMarkClicked(it) },
                modifier = Modifier
                    .padding(
                        start = 10.dp,
                        end = 10.dp,
                        top = 5.dp,
                        bottom = 5.dp
                    )
                    .combinedClickable(
                        onClick = { onNoteClicked(sortedList[index]) },
                        onLongClick = { onNoteLongClicked(sortedList[index]) },
                    ),
                onClearTagClicked = onClearTagClicked
            )
        }
    }
}

@Composable
fun NoteItem(
    noteWithTags: NoteWithTags,
    isLongClicked: Boolean,
    onBookMarkClicked: (NoteWithTags) -> Unit,
    onClearTagClicked: (Tag) -> Unit,
    modifier: Modifier = Modifier
) {
    // format reminder date
    val reminderDate = noteWithTags.note.reminderDate
   // val df = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val formattedReminderDate: String? = if (reminderDate != null) {
        val df = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        df.format(reminderDate)
    } else {
        null
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .let {
                if (isLongClicked) {
                    it.border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.surfaceTint,
                        shape = RoundedCornerShape(10.dp)
                    )
                } else {
                    it
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(
            modifier = Modifier.let {
                if (isLongClicked) {
                    it.background(color = MaterialTheme.colorScheme.inversePrimary)
                } else {
                    it
                }
            }
        ) {
            Row {
                Text(
                    text = noteWithTags.note.title,
                    modifier = Modifier
                        .weight(1f)
                        .padding(all = 5.dp),
                    fontSize = 25.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
                BookMarkIcon(
                    noteWithTags = noteWithTags,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 5.dp),
                    onBookMarkClicked = onBookMarkClicked
                )
            }

            if (noteWithTags.note.content.isNotEmpty()){
                Text(
                    text = noteWithTags.note.content,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3,
                    modifier = Modifier
                        .padding(all = 5.dp)
                        .fillMaxWidth()
                )
            }

            TagsListInNote(
                tags = noteWithTags.tags,
                onClearTagClicked = onClearTagClicked,
            )
            if (formattedReminderDate != null) {
                Text(
                    text = formattedReminderDate,
                    modifier = Modifier
                        .align(alignment = Alignment.End)
                        .padding(all = 5.dp),
                )
            }
        }
    }
}

@Composable
fun BookMarkIcon(
    noteWithTags: NoteWithTags,
    onBookMarkClicked: (NoteWithTags) -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(onClick = {
        onBookMarkClicked(
            noteWithTags.copy(
                note = noteWithTags.note.copy(
                    isMarked = !noteWithTags.note.isMarked
                ), tags = noteWithTags.tags
            )
        )
    }) {
        Icon(
            painter = if (noteWithTags.note.isMarked) {
                painterResource(id = R.drawable.bookmarked)
            } else {
                painterResource(id = R.drawable.bookmark_border)
            },
            contentDescription = stringResource(id = R.string.description_bookmark),
            modifier = modifier
                .size(30.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagsListInNote(
    tags: List<Tag>,
    onClearTagClicked: (Tag) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(modifier = modifier) {
        tags.forEach { tag ->
            TagItem(
                tag = tag,
                onClearTagClicked = { onClearTagClicked(it) },
            )
        }
    }
}

@Composable
fun TagItem(
    tag: Tag,
    onClearTagClicked: (Tag) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .background(Color.Transparent)
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.inversePrimary)
        ) {
            Text(
                text = tag.name,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.inversePrimary)
                    .padding(4.dp),
                textAlign = TextAlign.Center
            )
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = stringResource(id = R.string.description_clearTag),
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.inversePrimary)
                    .align(alignment = Alignment.CenterVertically)
                    .padding(4.dp)
                    .size(15.dp)
                    .clickable { onClearTagClicked(tag) },
            )
        }
    }
}