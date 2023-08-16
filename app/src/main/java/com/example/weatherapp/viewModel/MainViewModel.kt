package com.example.weatherapp.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.Room
import com.example.weatherapp.model.Weather
import com.example.weatherapp.model.WeatherModel
import com.example.weatherapp.room.RoomDao
import com.example.weatherapp.room.WeatherData
import com.example.weatherapp.room.WeatherDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class MainViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var weatherData : List<WeatherData>
    lateinit var database : WeatherDatabase
    lateinit var dao : RoomDao
    lateinit var roomDb : WeatherDatabase
//    private val context = getApplication<Application>().applicationContext
    init {

        database =
            Room.databaseBuilder(getApplication<Application>().applicationContext, WeatherDatabase::class.java, "contactDB")
                .build()

        weatherData = database.roomDao().getAll()
        Log.d("weatherdata",weatherData.toString())

//        getDataFromApi()

    }

    fun getData() : List<WeatherData> { return weatherData}



    private fun getDataFromApi() {

    }
}