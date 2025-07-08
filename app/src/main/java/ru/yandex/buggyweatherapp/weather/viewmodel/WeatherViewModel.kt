package ru.yandex.buggyweatherapp.weather.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.yandex.buggyweatherapp.location.domain.api.LocationInteractor
import ru.yandex.buggyweatherapp.model.Location
import ru.yandex.buggyweatherapp.model.WeatherData
import ru.yandex.buggyweatherapp.weather.data.dto.Request
import ru.yandex.buggyweatherapp.weather.data.dto.ResponseCode
import ru.yandex.buggyweatherapp.weather.domain.api.WeatherInteractor
import java.util.Timer
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherInteractor: WeatherInteractor,
    private val locationInteractor: LocationInteractor
) : ViewModel() {

//    private lateinit var activityContext: Context

//    private val weatherRepository = WeatherRepository()
//    private val locationRepository by lazy {
//        LocationRepository(activityContext)
//    }

    var weatherData = MutableLiveData<WeatherData>()
        private set
    var currentLocation = MutableLiveData<Location>()
        private set
    var isLoading = MutableLiveData<Boolean>()
        private set
    var error = MutableLiveData<String?>()
        private set
    var cityName = MutableLiveData<String?>()
        private set

    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    private var refreshTimer: Timer? = null

    init {
        fetchCurrentLocationWeather()
        startAutoRefresh()
    }

    fun fetchCurrentLocationWeather() {
        isLoading.value = true
        error.value = null

        viewModelScope.launch {
            locationInteractor.getCurrentLocation().collect { location ->
                handleLocationResult(location)
            }
        }
    }

    fun getWeatherForLocation(location: Location) {
        isLoading.value = true
        error.value = null

        viewModelScope.launch {
            weatherInteractor.getCurrentWeather(
                Request.CurrentWeather(
                    location.latitude,
                    location.longitude
                )
            ).collect { result ->
                handleWeatherDataResult(result)
//                Handler(Looper.getMainLooper()).post {
//                    isLoading.value = false
//
//                    if (data != null) {
//                        weatherData.value = data
//                    } else {
//                        error.value = exception?.message ?: "Unknown error"
//                    }
//                }
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
            currentLocation.value = currentLocation.value?.copy(
                name = result.first?.cityName
            )
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

    private suspend fun handleLocationResult(location: Location?) {
        if (location != null) {
            currentLocation.value = location
            cityName.value = locationInteractor.getCityNameFromLocation(location)
            getWeatherForLocation(location)
        } else {
            isLoading.value = false
            error.value = "Unable to get current location"
        }
    }

    fun formatTemperature(temp: Double): String {
        return "${temp.toInt()}°C"
    }


    /*fun loadWeatherIcon(iconCode: String) {
        coroutineScope.launch {
            val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
            ImageLoader.loadImage(iconUrl)
        }
    }*/


    private fun startAutoRefresh() {
//        refreshTimer = Timer()
//        refreshTimer?.scheduleAtFixedRate(object : TimerTask() {
//            override fun run() {
//                currentLocation.value?.let { location ->
//                    getWeatherForLocation(location)
//                }
//            }
//        }, 60000, 60000)
        viewModelScope.launch {
            while (true) {
                currentLocation.value?.let { location ->
                    weatherInteractor.getCurrentWeather(
                        Request.CurrentWeather(
                            location.latitude,
                            location.longitude
                        )
                    ).collect { result ->
                        handleWeatherDataResult(result)
                    }
                }
                Log.i("alex", "getCurrentWeather - ${currentLocation.value}")
                delay(60000)
            }
        }
    }


    fun toggleFavorite() {
        weatherData.value?.let {
            it.isFavorite = !it.isFavorite
            weatherData.value = it
        }
    }
}