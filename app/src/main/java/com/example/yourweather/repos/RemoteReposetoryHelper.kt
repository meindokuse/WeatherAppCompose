package com.example.yourweather.repos

import com.example.yourweather.api.RetrofitInstance
import com.example.yourweather.models.WeatherScreen
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException

object RemoteReposеtoryHelper {
    private const val API = "4a3693035f3741978f8112822242101"

    fun getWeatherWithSingle(city: String): Single<WeatherScreen> {
        return RetrofitInstance.apiService.getWeatherForecast(API, city)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { response ->
                if (response.isSuccessful) {
                    Single.just(response.body()!!)
                } else {
                    Single.error(HttpException(response))
                }
            }
    }
}
