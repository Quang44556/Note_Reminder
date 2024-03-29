package com.example.notereminder.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notereminder.R
import com.example.notereminder.data.NoteWithTags
import com.example.notereminder.data.entities.Tag
import com.example.notereminder.ui.AppViewModelProvider
import com.example.notereminder.ui.navigation.NavigationDestination

object SearchDestination : NavigationDestination {
    override val route = "search"
}

@Composable
fun SearchScreen(
    navigateBack: () -> Unit,
    navigateToNoteDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val searchUiState by viewModel.searchUiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            SearchTopAppBar(
                navigateUp = navigateBack,
                text = searchUiState.text,
                onValueChange = viewModel::changeText,
            )

            SearchScreenBody(
                modifier = Modifier.padding(10.dp),
                uiState = searchUiState,
                onNoteClicked = navigateToNoteDetail,
                onBookMarkClicked = viewModel::updateNote,
                onClearTagClicked = viewModel::deleteTag,
                onNoteLongClicked = {}
            )
        }
    }
}

@Composable
fun SearchScreenBody(
    onNoteClicked: (Long) -> Unit,
    onNoteLongClicked: (NoteWithTags) -> Unit,
    onBookMarkClicked: (NoteWithTags) -> Unit,
    onClearTagClicked: (Tag) -> Unit,
    modifier: Modifier = Modifier,
    uiState: SearchUiState,
) {
    NoteWithTagsList(
        modifier = modifier.padding(top = 10.dp),
        selectedNotes = listOf(),
        noteWithTagsList = uiState.noteWithTagsList,
        onNoteClicked = { onNoteClicked(it.note.noteId) },
        onNoteLongClicked = onNoteLongClicked,
        onBookMarkClicked = onBookMarkClicked,
        onClearTagClicked = onClearTagClicked
    )
}

@Composable
fun SearchTopAppBar(
    text: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {}
) {
    Row(modifier = modifier) {
        IconButton(
            onClick = navigateUp,
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back_button)
            )
        }

        MySearchBar(
            modifier = Modifier.padding(top = 10.dp, end = 10.dp),
            text = text,
            onValueChange = onValueChange
        )
    }
}

@Composable
fun MySearchBar(
    text: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
) {
    TextField(
        value = text,
        onValueChange = { onValueChange(it) },
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .border(2.dp, Color.DarkGray, RoundedCornerShape(30.dp)),
        placeholder = { Text(text = stringResource(id = R.string.search_hint)) },
        maxLines = 1,
        singleLine = true,
    )
}

@Preview
@Composable
fun Preview() {
    //SearchScreen(navigateBack = {})
}