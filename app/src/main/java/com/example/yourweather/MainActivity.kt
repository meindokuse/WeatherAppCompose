package com.example.yourweather

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yourweather.repos.LocalReposetoryHelper
import com.example.yourweather.models.AppAction
import com.example.yourweather.models.AppState
import com.example.yourweather.screens.DataLoadingScreen
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.runtime.collectAsState
import com.example.yourweather.domain.ScreenStore
import com.example.yourweather.domain.ScreenStoreFactory
import com.example.yourweather.screens.successinit.SuccessInitScreen


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val localReposetoryHelper = LocalReposetoryHelper(applicationContext)


        setContent {
            val screenStore: ScreenStore = viewModel(factory = ScreenStoreFactory(localReposetoryHelper))
            val status = screenStore.appState.collectAsState()
            val systemUiController: SystemUiController = rememberSystemUiController()

            LaunchedEffect(true) {
                systemUiController.setStatusBarColor(Color.Black)
                screenStore.obtainEvent(AppAction.FirstEnter,applicationContext)
            }
            when(status.value){
                is AppState.SuccessInit-> {
                    SuccessInitScreen(
                        screenState = status.value as AppState.SuccessInit,
                        updateWeather = {screenStore.obtainEvent(AppAction.UpdateData, applicationContext)},
                        updateLocation = {screenStore.obtainEvent(AppAction.UpdateLocation,applicationContext)},
                        updateAll = {screenStore.obtainEvent(AppAction.UpdateAll,applicationContext)},
                        onAddCity = { newLocation ->
                            screenStore.obtainEvent(AppAction.AddLocation(newLocation), applicationContext)
                        },
                        deleteLocation = {deletedLocation ->
                            screenStore.obtainEvent(AppAction.DeleteLocation(deletedLocation),applicationContext)
                        },
                        switchLocation = {currentLocation ->
                            screenStore.obtainEvent(AppAction.SwitchLocation(currentLocation),applicationContext)
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












