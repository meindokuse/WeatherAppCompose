package com.example.yourweather

import android.util.Log
import androidx.compose.runtime.MutableState
import com.example.yourweather.RetrofitInstance.API
import com.example.yourweather.models.Current
import com.example.yourweather.models.ForecastDay
import com.example.yourweather.models.WeatherForecast
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.internal.wait
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://api.weatherapi.com/v1/"
    const val API = "7ea8a53b59324322a25220845240701"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

//suspend fun updateWeather(apiKey: String, city: String, weatherCurrent: MutableState<WeatherForecast?>) {
//    try {
//        val weatherResponse = RetrofitInstance.apiService.getListHoursWeather(apiKey, city)
//        weatherCurrent.value = weatherResponse
//    } catch (e: Exception) {
//        // Обработка ошибок
//        e.printStackTrace()
//    }
//}

fun getWeatherSingle(city: String):Single<WeatherForecast>{
    return RetrofitInstance.apiService
        .getListHoursWeather(API,city)
        .map { response ->
            if (response.isSuccessful){
                response.body()?: throw Exception("Response body is null")
            }else{
                throw Exception("Request failed with code ${response.code()}")
            }
        }
}

fun updateWeatherSingle(city: String,forecastState: MutableState<WeatherForecast?>){
    val disposable = getWeatherSingle(city)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            { weatherForecast->
                Log.d("MyLog","$weatherForecast")
                forecastState.value = weatherForecast
            },
            {
                Log.d("MyLog","Error $it")
            }
        )

}



