package com.example.yourweather.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.yourweather.models.OtherLocations
import com.example.yourweather.models.WeatherForecast

@Dao
interface LocationsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNewLocation(otherLocations: OtherLocations)

    @Delete(entity = OtherLocations::class)
    suspend fun deleteLocation(vararg otherLocations: OtherLocations)

    @Query("SELECT * FROM other_locations")
    suspend fun getAllLocation ():List<OtherLocations>

}