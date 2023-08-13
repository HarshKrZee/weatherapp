package com.example.weatherapp.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.model.WeatherModel
import com.example.weatherapp.service.RetrofitHelper
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class MainViewModel : ViewModel() {

    init {
        getDataFromApi()
    }

    val weatherData = MutableLiveData<WeatherModel>()
    val weatherError = MutableLiveData<Boolean>()
    val weatherLoad = MutableLiveData<Boolean>()


    private fun getDataFromApi() {


        GlobalScope.launch(Dispatchers.IO) {
            val result = RetrofitHelper.api.getData()
            Log.d("APIResponse1", result?.body().toString())

        }

    }
}