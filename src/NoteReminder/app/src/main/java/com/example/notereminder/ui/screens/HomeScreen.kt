package com.example.notereminder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.notereminder.DEFAULT_ID
import com.example.notereminder.R
import com.example.notereminder.data.NoteWithTags
import com.example.notereminder.data.entities.Tag
import com.example.notereminder.ui.AppViewModelProvider
import com.example.notereminder.ui.navigation.NavigationDestination
import java.text.DateFormat
import java.util.Date


object HomeDestination : NavigationDestination {
    override val route = "home"
}

@Composable
fun HomeScreen(
    navigateToNoteDetail: (Long) -> Unit,
    navController: NavHostController,
    onSeeDetailClicked: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            HomeTopAppBar(
                title = stringResource(id = R.string.app_name),
                canNavigateBack = navController.previousBackStackEntry != null,
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
                    contentDescription = stringResource(R.string.add_note)
                )
            }
        }
    ) { innerPadding ->
        HomeBody(
            noteWithTagsList = homeUiState.noteWithTagsList,
            modifier = Modifier.padding(innerPadding),
            onSeeDetailClicked = onSeeDetailClicked,
            onBookMarkClicked = viewModel::updateNote,
            onClearTagClicked = viewModel::deleteTag
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
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
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Calendar"
                )
            }
        }
    )
}

@Composable
fun HomeBody(
    noteWithTagsList: List<NoteWithTags>,
    onSeeDetailClicked: (Long) -> Unit,
    onBookMarkClicked: (NoteWithTags) -> Unit,
    onClearTagClicked: (Tag) -> Unit,
    modifier: Modifier = Modifier
) {
    NoteWithTagsList(
        noteWithTagsList = noteWithTagsList,
        onItemClicked = { onSeeDetailClicked(it.note.noteId) },
        onBookMarkClicked = { onBookMarkClicked(it) },
        modifier = modifier,
        onClearTagClicked = onClearTagClicked
    )
}

@Composable
fun NoteWithTagsList(
    noteWithTagsList: List<NoteWithTags>,
    onItemClicked: (NoteWithTags) -> Unit,
    onBookMarkClicked: (NoteWithTags) -> Unit,
    onClearTagClicked: (Tag) -> Unit,
    modifier: Modifier = Modifier
) {
    // sorted list
    // marked notes is on top of list
    // among marked notes, notes having reminder date close to current date is on top
    // among notes that have reminder date equal to one another, notes having created date close to current date is on top
    val sortedList = noteWithTagsList.sortedWith(
        compareByDescending<NoteWithTags> { it.note.isMarked }
            .thenByDescending { noteWithTags -> noteWithTags.note.reminderDate.takeIf { it > Date() } }
            .thenByDescending { it.note.createdDate }
    )

    LazyColumn(modifier = modifier) {
        items(sortedList.size) { index ->
            NoteItem(
                noteWithTags = sortedList[index],
                onBookMarkClicked = { onBookMarkClicked(it) },
                modifier = Modifier
                    .clickable { onItemClicked(sortedList[index]) }
                    .padding(
                        start = 10.dp,
                        end = 10.dp,
                        top = 5.dp,
                        bottom = 5.dp
                    ),
                onClearTagClicked = onClearTagClicked
            )
        }
    }
}

@Composable
fun NoteItem(
    noteWithTags: NoteWithTags,
    onBookMarkClicked: (NoteWithTags) -> Unit,
    onClearTagClicked: (Tag) -> Unit,
    modifier: Modifier = Modifier
) {
    // format created date and reminder date
    val createdDate = noteWithTags.note.createdDate
    val reminderDate = noteWithTags.note.reminderDate
    val df = DateFormat.getDateInstance(DateFormat.DEFAULT)
    val formattedCreatedDate = df.format(createdDate)
    val formattedReminderDate = df.format(reminderDate)

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column {
            Row {
                Text(
                    text = noteWithTags.note.title,
                    modifier = Modifier
                        .weight(1f)
                        .padding(all = 5.dp),
                    fontSize = 25.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
                BookMarkIcon(
                    noteWithTags = noteWithTags,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 5.dp),
                    onBookMarkClicked = onBookMarkClicked
                )
            }

            Text(
                text = noteWithTags.note.content,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier
                    .padding(all = 5.dp)
                    .fillMaxWidth()
            )
            TagsListInNote(
                tags = noteWithTags.tags,
                onClearTagClicked = onClearTagClicked,
            )
            Row {
                Text(
                    text = formattedCreatedDate,
                    modifier = Modifier
                        .padding(all = 5.dp),
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = formattedReminderDate,
                    modifier = Modifier
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
    Icon(
        painter = if (noteWithTags.note.isMarked) {
            painterResource(id = R.drawable.bookmarked)
        } else {
            painterResource(id = R.drawable.bookmark_border)
        },
        contentDescription = stringResource(id = R.string.bookmark),
        modifier = modifier
            .size(30.dp)
            .clickable {
                onBookMarkClicked(
                    noteWithTags.copy(
                        note = noteWithTags.note.copy(
                            isMarked = !noteWithTags.note.isMarked
                        ), tags = noteWithTags.tags
                    )
                )
            }
    )
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
                contentDescription = stringResource(id = R.string.clearTag),
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

//@Preview
//@Composable
//fun TagPreview() {
//    TagItem(tag1)
//}

//@Preview
//@Composable
//fun TagListPreview() {
//    TagsListInNote(tags = listOf(tag1, tag1, tag1))
//}

//@Preview
//@Composable
//fun HomeScreenPreview() {
//    HomeScreen({})
//}

//@Preview
//@Composable
//fun NoteItemPreview() {
//    NoteItem(noteWithTags = item3)
//}

//@Preview
//@Composable
//fun HomeBodyPreview() {
//    HomeBody(
//        noteWithTagsList = listOf(item1, item2, item3),
//        modifier = Modifier.padding(9.dp)
//    )
//}