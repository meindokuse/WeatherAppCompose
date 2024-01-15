package com.example.yourweather

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.window.SplashScreen
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yourweather.database.ReposetoryHelper
import com.example.yourweather.models.AppAction
import com.example.yourweather.models.AppState
import com.example.yourweather.screens.DataLoadingScreen
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.runtime.collectAsState
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.yourweather.screens.successinit.SuccessInitScreen


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val reposetoryHelper = ReposetoryHelper(applicationContext)


        setContent {
            val screenStore:ScreenStore = viewModel(factory = ScreenStoreFactory(reposetoryHelper))
            val status = screenStore.appState.collectAsState()
            val systemUiController: SystemUiController = rememberSystemUiController()

            LaunchedEffect(true) {
                systemUiController.setStatusBarColor(Color.Black)
                screenStore.obtainEvent(AppAction.FirstEnter,applicationContext)
            }
            when(status.value){
                is AppState.SuccessInit-> {
                    Log.d("MyLog","Вход в успешную")
                    SuccessInitScreen(
                        screenState = status.value as AppState.SuccessInit,
                        updateWeather = {screenStore.obtainEvent(AppAction.UpdateData, applicationContext)},
                        updateLocation = {screenStore.obtainEvent(AppAction.UpdateLocation,applicationContext)},
                        updateAll = {screenStore.obtainEvent(AppAction.UpdateAll,applicationContext)},
                        onAddCity = { newLocation ->
                            screenStore.obtainEvent(AppAction.AddLocation(newLocation), applicationContext)
                        }
                        )
                }
                is AppState.NoData->{
                    Log.d("MyLog","Вход в не успешную")
                    DataLoadingScreen(state = status.value as AppState.NoData ,
                        updatingFun = {screenStore.obtainEvent(AppAction.UpdateAll,applicationContext)})
                }
                else -> {
//                    SplashScreen.SPLASH_SCREEN_STYLE_ICON
                }
            }
        }
    }
}












