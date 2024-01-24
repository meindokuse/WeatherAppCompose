package com.example.yourweather.screens.successinit

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationEndReason
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yourweather.R
import com.example.yourweather.models.AppAction
import com.example.yourweather.models.AppState
import com.example.yourweather.screens.dialogs.AddCityDialog
import com.example.yourweather.screens.dialogs.DialogShouldShowRationale
import com.example.yourweather.ui.theme.CardBackgroundSecondV
import com.example.yourweather.ui.theme.WhiteCream
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SuccessInitScreen(
    screenState: AppState.SuccessInit,
    updateWeather:()->Unit,
    updateLocation:()->Unit,
    updateAll:()->Unit,
    onAddCity:(String)->Unit,
    deleteLocation:(String) ->Unit,
    switchLocation: (String)-> Unit
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

    val scrollState = rememberLazyListState()
   val current = LocalDensity.current

    val titleAlpha = remember {
        MutableStateFlow(0f)
    }
    val textAlpha = remember {
        MutableStateFlow(1f)
    }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.firstVisibleItemScrollOffset }
            .collect { scrollOffset ->
                val density = current.density
                val pixelsToDp = with(current) { (200.dp.toPx() / density).dp }

                titleAlpha.emit(scrollOffset / pixelsToDp.value)
                textAlpha.emit(1f - titleAlpha.value)
            }
    }

    LaunchedEffect(key1 = screenState.error, block = {
        when(screenState.error){
            1->{
                snackbarHostState.showSnackbar(
                    message = "Проблемы с получением локации",
                    actionLabel = "ОК",
                    duration = SnackbarDuration.Long
                )
            }
            2->{
                snackbarHostState.showSnackbar(
                    message = "Проблемы с получением данных о погоде",
                    actionLabel = "ОК",
                    duration = SnackbarDuration.Long
                )
            }
        }
    })
    val visibility = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = drawerState.isOpen, block = {
            visibility.value = drawerState.isOpen
    })



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

                NavigationContent(
                    screenState = screenState,
                    dialogState = dialogAddState,
                    deleteLocation = deleteLocation,
                    switchLocation = switchLocation,
                    drawerState = drawerState,
                    visibility = visibility
                    )
            }
        }) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState,
                    snackbar = {data->
                        Snackbar(
                            containerColor = WhiteCream,
                            snackbarData = data,
                            contentColor = Color.Black,
                            actionColor = Color.Black
                        )
                    })
            },
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Black,
                        titleContentColor = Color.White,
                    ),
                    title = {
                        Text(
                            "Погода",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .graphicsLayer(alpha = titleAlpha.collectAsState().value, compositingStrategy = CompositingStrategy.ModulateAlpha )
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.apply {
                                        if (isClosed) {
                                            visibility.value = true
                                            open()
                                        } else {
                                            visibility.value = false

                                            close()
                                        }
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
                    },
                    actions = {
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
                    },
                )
            },

        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .background(Color.Black),
                state = scrollState

            ) {
                item {

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Погода", style = TextStyle(
                                fontSize = 40.sp, color = Color.White,
                            ),
                            modifier = Modifier
                                .padding(60.dp)
                                .graphicsLayer(
                                    alpha = textAlpha.collectAsState().value,
                                    compositingStrategy = CompositingStrategy.ModulateAlpha
                                )
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
}







