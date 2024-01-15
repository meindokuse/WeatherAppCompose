package com.example.yourweather.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


@Entity(tableName = "last_weather")
data class WeatherForecast(
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0,
    val location: String,
    @Embedded
    val current: Current,
    @Embedded
    val forecast: Forecast
)


data class Current(
    val temp_c: Double,
    val last_updated_epoch: Long,
    val uv : Float,
    val gust_kph:Float,
    val humidity:Int
)

data class Forecast(
    @TypeConverters(ForecastListConverter::class)
    val forecastday: List<ForecastDay>
)

data class ForecastDay(
    val date_epoch: Long,
    @Embedded
    val condition: Condition,
    @TypeConverters(HourListConverter::class)
    val hour: List<Hour>,
    @Embedded
    val day: Day
)

data class Hour(
    val time_epoch: Long,
    val temp_c: Double,
    @Embedded
    val condition: Condition
)

data class Condition(
    val text: String,
    val icon: String,
    val code: Int
)

data class Day(
    val maxtemp_c: Float,
    val mintemp_c: Float,
    val avghumidity: Int,
    @Embedded
    val condition: Condition
)

class ForecastListConverter {
    @TypeConverter
    fun fromString(value: String): List<ForecastDay> {
        val listType = object : TypeToken<List<ForecastDay>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<ForecastDay>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}
@Entity(tableName = "other_locations")
data class OtherLocations(
    @PrimaryKey(autoGenerate = true)
    val id:Long=0,
    @ColumnInfo(defaultValue = "Строитель")
    val location:String
)


class HourListConverter {
    @TypeConverter
    fun fromString(value: String): List<Hour> {
        val listType = object : TypeToken<List<Hour>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<Hour>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}
