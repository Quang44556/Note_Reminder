package com.example.notereminder

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import com.example.notereminder.ui.theme.NoteReminderTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoteReminderTheme {
                /**
                 * Ask permission to post notifications
                 */
                val showPermissionDialog = rememberSaveable { mutableStateOf(false) }

                val permissionState =
                    rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

                val requestPermissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (!isGranted) {
                        // show dialog to explain or ask user to go to setting
                        showPermissionDialog.value = true
                    }
                }

                LaunchedEffect(permissionState) {
                    if (!permissionState.status.isGranted && permissionState.status.shouldShowRationale) {
                        // show dialog to explain
                        showPermissionDialog.value = true
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                // if permission was granted, show main screen
                if (permissionState.status.isGranted) {
                    NoteReminderApp()
                }

                // show dialog
                if (showPermissionDialog.value) {
                    if (permissionState.status.shouldShowRationale) {
                        PermissionDialog(
                            strRes = R.string.explain_permission,
                            onOkClick = {
                                showPermissionDialog.value = false
                                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        )
                    } else {
                        PermissionDialog(
                            strRes = R.string.go_to_setting,
                            onOkClick = {
                                openAppSettings()
                                showPermissionDialog.value = false
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Go to app setting
 */
fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

/**
 * Dialog to explain permission or ask user to go to setting
 */
@Composable
fun PermissionDialog(
    strRes: Int,
    onOkClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {},
        text = {
            Text(text = stringResource(id = strRes))
        },
        confirmButton = {
            TextButton(
                onClick = onOkClick
            ) {
                Text(stringResource(id = R.string.dialog_ok))
            }
        },
    )
}