package com.example.yourweather.screens.successinit

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yourweather.R
import com.example.yourweather.models.AppState
import com.example.yourweather.screens.dialogs.AddCityDialog
import com.example.yourweather.screens.dialogs.DialogShouldShowRationale
import com.example.yourweather.ui.theme.CardBackgroundSecondV
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SuccessInitScreen(
    screenState: AppState.SuccessInit,
    updateWeather:()->Unit,
    updateLocation:()->Unit,
    updateAll:()->Unit,
    onAddCity:(String)->Unit
){
    val permissionState = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)

    val dialogState = remember {
        mutableStateOf(false)
    }
    val dialogAddState = remember {
        mutableStateOf(false)
    }
    if (dialogState.value){
        DialogShouldShowRationale(
            permissionState = permissionState,
            dialogState = dialogState
        )
    }
    if (dialogAddState.value){
        AddCityDialog(
            onAddCity = onAddCity,
            dialogState = dialogAddState
        )
    }

    LaunchedEffect(permissionState.permissionRequested) {
        when {
            permissionState.hasPermission -> {
                updateAll()
            }
            permissionState.shouldShowRationale -> {
                dialogState.value = true
            }
            else -> {
                permissionState.launchPermissionRequest()
            }
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    DismissibleNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            DismissibleDrawerSheet(
                drawerContainerColor = CardBackgroundSecondV,
                modifier = Modifier
                    .background(Color.Black)
                    .clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
            ) {
                NavigationContent(screenState = screenState, dialogState = dialogAddState)
            }
        }) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    // FOR OPEN DRAWER
                    IconButton(
                        onClick = {
                            scope.launch {
                                Log.d("MyLog", "qwe")
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_density_small_24,),
                            contentDescription = "",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .size(50.dp)
                        )
                    }
                    IconButton(onClick = { updateLocation() },) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_share_location_24,),
                            contentDescription = "",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .size(50.dp)
                        )
                    }
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Погода", style = TextStyle(
                            fontSize = 50.sp, color = Color.White,
                        ),
                        modifier = Modifier.padding(70.dp)
                    )
                }

                MainScreen(
                    data = screenState,
                    updateWeather = updateWeather
                )
            }
        }
    }
}





