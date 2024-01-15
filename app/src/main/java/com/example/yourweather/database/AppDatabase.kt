package com.example.yourweather.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.yourweather.models.ForecastListConverter
import com.example.yourweather.models.HourListConverter
import com.example.yourweather.models.OtherLocations
import com.example.yourweather.models.WeatherForecast

@Database(entities = [WeatherForecast::class,OtherLocations::class], version = 3, exportSchema = false)
@TypeConverters(ForecastListConverter::class,HourListConverter::class)
abstract class AppDatabase:RoomDatabase(){

    abstract fun weatherDao(): WeatherDao

    abstract fun locationDao(): LocationsDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "AppInfo"
            ).build().also { INSTANCE = it }
        }
    }
}