package com.example.weatherapp.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "weatherData")
data class WeatherData(
    @PrimaryKey (autoGenerate = true)val id : Int,
    @ColumnInfo(name = "City") val name : String?,
    @ColumnInfo(name = "UpdatedAt") val UpdateAt: Int,
    @ColumnInfo(name="Temp") val Temperature: Double
)
