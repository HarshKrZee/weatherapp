package com.example.weatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.room.Room
import com.example.openmapweatherapp.utils.RetrofitInstance
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.databinding.FragmentLiveWeatherBinding
import com.example.weatherapp.room.WeatherData
import com.example.weatherapp.room.WeatherDatabase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LiveWeather : Fragment() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var binding: FragmentLiveWeatherBinding
    private lateinit var database: WeatherDatabase
    private var city: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLiveWeatherBinding.inflate(inflater,container,false)   // for fragments
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val applicationContext = getActivity()?.getApplicationContext()
        database =
            applicationContext?.let {
                Room.databaseBuilder(it, WeatherDatabase::class.java, "contactDB")
                    .build()
            }!!


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())

        fetchLocation()

        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                if (query != null) {
                    city = query
                }
                getCurrentWeather(city)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

    }

    private fun fetchLocation() {

        val task: Task<Location> = fusedLocationProviderClient.lastLocation


        if (this.context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED && this.context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101
            )
            return
        }

        task.addOnSuccessListener {
            val geocoder = Geocoder(this.requireContext(), Locale.getDefault())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                Log.d("print1", "reached this block")
                geocoder.getFromLocation(
                    it.latitude,
                    it.longitude,
                    1,
                    object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: MutableList<Address>) {
                            city = addresses[0].locality
                            getCurrentWeather(city)
                        }

                    })
            } else {

                Log.d("print2", "reached this block")
                val address =
                    geocoder.getFromLocation(it.latitude, it.longitude, 1) as List<Address>

                city = address[0].locality
                getCurrentWeather(city)
            }

        }

    }

    private fun getCurrentWeather(city: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                RetrofitInstance.api.getCurrentWeather(
                    city,
                    "metric",
                    "7a4b8a65730079f3d0f9d59587722dce"
                )
            } catch (e: IOException) {
                val applicationContext = getActivity()?.getApplicationContext()
                Toast.makeText(applicationContext, "app error ${e.message}", Toast.LENGTH_SHORT)
                    .show()
                return@launch
            } catch (e: HttpException) {
                val applicationContext = getActivity()?.getApplicationContext()
                Toast.makeText(applicationContext, "http error ${e.message}", Toast.LENGTH_SHORT)
                    .show()
                return@launch
            }

            if (response.isSuccessful && response.body() != null) {
                withContext(Dispatchers.Main) {

                    val data = response.body()!!

                    database.roomDao().insert(
                        WeatherData(
                            0,
                            response.body()!!.name,
                            response.body()!!.dt,
                            response.body()!!.main.temp
                        )
                    )

                    val iconId = data.weather[0].icon

                    val imgUrl = "https://openweathermap.org/img/wn/$iconId@4x.png"

                    Picasso.get().load(imgUrl).into(binding.weatherIcon)


                    binding.sunset.text =
                        dateFormatConverter(
                            data.sys.sunset.toLong()
                        )

//                    binding.tvSunrise.text =
//                        dateFormatConverter(
//                            data.sys.sunrise.toLong()
//                        )

                    binding.apply {
                        weatherType.text = data.weather[0].main
                        windSpeed.text = "${data.wind.speed} KM/H"
                        cityName.text = "${data.name}\n${data.sys.country}"
                        temp.text = "${data.main.temp.toInt()}°C"
                        feelsLike.text = "Feels like: ${data.main.feels_like.toInt()}°C"
                        minTemp.text = "Min temp: ${data.main.temp_min.toInt()}°C"
                        maxTemp.text = "Max temp: ${data.main.temp_max.toInt()}°C"
                        humidity.text = "${data.main.humidity} %"
                        pressure.text = "${data.main.pressure} hPa"
                        updatedAt.text = "Last Update : ${
                            dateFormatConverter(
                                data.dt.toLong()
                            )
                        }"


                    }

                }
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
