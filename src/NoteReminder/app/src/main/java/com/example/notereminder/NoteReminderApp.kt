package com.example.notereminder

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.notereminder.ui.navigation.AppNavHost
import com.example.notereminder.ui.screens.BookMarkInNote
import com.example.notereminder.ui.screens.HomeDestination
import com.example.notereminder.ui.screens.NoteDetailDestination

@Composable
fun NoteReminderApp(navController: NavHostController = rememberNavController()) {
    AppNavHost(navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {}
) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route

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
            if (currentRoute == HomeDestination.route) {
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
            if (currentRoute == NoteDetailDestination.routeWithArgs) {
                IconButton(onClick = {}) {
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
        }
    )

}