package com.example.yourweather.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

abstract class AppDatabase:RoomDatabase(){

    abstract fun weatherDao(): WeatherDao

    companion object{
        @Volatile
        private var INSTANSE: AppDatabase? = null

        fun getInstance(context: Context):AppDatabase{
            return INSTANSE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "last_weather"
                ).build()
                INSTANSE = instance
                instance
            }
        }
    }
}