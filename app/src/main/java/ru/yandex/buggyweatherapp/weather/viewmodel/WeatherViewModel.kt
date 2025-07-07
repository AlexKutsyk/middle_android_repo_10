package ru.yandex.buggyweatherapp.weather.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.yandex.buggyweatherapp.model.Location
import ru.yandex.buggyweatherapp.model.WeatherData
import ru.yandex.buggyweatherapp.repository.LocationRepository
import ru.yandex.buggyweatherapp.weather.data.dto.Request
import ru.yandex.buggyweatherapp.weather.data.dto.ResponseCode
import ru.yandex.buggyweatherapp.weather.domain.api.WeatherInteractor
import java.util.Timer
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherInteractor: WeatherInteractor
) : ViewModel() {

    private lateinit var activityContext: Context

//    private val weatherRepository = WeatherRepository()
    private val locationRepository by lazy {
        LocationRepository(activityContext)
    }

    val weatherData = MutableLiveData<WeatherData>()
    val currentLocation = MutableLiveData<Location>()
    val isLoading = MutableLiveData<Boolean>()
    val error = MutableLiveData<String>()
    val cityName = MutableLiveData<String>()


    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())


    private var refreshTimer: Timer? = null


    fun initialize(context: Context) {
        this.activityContext = context
        fetchCurrentLocationWeather()


        startAutoRefresh()
    }


    fun fetchCurrentLocationWeather() {
        isLoading.value = true
        error.value = null
        
        locationRepository.getCurrentLocation { location ->
            if (location != null) {
                currentLocation.value = location
                
                
                val cityNameFromLocation = locationRepository.getCityNameFromLocation(location)
                cityName.value = cityNameFromLocation
                
                getWeatherForLocation(location)
            } else {
                isLoading.value = false
                error.value = "Unable to get current location"
            }
        }
    }

    fun getWeatherForLocation(location: Location) {
        isLoading.value = true
        error.value = null
        
        weatherRepository.getWeatherData(location) { data, exception ->
            
            Handler(Looper.getMainLooper()).post {
                isLoading.value = false
                
                if (data != null) {
                    weatherData.value = data
                } else {
                    error.value = exception?.message ?: "Unknown error"
                }
            }
        }
    }

    fun searchWeatherByCity(city: String) {
        if (city.isBlank()) {
            error.value = "City name cannot be empty"
            return
        }

        isLoading.value = true
        error.value = null


        /*weatherRepository.getWeatherByCity(city) { data, exception ->
            

        }*/
        Log.i("alex", "VM / Request - $city")
        viewModelScope.launch {
            weatherInteractor.getWeatherByCity(Request.WeatherByCity(city)).collect { result ->
                Log.i("alex", "VM / Result - $result")

                handleWeatherDataResult(result)
            }

        }
    }

    private fun handleWeatherDataResult(result: Pair<WeatherData?, Int?>) {
        isLoading.value = false

        if (result.first != null) {
            weatherData.value = result.first
            cityName.value = result.first?.cityName
            currentLocation.value = Location(0.0, 0.0, result.first?.cityName)
        } else {
            handleError(result.second)
            error.value = handleError(result.second)
        }
    }

    private fun handleError(code: Int?): String {
        return when (code) {
            ResponseCode.ServerFailed.code -> {
                "Server error"
            }

            ResponseCode.ConnectionFailed.code -> {
                "Connection error"
            }

            else -> {
                "Unknown error"
            }
        }
    }

    fun formatTemperature(temp: Double): String {
        return "${temp.toInt()}°C"
    }
    
    
    fun loadWeatherIcon(iconCode: String) {
        coroutineScope.launch {
            val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
            ImageLoader.loadImage(iconUrl)
        }
    }


    private fun startAutoRefresh() {
        refreshTimer = Timer()
        refreshTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                currentLocation.value?.let { location ->
                    getWeatherForLocation(location)
                }
            }
        }, 60000, 60000)
    }


    fun toggleFavorite() {
        weatherData.value?.let {
            it.isFavorite = !it.isFavorite

            weatherData.value = it
        }
    }


    override fun onCleared() {
        super.onCleared()

    }
}