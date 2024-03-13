package com.example.notereminder.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.notereminder.MyTopAppBar
import com.example.notereminder.R
import com.example.notereminder.data.NoteWithTags
import com.example.notereminder.data.entities.Tag
import com.example.notereminder.ui.AppViewModelProvider
import com.example.notereminder.ui.navigation.NavigationDestination
import java.text.DateFormat


object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@Composable
fun HomeScreen(
    navigateToNoteDetail: (Int) -> Unit,
    navController: NavHostController,
    onSeeDetailClicked: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            MyTopAppBar(
                title = stringResource(id = HomeDestination.titleRes),
                canNavigateBack = navController.previousBackStackEntry != null,
                navController = navController,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToNoteDetail(1) },
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
            onBookMarkClicked = viewModel::updateNote
        )
    }
}

@Composable
fun HomeBody(
    noteWithTagsList: List<NoteWithTags>,
    onSeeDetailClicked: (Int) -> Unit,
    onBookMarkClicked: (NoteWithTags) -> Unit,
    modifier: Modifier = Modifier
) {
    NoteWithTagsList(
        noteWithTagsList = noteWithTagsList,
        onItemClicked = { onSeeDetailClicked(it.note.noteId) },
        onBookMarkClicked = { onBookMarkClicked(it) },
        modifier = modifier
    )
}

@Composable
fun NoteWithTagsList(
    noteWithTagsList: List<NoteWithTags>,
    onItemClicked: (NoteWithTags) -> Unit,
    onBookMarkClicked: (NoteWithTags) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(noteWithTagsList.size) { index ->
            NoteItem(
                noteWithTags = noteWithTagsList[index],
                onBookMarkClicked = { onBookMarkClicked(it) },
                modifier = Modifier
                    .clickable { onItemClicked(noteWithTagsList[index]) }
                    .padding(
                        start = 10.dp,
                        end = 10.dp,
                        top = 5.dp,
                        bottom = 5.dp
                    ),
            )
        }
    }
}

@Composable
fun NoteItem(
    noteWithTags: NoteWithTags,
    onBookMarkClicked: (NoteWithTags) -> Unit,
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
                        .padding(all = 5.dp),
                    fontSize = 25.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
                Spacer(modifier = Modifier.weight(1f))
                BookMarkInNote(
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
            TagsListInNote(noteWithTags.tags)
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
fun BookMarkInNote(
    noteWithTags: NoteWithTags,
    onBookMarkClicked: (NoteWithTags) -> Unit,
    modifier: Modifier = Modifier
) {
    Image(
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
fun TagsListInNote(tags: List<Tag>, modifier: Modifier = Modifier) {
    FlowRow(modifier = modifier) {
        tags.forEach { tag -> TagItem(tag = tag) }
    }
}

@Composable
fun TagItem(tag: Tag, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .background(Color.Transparent)
            .padding(4.dp)
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
    }
}

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