package com.example.weatherapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.viewModel.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
//        supportActionBar?.hide()

//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
////        binding.rlMainLayout.visibility = View.GONE
//
//        getCurrentLocation()

        val viewModel : MainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

    }

//    private fun getCurrentLocation() {
//        if (checkPermissions()) {
//            if (isLocationEnabled()) {
//
//                // if permission is given and location is enabled as well
//
//                if (ActivityCompat.checkSelfPermission(
//                        this,
//                        Manifest.permission.ACCESS_FINE_LOCATION
//                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                        this,
//                        Manifest.permission.ACCESS_COARSE_LOCATION
//                    ) != PackageManager.PERMISSION_GRANTED
//                ) {
//                    requestPermission()
//                    return
//                }
//
//                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
//                    val location: Location? = task.result
//                    if (location == null) {
//                        Toast.makeText(this, "Null Recieved", Toast.LENGTH_SHORT).show()
//                    } else {
//
//                        fetchCurrentLocationWeather(
//                            location.latitude.toString(),
//                            location.longitude.toString()
//                        )
//                    }
//                }
//
//            } else {
//                //open setting to turn on location
//
//                Toast.makeText(this, "Turn on location", Toast.LENGTH_SHORT).show()
//                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                startActivity(intent)
//            }
//        } else {
//            // request permission
//
//            requestPermission()
//        }
//    }
//
//    private fun fetchCurrentLocationWeather(latitude: String, longitude: String) {
//
////        binding.pbLoading.visibility = View.VISIBLE
////        val result =
////            RetrofitHelper.api.getCurrentWeatherData(latitude, longitude, api_key).enqueue(object :
////                Callback<WeatherData> {
////                @RequiresApi(Build.VERSION_CODES.O)
////                override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
////
////                    if(response.isSuccessful)
////                    {
////                        Log.d("response",response.toString())
////                        setDataOnViews(response.body())
////                    }
////                }
////
////                override fun onFailure(call: Call<WeatherData>, t: Throwable) {
////                    Toast.makeText(applicationContext,"ERROR",Toast.LENGTH_SHORT).show()
////                }
////            })
//
//
//    }
//
////    @RequiresApi(Build.VERSION_CODES.O)
////    private fun setDataOnViews(body: WeatherData?) {
////        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm")
////        val currentDate = sdf.format(Date())
////        binding.dateTime.text = currentDate
////        Log.d("date",currentDate)
////        binding.maxTemp.text = "Day " + kelvinToCelcius(body!!.main.temp_max) +"°"
////        binding.minTemp.text = "Day " + kelvinToCelcius(body!!.main.temp_min) +"°"
////
////        binding.temp.text =  ""+kelvinToCelcius(body!!.main.temp)+"°"
////        binding.feelsLike.text = ""+kelvinToCelcius(body!!.main.feels_like)+"°"
////        binding.weatherType.text = body.weather[0].main
////        binding.sunset.text = timeStampToLocalDate(body.sys.sunset.toLong())
////        binding.pressure.text = body.main.pressure.toString()
////        binding.humidity.text = body.main.humidity.toString()+"%"
////        binding.windSpeed.text = body.wind.speed.toString()+"m/s"
////
////
////    }
//
////    @RequiresApi(Build.VERSION_CODES.O)
////    private fun timeStampToLocalDate(timeStamp : Long) : String {
////        val localTime = timeStamp.let {
////            Instant.ofEpochSecond(it).atZone(ZoneId.systemDefault()).toLocalTime()
////        }
////
////        return localTime.toString()
////    }
////
////    private fun kelvinToCelcius(tempMax: Double): Any? {
////        var intTemp = tempMax
////        intTemp = intTemp.minus(273)
////        return intTemp.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
////    }
//
//
//    private fun isLocationEnabled(): Boolean {
//        val locationManager: LocationManager =
//            getSystemService(Context.LOCATION_SERVICE) as LocationManager
//
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
//            LocationManager.NETWORK_PROVIDER
//        )
//    }
//
//
//    private fun requestPermission() {
//        ActivityCompat.requestPermissions(
//            this,
//            arrayOf(
//                android.Manifest.permission.ACCESS_FINE_LOCATION,
//                android.Manifest.permission.ACCESS_COARSE_LOCATION
//            ),
//            PERMISSION_REQUEST_ACCESS_LOCATION
//        )
//    }
//
//    companion object {
//        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
//        const val api_key = "7a4b8a65730079f3d0f9d59587722dce"
//    }
//
//    private fun checkPermissions(): Boolean {
//
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                android.Manifest.permission.ACCESS_COARSE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED &&
//            ActivityCompat.checkSelfPermission(
//                this,
//                android.Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            return true
//        }
//
//        return false
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(applicationContext, "Granted", Toast.LENGTH_SHORT).show()
//                getCurrentLocation()
//            } else {
//                Toast.makeText(applicationContext, "Denied", Toast.LENGTH_SHORT).show()
//            }
//
//        }
//    }
}