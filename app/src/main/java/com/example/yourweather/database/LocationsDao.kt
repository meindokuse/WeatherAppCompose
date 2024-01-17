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

    @Query("DELETE FROM other_locations WHERE location = :location")
    suspend fun deleteLocation(location: String)

    @Query("SELECT * FROM other_locations")
    suspend fun getAllLocation ():List<OtherLocations>

}