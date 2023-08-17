package com.example.weatherapp.model.interfaces

import com.example.openmapweatherapp.data.ApiInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper2 {
    private val baseUrl = "https://autocomplete.search.hereapi.com/v1/"
    val api: ApiInterface by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }
}