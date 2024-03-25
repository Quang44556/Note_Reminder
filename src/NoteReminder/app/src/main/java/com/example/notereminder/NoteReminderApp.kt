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

/**
 * Top level composable that represents screens for the application.
 */
@Composable
fun NoteReminderApp(navController: NavHostController = rememberNavController()) {
    AppNavHost(navController = navController)
}

/**
 * Provides Navigation graph for the application.
 */
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
            )
        }
        composable(
            route = NoteDetailDestination.routeWithArgs,
            arguments = listOf(navArgument(NoteDetailDestination.ITEM_ID_ARG) {
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