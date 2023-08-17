package com.example.openmapweatherapp.data

import com.example.weatherapp.model.CityList
import com.example.weatherapp.model.WeatherModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiInterface {

    @GET
    suspend fun getCurrentWeather(
        @Url url: String
    ): Response<WeatherModel>

    @GET
    suspend fun getCity(@Url url:String) : Response<CityList>

}