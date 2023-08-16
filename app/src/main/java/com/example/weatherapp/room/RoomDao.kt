package com.example.weatherapp.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.weatherapp.model.WeatherModel


@Dao
interface RoomDao {
    @Insert
    suspend fun insert(data : WeatherData)

    @Query("SELECT * from weatherData")
    fun getAll() : List<WeatherData>
}