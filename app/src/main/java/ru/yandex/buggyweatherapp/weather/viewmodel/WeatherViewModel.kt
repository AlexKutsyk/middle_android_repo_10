package ru.yandex.buggyweatherapp.weather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.yandex.buggyweatherapp.location.domain.api.LocationInteractor
import ru.yandex.buggyweatherapp.location.domain.models.Location
import ru.yandex.buggyweatherapp.model.WeatherData
import ru.yandex.buggyweatherapp.weather.data.dto.Request
import ru.yandex.buggyweatherapp.weather.domain.api.WeatherInteractor
import ru.yandex.buggyweatherapp.weather.domain.models.RequestError
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherInteractor: WeatherInteractor,
    private val locationInteractor: LocationInteractor
) : ViewModel() {

    var weatherData = MutableLiveData<WeatherData>()
        private set
    var currentLocation = MutableLiveData<Location?>()
        private set
    var isLoading = MutableLiveData<Boolean>()
        private set
    var error = MutableLiveData<RequestError?>()
        private set
    var cityName = MutableLiveData<String?>()
        private set

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

        viewModelScope.launch(Dispatchers.Main) {
            weatherInteractor.getCurrentWeather(
                Request.CurrentWeather(
                    location.latitude,
                    location.longitude
                )
            ).collect { result ->
                handleWeatherDataResult(result)
            }
        }
    }

    fun searchWeatherByCity(city: String) {
        if (city.isBlank()) {
            error.value = RequestError.EmptyRequest
            return
        }

        isLoading.value = true
        error.value = null

        viewModelScope.launch(Dispatchers.Main) {
            weatherInteractor.getWeatherByCity(Request.WeatherByCity(city)).collect { result ->
                handleWeatherDataResult(result)
            }
        }
    }

    private fun handleWeatherDataResult(result: Pair<WeatherData?, RequestError?>) {
        isLoading.value = false

        if (result.first != null) {
            weatherData.value = result.first
            cityName.value = result.first?.cityName
            currentLocation.value = currentLocation.value?.copy(
                name = result.first?.cityName
            )
        } else {
            error.value = result.second
        }
    }

    private suspend fun handleLocationResult(location: Location?) {
        if (location != null) {
            currentLocation.value = location
            cityName.value = locationInteractor.getCityNameFromLocation(location)
            getWeatherForLocation(location)
        } else {
            isLoading.value = false
            error.value = RequestError.UnableLocation
        }
    }

    fun startAutoRefresh() {
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
                delay(DELAY_TIMER)
            }
        }
    }

    fun toggleFavorite() {
        weatherData.value?.let {
            weatherData.value = it.copy(
                isFavorite = !it.isFavorite
            )
        }
    }

    companion object {
        private const val DELAY_TIMER = 60_000L
    }
}