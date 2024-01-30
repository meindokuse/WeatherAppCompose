package com.example.yourweather.screens

import android.Manifest
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yourweather.models.AppAction
import com.example.yourweather.models.AppState
import com.example.yourweather.models.DataLoadingState
import com.example.yourweather.screens.dialogs.DialogShouldShowRationale
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DataLoadingScreen(
    state: AppState.NoData,
    updatingFun:()->Unit
) {
    val loadingState = remember {
        mutableStateOf("Запрашиваем необходиме данные...")
    }
    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val dialogState = remember {
        mutableStateOf(false)
    }
    if (dialogState.value){
        DialogShouldShowRationale(
            permissionState = permissionState,
            dialogState = dialogState
        )
    }

    LaunchedEffect(permissionState.permissionRequested) {
        when {
            permissionState.hasPermission -> {
                updatingFun()
            }
            permissionState.shouldShowRationale -> {
                dialogState.value = true
            }
            else -> {
                permissionState.launchPermissionRequest()
            }
        }
    }
    Column(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = loadingState.value, style = TextStyle(fontSize = 22.sp, fontFamily = FontFamily.Monospace, color = Color.White))
        if (state.noLocation) {
            loadingState.value="No location available. Please enable location services."
        } else if (state.noConnection) {
            loadingState.value="No internet connection. Please check your network settings."
        } else if (state.loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = Color.White
            )
        } else if (state.success) {
            loadingState.value="Data loading was successful."
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (state.noConnection or state.noConnection){
                updatingFun()
            } },
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray, contentColor = Color.White)
        ) {
            Text("Retry", color = Color.White)
        }
    }
}





