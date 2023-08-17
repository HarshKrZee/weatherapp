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
import com.example.weatherapp.model.Item
import com.example.weatherapp.model.LocalLocation
import com.example.weatherapp.model.Weather
import com.example.weatherapp.model.WeatherModel
import com.example.weatherapp.model.interfaces.RetrofitHelper2
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
    private lateinit var result2 : ArrayList<LocalLocation>
    var resultSuggestionLiveData = MutableLiveData<List<Item>>()
    var isOnline:Boolean = true


    var data = MutableLiveData<WeatherModel>()
    init {

    }

     fun getCurrentWeather(lat: String,lon: String) {
         Log.d("check","Api CAll")

         val key = "7a4b8a65730079f3d0f9d59587722dce"
         val endpoint = "weather?lat=${lat}&lon=${lon}&appid=${key}"

        GlobalScope.launch(Dispatchers.IO) {
            try{
                val response = RetrofitInstance.api.getCurrentWeather(endpoint)
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


    fun getCityWeather(city: String) {

        val key = "7a4b8a65730079f3d0f9d59587722dce"
        val endpoint = "weather?q=${city}&appid=${key}"

        GlobalScope.launch(Dispatchers.IO) {
            try{
                val response = RetrofitInstance.api.getCurrentWeather(endpoint)
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

    fun getCitySuggestionFromApi(pattern:String){
        Log.d("suggestion call","suggestions")
        val key = "fKzVotCk8CmC2teZ3QEn0f9XoL_sZAV585wgTMKFoao"
        var endPoint = "autocomplete?q=$pattern&apiKey=$key"

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val apiData = RetrofitHelper2.api.getCity(endPoint)
                apiData?.body()?.let {
                    val items = apiData.body()?.items
                    items?.let {
//                        val labels:ArrayList<LocalLocation> = ArrayList()
//                        for (i in items){
//                            labels.add(LocalLocation(i.address.label,i.address.city))
//                        }
//                        result2 = labels
                        Log.d("API Response",it.toString())
                        resultSuggestionLiveData.postValue(it)
                    }
                }
            } catch (e: Exception) {
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