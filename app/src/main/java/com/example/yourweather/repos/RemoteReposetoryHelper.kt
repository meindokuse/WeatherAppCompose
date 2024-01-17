package com.example.yourweather.repos

import android.util.Log
import com.example.yourweather.api.RetrofitInstance
import com.example.yourweather.models.WeatherScreen
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

object RemoteReposetoryHelper {
   private const val API = "7ea8a53b59324322a25220845240701"

    fun getWeatherWithSingle(city:String):Single<WeatherScreen>{
        return Single.create<WeatherScreen> {emitter->
            val response = RetrofitInstance.apiService.getWeatherForecast(API,city)
            try {
                if (response.isSuccessful) {
                    emitter.onSuccess(response.body()!!)
                } else {
                    emitter.onError(HttpException(response))
                }
            }catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

}
