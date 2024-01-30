package com.example.yourweather.domain

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yourweather.repos.LocalReposetoryHelper
import com.example.yourweather.models.AppAction
import com.example.yourweather.models.AppState
import com.example.yourweather.models.OtherLocations
import com.example.yourweather.models.WeatherForecast
import com.example.yourweather.models.WeatherScreen
import com.example.yourweather.repos.RemoteReposеtoryHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume


class ScreenStore(private val localReposetoryHelper: LocalReposetoryHelper):ViewModel() {
    private val _appState = MutableStateFlow<AppState>(AppState.Idle)
    val appState: StateFlow<AppState> = _appState
    private var disposable: Disposable? = null


    fun obtainEvent(event:AppAction,context: Context){
        when(val currentState = _appState.value ){
            is AppState.Idle -> reduce(currentState,event,context)
            is AppState.SuccessInit ->reduce(currentState,event,context)
            is AppState.NoData->{ reduce(currentState,event,context) }
        }
    }
    private fun reduce(state: AppState.NoData,action: AppAction,context: Context){
        when(action){
            AppAction.UpdateAll -> {
                Log.d("MyLog","reduce NoData")
                _appState.value = state.copy(loading = true)
                viewModelScope.launch(Dispatchers.IO) {
                    val location = requestLocationUpdateSuspend(context)
                    val newAddress = getAddressFromLocationAsync(
                        location.latitude,
                        location.longitude,
                        context
                    )
                    getWeatherWithApi(state,newAddress)
                }
            }
            else -> {}
        }
    }
    private fun reduce(state: AppState.Idle, action: AppAction, context: Context){
        when(action){
            AppAction.FirstEnter-> {
                viewModelScope.launch(Dispatchers.IO) {
                    val locations:MutableList<String> = ArrayList()
                    val initWeather = localReposetoryHelper.getAllWeatherForecasts()
                    locations.addAll(localReposetoryHelper.getAllLocations())
                    Log.d("MyLog","$locations")
                    Log.d("MyLog","$initWeather")
                    withContext(Dispatchers.Main){
                        if (initWeather != null){
                            val weatherScreen = WeatherScreen(initWeather.current,initWeather.forecast)
                            Log.d("MyLog","SuccessInit in Idle , ${initWeather.location}")
                            _appState.value = AppState.SuccessInit(0,locations,false, weatherScreen, initWeather.location)
                        } else{
                            Log.d("MyLog","NoData in Idle")
                            _appState.value = AppState.NoData(true,false,false,false)
                        }
                    }
                }
            }

            else -> {
            }
        }

    }
    private fun reduce(state: AppState.SuccessInit, action: AppAction, context: Context){
        when(action){
            AppAction.UpdateData -> {
                _appState.value = state.copy(loading = true)
                Log.d("MyLog","SuccessInit UpdateData ${_appState.value}")
                viewModelScope.launch {
                    getWeatherWithApi(state,state.location)
                    withContext(Dispatchers.Main){
                        _appState.emit(state.copy(loading = false))
                    }
                }
            }
            AppAction.UpdateAll -> {
                _appState.value = state.copy(loading = true)
                viewModelScope.launch(Dispatchers.IO) {
                    val location = requestLocationUpdateSuspend(context)
                    val newLocation = getAddressFromLocationAsync(location.latitude, location.longitude, context)
                    if (newLocation == "Belgorod") {
                        withContext(Dispatchers.Main) {
                            _appState.emit(state.copy(error = 1))
                        }
                        launch { updateErrorState(state)}
                        getWeatherWithApi(state.copy(error = 1),state.location)
                    }else{
                        getWeatherWithApi(state,newLocation)
                    }
                }
            }
            AppAction.UpdateLocation ->{
                _appState.value = state.copy(loading = true)
                viewModelScope.launch(Dispatchers.IO) {
                    val location =  requestLocationUpdateSuspend(context)
                    val newLocation = getAddressFromLocationAsync(location.latitude,location.longitude,context)

                    withContext(Dispatchers.Main){
                        if (newLocation == "Belgorod"){
                            _appState.emit(state.copy(error = 1))
                            launch { updateErrorState(state)}
                        } else{
                            _appState.emit(state.copy(location = newLocation))
                            getWeatherWithApi(state,newLocation)
                        }
                    }
                }
            }
            is AppAction.AddLocation->{
                viewModelScope.launch(Dispatchers.IO) {
                    val updatedLocations = state.otherLocations.toMutableList()
                    updatedLocations.add(action.newLocation)
                    localReposetoryHelper.addNewLocation( OtherLocations(location = action.newLocation))
                    withContext(Dispatchers.Main){
                        _appState.emit(state.copy(otherLocations = updatedLocations))
                    }
                }

            }
            is AppAction.DeleteLocation ->{

                val updatedLocations = state.otherLocations.toMutableList()
                updatedLocations.remove(action.currentLocation)

                viewModelScope.launch(Dispatchers.IO) {
                    localReposetoryHelper.deleteLocation( action.currentLocation)

                    withContext(Dispatchers.Main){
                        _appState.emit(state.copy(otherLocations = updatedLocations))
                    }
                }
            }

            is AppAction.SwitchLocation ->{
                viewModelScope.launch(Dispatchers.IO) {
                    withContext(Dispatchers.Main){
                        _appState.emit(state.copy(location = action.currentLocation))
                    }
                    getWeatherWithApi(state,action.currentLocation)
                }
            }
            else -> throw NotImplementedError("Error")
        }
    }


    @Suppress("DEPRECATION")
    private fun getAddressFromLocationAsync(latitude: Double, longitude: Double, context: Context): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)


        return if (addresses?.isNotEmpty()!!) {
                    Log.d("MyLog","Корректное получение локацмм $addresses")

                     addresses[0].locality?: "Belgorod"
                   } else {
                    "Belgorod"
                 }
    }

    private suspend fun requestLocationUpdateSuspend(context: Context): Location {
        return suspendCancellableCoroutine { continuation ->
            val fusedLocationClient: FusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(context)

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("MyLog", "нет разрешения")
                continuation.resume(Location("Belgorod"))
            } else {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        Log.d("MyLog", "$location")
                        location?.let {
                            Log.d("MyLog", "Получили локацию $it")
                            continuation.resume(Location(it))
                        } ?: run {
                            Log.d("MyLog", "Локация не доступна")
                            continuation.resume(Location("Belgorod"))
                        }
                    }
                    .addOnFailureListener {
                        Log.e("MyLog", "Ошибка при получении локации: ${it.message}")
                        continuation.resume(Location("Belgorod"))
                    }
            }

            continuation.invokeOnCancellation {}
        }
    }


    private fun getWeatherWithApi(state: AppState, city: String) {
        disposable?.dispose()
        disposable = RemoteReposеtoryHelper
            .getWeatherWithSingle(city)
            .subscribe(
                { weatherScreen ->
                    Log.d("MyLog", "$weatherScreen")
                    Log.d("MyLog", city)

                    when (state) {
                        is AppState.SuccessInit -> {
                            Log.d("MyLog", "Получили погоду")
                            val newState = state.copy(
                                loading = false,
                                weatherScreen = weatherScreen,
                                location = city
                            )
                            updateStateIfSuccess(newState)
                        }
                        is AppState.NoData -> {
                            _appState.value = AppState.SuccessInit(
                                error = 0,
                                otherLocations = emptyList(),
                                loading = false,
                                weatherScreen = weatherScreen,
                                location = city
                            )
                            viewModelScope.launch(Dispatchers.IO) {
                                val weatherForecast = WeatherForecast(
                                    location = city,
                                    current = weatherScreen.current,
                                    forecast = weatherScreen.forecast
                                )
                                localReposetoryHelper.insertWeatherForecast(weatherForecast)
                            }
                        }
                        else -> {
                            Log.d("MyLog", "Error with State")
                        }
                    }
                    disposable?.dispose()
                },
                {
                    Log.d("MyLog", "Error in Retrofit $it")
                    disposable?.dispose()
                    viewModelScope.launch {
                        when(state){
                            is AppState.SuccessInit ->{
                                _appState.emit(state.copy(error = 2))
                                updateErrorState(state)
                            }
                            is AppState.NoData -> _appState.emit(state.copy(success = false))
                            else -> {
                                throw NoSuchMethodError("State Error")
                            }
                        }
                    }

                }
            )
    }

    private fun updateStateIfSuccess(newState: AppState.SuccessInit){
        viewModelScope.launch(Dispatchers.IO) {
            val weatherForecast = WeatherForecast(
                id = 1,
                location = newState.location,
                current = newState.weatherScreen.current,
                forecast = newState.weatherScreen.forecast
            )
            localReposetoryHelper.updateWeatherForecast(weatherForecast)
            _appState.emit(newState)
        }
        Log.d("MyLog","updateStateIfSuccess $newState")
    }
    private suspend fun updateErrorState(state: AppState.SuccessInit){
        Log.d("MyLog","updateErrorState")
        delay(5000)
        _appState.emit(state.copy(error = 0))
        Log.d("MyLog","updateErrorState - Post Delay")

    }
//    fun getAddressFromLocation(latitude: Double, longitude: Double): Address? {
//        val geoApiContext = GeoApiContext.Builder()
//            .apiKey("YOUR_API_KEY")
//            .build()
//
//        val addresses = GeocodingApi.getFromLocation(
//            geoApiContext,
//            LatLng(latitude, longitude),
//            1
//        )
//        return addresses.firstOrNull()?.address
//    }

}

//const val api = "AIzaSyDPqmC2KBEIR6Asth1WkZ1Ty5KdEGoWjIA"