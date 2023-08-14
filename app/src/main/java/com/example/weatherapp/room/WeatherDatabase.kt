package com.example.weatherapp.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WeatherData::class],version=1)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun roomDao() : RoomDao

}