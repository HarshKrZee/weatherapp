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
import android.view.View.GONE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.openmapweatherapp.utils.RetrofitInstance
import com.example.pp.ui.adapters.ListRecycleAdapter
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.room.WeatherData
import com.example.weatherapp.room.WeatherDatabase
import com.example.weatherapp.viewModel.MainViewModel
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


class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: WeatherDatabase
    private var city: String = ""


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

//        binding.recycleview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,true)
//        val recycleAdapter = ListRecycleAdapter(results,context)
//        binding.recycleview.adapter = recycleAdapter

        if(!isNetworkAvailable())
        {
//            binding.rlMainLayout.visibility = View.GONE

        }
        else {

            database =
                Room.databaseBuilder(applicationContext, WeatherDatabase::class.java, "contactDB")
                    .build()

//            var viewModel : MainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
//
//            viewModel.getData().observe(this,{
//                prepareRecyclerView(it)
//            })



            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

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

//        getCurrentWeather(city)
//
//        binding.cityName.setOnClickListener {
//        }

//        val ai: ApplicationInfo = applicationContext.packageManager
//            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
//        val value = ai.metaData["AIzaSyCDqrzcWS7nYzfk_mMNDJFjseuyk2AgnZI"]
//        val apiKey = value.toString()
//
//        if (!Places.isInitialized()) {
//            Places.initialize(applicationContext, apiKey)
//        }
//
//        val autocompleteSupportFragment1 = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment1) as AutocompleteSupportFragment?
//
//        autocompleteSupportFragment1!!.setPlaceFields(
//            listOf(
//
//                Place.Field.NAME,
//                Place.Field.ADDRESS,
//                Place.Field.PHONE_NUMBER,
//                Place.Field.LAT_LNG,
//                Place.Field.OPENING_HOURS,
//                Place.Field.RATING,
//                Place.Field.USER_RATINGS_TOTAL
//
//            )
//        )
//
//        autocompleteSupportFragment1.setOnPlaceSelectedListener(object : PlaceSelectionListener {
//            override fun onPlaceSelected(place: Place) {
//
//                // Text view where we will
//                // append the information that we fetch
//                val textView = findViewById<TextView>(R.id.tv1)
//
//                // Information about the place
//                val name = place.name
//                val address = place.address
//                val phone = place.phoneNumber.toString()
//                val latlng = place.latLng
//                val latitude = latlng?.latitude
//                val longitude = latlng?.longitude
//
//                val isOpenStatus : String = if(place.isOpen == true){
//                    "Open"
//                } else {
//                    "Closed"
//                }
//
//                val rating = place.rating
//                val userRatings = place.userRatingsTotal
//
//                textView.text = "Name: $name \nAddress: $address \nPhone Number: $phone \n" +
//                        "Latitude, Longitude: $latitude , $longitude \nIs open: $isOpenStatus \n" +
//                        "Rating: $rating \nUser ratings: $userRatings"
//            }
//
//            override fun onError(status: Status) {
//                Toast.makeText(applicationContext,"Some error occurred", Toast.LENGTH_SHORT).show()
//            }
//        })

        }

    }

//    fun prepareRecyclerView(results : List<WeatherData>?)
//    {
//        binding.recycleview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,true)
//        val recycleAdapter = ListRecycleAdapter(results,this)
//        binding.recycleview.adapter = recycleAdapter
//    }

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
                Toast.makeText(applicationContext, "app error ${e.message}", Toast.LENGTH_SHORT)
                    .show()
                return@launch
            } catch (e: HttpException) {
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


//                    val weatherData = database.roomDao().getAll()
//
//                    binding.recycleview.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL,true)
//                    val recycleAdapter = ListRecycleAdapter(weatherData,applicationContext)
//                    binding.recycleview.adapter = recycleAdapter

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

