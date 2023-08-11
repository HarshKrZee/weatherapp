package com.example.weatherapp.Utilities

import com.example.weatherapp.POJO.WeatherData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiEndPoint {

    @GET("weather")
    fun getCurrentWeatherData(
        @Query("lat") latitude : String,
        @Query("lon") longitude : String,
        @Query("appid") api_key : String
    ) : Call<WeatherData>

    @GET("weather")
    fun getCityWeatherData(
        @Query("q") city : String,
        @Query("appid") api_key : String
    ) : Call<WeatherData>
}