package com.example.weatherapp.service

import com.example.weatherapp.model.WeatherModel
import retrofit2.Response
import retrofit2.http.GET

interface WeatherAPI {

    //https://api.openweathermap.org/data/2.5/weather?q=DELHI&appid=7a4b8a65730079f3d0f9d59587722dce
    @GET("data/2.5/weather?q=DELHI&appid=7a4b8a65730079f3d0f9d59587722dce")
    suspend fun getData() : Response<WeatherModel>
}