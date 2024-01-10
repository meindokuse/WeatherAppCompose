package com.example.yourweather


import com.example.yourweather.models.ForecastDay
import com.example.yourweather.models.WeatherForecast
import com.google.gson.annotations.SerializedName
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {


    @GET("forecast.json")
    fun getListHoursWeather(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("days") d:Int = 5,
        @Query("aqi") includeAqi: String = "no",
        @Query("alerts") alerts: String = "no"
    ):Single<Response<WeatherForecast>>
}

