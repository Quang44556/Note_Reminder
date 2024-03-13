package com.example.notereminder.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.notereminder.MyTopAppBar
import com.example.notereminder.R
import com.example.notereminder.data.NoteWithTags
import com.example.notereminder.data.entities.Note
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

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            MyTopAppBar(
                title = stringResource(id = NoteDetailDestination.titleRes),
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = navigateBack,
                navController = navController,
            )
        },
    ) { innerPadding ->
        NoteDetailBody(
            noteWithTags = uiState.noteWithTags,
            onTextFieldChange = viewModel::updateUiState,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun NoteDetailBody(
    noteWithTags: NoteWithTags,
    onTextFieldChange: (NoteWithTags) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        TagsListInNote(
            tags = noteWithTags.tags,
            modifier = Modifier.padding(5.dp)
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
            textStyle = MaterialTheme.typography.titleLarge
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

@Preview
@Composable
fun NoteDetailScreenPreview() {
    NoteDetailScreen({}, rememberNavController())
}