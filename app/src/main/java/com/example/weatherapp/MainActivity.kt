package com.example.weatherapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import android.provider.Settings
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pp.ui.adapters.ListRecycleAdapter
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.model.Item
import com.example.weatherapp.room.WeatherData
import com.example.weatherapp.room.WeatherDatabase
import com.example.weatherapp.room.WeatherRepository
import com.example.weatherapp.viewModel.MainViewModel
import com.example.weatherapp.viewModel.WeatherViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var weatherDatabase: WeatherDatabase
    private lateinit var repository: WeatherRepository
    private lateinit var factory: WeatherViewModelFactory
    private var city:String=""
    var isConnected = true

    // to check if we are monitoring Network
    private val monitoringConnectivity = false


    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        weatherDatabase = WeatherDatabase(this)
        repository = WeatherRepository(weatherDatabase)
        factory = WeatherViewModelFactory(repository)
        viewModel = ViewModelProvider(this,factory).get(MainViewModel::class.java)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        checkLocationPermissions()
        getCurrentLocation()

        viewModel.data.observe(this, Observer {
            binding.apply {
                weatherType.text = it.weather[0].main
                windSpeed.text = "${it.wind.speed} KM/H"
                cityName.text = "${it.name}\n${it.sys.country}"
                temp.text = "${it.main.temp.toInt()}°F"
                feelsLike.text = "Feels like: ${it.main.feels_like.toInt()}°F"
                minTemp.text = "Min temp: ${it.main.temp_min.toInt()}°F"
                maxTemp.text = "Max temp: ${it.main.temp_max.toInt()}°F"
                humidity.text = "${it.main.humidity} %"
                pressure.text = "${it.main.pressure} hPa"
                updatedAt.text = "Last Update : ${
                    dateFormatConverter(
                        it.dt.toLong()
                    )
                }"


            }
        })


        viewModel.resultSuggestionLiveData.observe(this,{
            prepareRecycleView(it)
        })

        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    city = query
                }
                Log.d("check","bciudbs")
                viewModel.getCityWeather(city)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.getCitySuggestionFromApi(newText)
                return true
            }

        })

    }

    private fun prepareRecycleView(it: List<Item>) {
        binding.recycleview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,true)
        val recycleAdapter = ListRecycleAdapter(it,viewModel,binding)
        binding.recycleview.adapter = recycleAdapter
    }

    private fun getCurrentLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("check","Not yet Started")
            return
        }
        Log.d("check","Started")
        fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){ task ->
            val location = task.result
            if(location==null){
                Toast.makeText(this,"Null Received", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this,"Get Success", Toast.LENGTH_SHORT).show()
                viewModel.getCurrentWeather(location.latitude.toString(),location.longitude.toString())
            }
        }
    }

    private fun checkLocationPermissions(){
        if(!isLocationEnabled()){
            Toast.makeText(this,"Turn on Location", Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
        if(!checkPermissions()) requestPermission()
    }

    private fun isLocationEnabled():Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isLocationEnabled
    }
    private fun checkPermissions():Boolean{
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    companion object{
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 200
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_ACCESS_LOCATION)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

private fun dateFormatConverter(date: Long): String {

    return SimpleDateFormat(
        "hh:mm a",
        Locale.ENGLISH
    ).format(Date(date * 1000))
}

