package com.example.weather_project

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import com.example.weather_project.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.SimpleTimeZone

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Thai nguyen")
        SearchCity()
    }

    private fun SearchCity() {
        val search = binding.searchView
        search.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }


    private fun fetchWeatherData(cityname : String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(cityname,"5ca78d3065eae2fca07ff552d3a71d79","metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody=response.body()
                if(response.isSuccessful && responseBody != null){
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windS = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seale = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknow"
                    var maxT = responseBody.main.temp_max
                    var minT = responseBody.main.temp_min
                    binding.temp.text="$temperature °C"
                    binding.weather.text=condition
                    binding.maxTemp.text="Max : $maxT °C"
                    binding.minTemp.text="Min : $minT °C"
                    binding.SunRise.text="${time(sunRise)}"
                    binding.sunset.text="${time(sunSet)}"
                    binding.sea.text="$seale hPa"
                    binding.humidity.text="$humidity %"
                    binding.windSpeed.text="$windS m/s"
                    binding.condition.text=condition
                    binding.day.text=dayName(System.currentTimeMillis())
                        binding.date.text=date()
                        binding.cityName.text="$cityname"
                    Log.d("TAG", "onResponse: $temperature")
                    chageImg(condition)
                }
            }

           

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun chageImg(conditionS :String) {
        when(conditionS){
            "Clear Sky","Sunny","Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Partly Clouds","Clouds","Overcast","Mist","Foggy"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): CharSequence? {
        val  sdf = SimpleDateFormat("dd MMMM yyyy",Locale.getDefault())
        return sdf.format((Date()))
    }
    private fun time(timestamp: Long): String {
        val  sdf = SimpleDateFormat("HH:mm",Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }
    fun dayName(timestamp : Long): String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }

}


