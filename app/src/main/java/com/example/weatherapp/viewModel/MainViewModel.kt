package com.example.weatherapp.viewModel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.Room
import com.example.openmapweatherapp.utils.RetrofitInstance
import com.example.weatherapp.model.Weather
import com.example.weatherapp.model.WeatherModel
import com.example.weatherapp.room.RoomDao
import com.example.weatherapp.room.WeatherData
import com.example.weatherapp.room.WeatherDatabase
import com.example.weatherapp.room.WeatherRepository
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import java.io.IOException

class MainViewModel(private val repository: WeatherRepository) : ViewModel() {

    lateinit var weatherData : List<WeatherData>
    lateinit var database : WeatherDatabase
    lateinit var dao : RoomDao
    lateinit var roomDb : WeatherDatabase
//    private val context = getApplication<Application>().applicationContext

    private lateinit var result:WeatherModel
    private lateinit var resultList:List<WeatherData>
    var resultListLiveData = MutableLiveData<List<WeatherData>>()


    var data = MutableLiveData<WeatherModel>()
    init {

    }

     fun getCurrentWeather(city: String) {
         Log.d("check","Api CAll")
        GlobalScope.launch(Dispatchers.IO) {
            try{
                val response = RetrofitInstance.api.getCurrentWeather(city, "metric", "7a4b8a65730079f3d0f9d59587722dce")
                response?.body()?.let{
                    result = it
                    data.postValue(result)
                    repository.insert(
                        WeatherData(
                            0,
                            it.name,
                            it.dt,
                            it.main.temp
                        )
                    )
                }
            }
            catch (e: IOException) {
                Log.d("error",e.toString())
            }
        }
    }

    fun getAllData()
    {
        GlobalScope.launch(Dispatchers.IO){
            try{
                resultList = repository.getAll()
                resultListLiveData.postValue(resultList)
            }
            catch(e : Exception)
            {
                Log.d("error",e.toString())
            }
        }

    }
}