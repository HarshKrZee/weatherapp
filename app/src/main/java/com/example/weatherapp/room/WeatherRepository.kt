package com.example.weatherapp.room


class WeatherRepository(private val weatherDatabase: WeatherDatabase) {
    suspend fun getAll():List<WeatherData> = weatherDatabase.roomDao().getAll()
    suspend fun insert(weatherData:WeatherData) = weatherDatabase.roomDao().insert(weatherData)
}