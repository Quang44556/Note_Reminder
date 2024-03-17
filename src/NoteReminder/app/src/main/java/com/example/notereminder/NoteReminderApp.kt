package com.example.notereminder

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.notereminder.ui.screens.HomeDestination
import com.example.notereminder.ui.screens.HomeScreen
import com.example.notereminder.ui.screens.NoteDetailDestination
import com.example.notereminder.ui.screens.NoteDetailScreen

@Composable
fun NoteReminderApp(navController: NavHostController = rememberNavController()) {
    AppNavHost(navController = navController)
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                modifier = Modifier,
                navigateToNoteDetail = { navController.navigate("${NoteDetailDestination.route}/${it}") },
                navController = navController,
                onSeeDetailClicked = { navController.navigate("${NoteDetailDestination.route}/${it}") }
            )
        }
        composable(
            route = NoteDetailDestination.routeWithArgs,
            arguments = listOf(navArgument(NoteDetailDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            NoteDetailScreen(
                modifier = Modifier,
                navigateBack = { navController.navigateUp() },
                navController = navController,
            )
        }
    }
}