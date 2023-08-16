package com.example.weatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pp.ui.adapters.ListRecycleAdapter
import com.example.weatherapp.databinding.ActivityMainBinding
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

        viewModel.data.observe(this, Observer {
            binding.apply {
                weatherType.text = it.weather[0].main
                windSpeed.text = "${it.wind.speed} KM/H"
                cityName.text = "${it.name}\n${it.sys.country}"
                temp.text = "${it.main.temp.toInt()}°C"
                feelsLike.text = "Feels like: ${it.main.feels_like.toInt()}°C"
                minTemp.text = "Min temp: ${it.main.temp_min.toInt()}°C"
                maxTemp.text = "Max temp: ${it.main.temp_max.toInt()}°C"
                humidity.text = "${it.main.humidity} %"
                pressure.text = "${it.main.pressure} hPa"
                updatedAt.text = "Last Update : ${
                    dateFormatConverter(
                        it.dt.toLong()
                    )
                }"


            }
        })

        viewModel.resultListLiveData.observe(this,{
            prepareRecycleView(it)
        })

        if(!isNetworkAvailable())
        {
            Toast.makeText(this,"You are offline !",Toast.LENGTH_SHORT).show();
            binding.rlMainLayout.visibility = View.GONE
            viewModel.getAllData()

        }
        else {



            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

            fetchLocation()

            binding.searchView.setOnQueryTextListener(object :
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query != null) {
                        city = query
                    }
                    Log.d("check","bciudbs")
                    viewModel.getCurrentWeather(city)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }

            })

        }

    }

    private fun prepareRecycleView(it: List<WeatherData>?) {
        binding.recycleview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,true)
        val recycleAdapter = ListRecycleAdapter(it,this)
        binding.recycleview.adapter = recycleAdapter
    }


    private fun fetchLocation() {

        val task: Task<Location> = fusedLocationProviderClient.lastLocation


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101
            )
            return
        }

        task.addOnSuccessListener {
            val geocoder = Geocoder(this, Locale.getDefault())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                Log.d("print1", "reached this block")
                geocoder.getFromLocation(
                    it.latitude,
                    it.longitude,
                    1,
                    object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: MutableList<Address>) {
                            city = addresses[0].locality
                            viewModel.getCurrentWeather(city)
                        }

                    })
            } else {

                Log.d("print2", "reached this block")
                val address =
                    geocoder.getFromLocation(it.latitude, it.longitude, 1) as List<Address>

                city = address[0].locality
                viewModel.getCurrentWeather(city)
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

