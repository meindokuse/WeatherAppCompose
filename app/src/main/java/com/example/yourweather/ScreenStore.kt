package com.example.yourweather

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yourweather.api.getWeatherSingle
import com.example.yourweather.repos.LocalReposetoryHelper
import com.example.yourweather.models.AppAction
import com.example.yourweather.models.AppState
import com.example.yourweather.models.OtherLocations
import com.example.yourweather.models.WeatherForecast
import com.example.yourweather.models.WeatherScreen
import com.example.yourweather.repos.RemoteReposetoryHelper
import com.google.android.gms.location.LocationServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Locale



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
                    requestLocationUpdates(context){ location->
                       val newLocation = getAddressFromLocationAsync(
                           location.latitude,
                           location.longitude,
                           context
                       )
                        getWeatherWithApi(state,newLocation)
                    }
                    withContext(Dispatchers.Main){
                        _appState.value = state.copy(loading = false)
                    }
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
                            _appState.value = AppState.SuccessInit(locations,false, weatherScreen, initWeather.location)
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
                        _appState.value = state.copy(loading = false)
                    }
                }
            }
            AppAction.UpdateAll -> {
                _appState.value = state.copy(loading = true)
                viewModelScope.launch(Dispatchers.IO) {
                    requestLocationUpdates(context){location->
                        val newLocation = getAddressFromLocationAsync(
                            location.latitude,
                            location.longitude,
                            context
                        )
                        getWeatherWithApi(state,newLocation)
                    }
                    withContext(Dispatchers.Main){
                        _appState.value = state.copy(loading = false)
                    }
                }
            }
            AppAction.UpdateLocation ->{
                _appState.value = state.copy(loading = true)
                viewModelScope.launch(Dispatchers.IO) {
                    requestLocationUpdates(context){location->
                        val newLocation = getAddressFromLocationAsync(
                            location.latitude,
                            location.longitude,
                            context
                        )
                        Log.d("MyLog","NewLocation $newLocation")
                        _appState.value = state.copy(loading = false, location = newLocation)
                    }
                }
            }
            is AppAction.AddLocation->{

                viewModelScope.launch(Dispatchers.IO) {
                    localReposetoryHelper.addNewLocation( OtherLocations(location = action.newLocation))
                }
                val updatedLocations = state.otherLocations.toMutableList()
                updatedLocations.add(action.newLocation)
                _appState.value = state.copy(otherLocations = updatedLocations)


            }
            is AppAction.DeleteLocation ->{

                val updatedLocations = state.otherLocations.toMutableList()
                updatedLocations.remove(action.currentLocation)
                _appState.value = state.copy(otherLocations = updatedLocations)

                viewModelScope.launch(Dispatchers.IO) {
                    localReposetoryHelper.deleteLocation( action.currentLocation)
                }
            }
            is AppAction.SwitchLocation ->{
                _appState.value = state.copy(location = action.currentLocation)
                viewModelScope.launch(Dispatchers.IO) {
                    getWeatherWithApi(state,action.currentLocation)
                }
            }

            else -> throw NotImplementedError("Error")

        }
    }


    private fun getAddressFromLocationAsync(latitude: Double, longitude: Double,context: Context): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)

        return if (addresses?.isNotEmpty()!!) {
                    Log.d("MyLog","Корректное получение локацмм $addresses")
                     addresses[0].locality?: "Belgorod"
                   } else {
                    "Belgorod"
                 }
    }

    private fun requestLocationUpdates(context: Context, callback: (Location) -> Unit)  {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("MyLog", "нет разрешения")
            callback.invoke(Location("Belgorod"))
        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    Log.d("MyLog","$location")
                    location?.let {
                        Log.d("MyLog","Получили локацию $it")
                        callback.invoke(it)
                    } ?: run {
                        Log.d("MyLog", "Локация не доступна")
                        callback.invoke(Location("Belgorod"))
                    }
                }
                .addOnFailureListener {
                    Log.e("MyLog", "Ошибка при получении локации: ${it.message}")
                    callback.invoke(Location("Belgorod"))
                }
        }
    }

    private fun getWeatherWithApi(state: AppState, city: String) {
        disposable?.dispose()
        disposable = RemoteReposetoryHelper
            .getWeatherWithSingle(city)
            .subscribe(
                { weatherScreen ->
                    Log.d("MyLog", "$weatherScreen")
                    Log.d("MyLog", city)

                    when(state){
                        is AppState.SuccessInit->{
                            Log.d("MyLog","Получили погоду")
                            val newState = state.copy(
                                loading = false,
                                    weatherScreen =weatherScreen,
                                    location = city)
                            updateStateIfSuccess(newState)
                        }
                        is AppState.NoData->{
                            _appState.value = AppState.SuccessInit(
                                otherLocations = emptyList(),
                                loading = false,
                                weatherScreen =weatherScreen,
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
        }
        Log.d("MyLog","updateStateIfSuccess $newState")
        _appState.value = newState
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