package ru.yandex.buggyweatherapp.weather.data

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.yandex.buggyweatherapp.weather.data.api.NetworkClient
import ru.yandex.buggyweatherapp.weather.data.api.WeatherApiService
import ru.yandex.buggyweatherapp.weather.data.dto.Request
import ru.yandex.buggyweatherapp.weather.data.dto.Response
import ru.yandex.buggyweatherapp.weather.data.dto.ResponseCode
import ru.yandex.buggyweatherapp.weather.data.dto.WeatherDataDto
import javax.inject.Inject

class WeatherNetworkClient @Inject constructor(
    val connectivityManager: ConnectivityManager,
    val weatherApiService: WeatherApiService
) : NetworkClient {
    override suspend fun doRequest(request: Request): Response {
        if (!isConnected()) {
            return Response().apply {
                resultCode = ResponseCode.ConnectionFailed.code
            }
        }

        return when (request) {
            is Request.CurrentWeather -> {
                withContext(Dispatchers.IO) {
                    try {
                        val response = weatherApiService.getCurrentWeather(
                            request.latitude,
                            request.longitude
                        )
                        handleResponse(response)
                    } catch (e: Throwable) {
                        Response().apply { resultCode = ResponseCode.Other.code }
                    }

                }
            }

            is Request.WeatherByCity -> {
                withContext(Dispatchers.IO) {
                    try {
                        val response = weatherApiService.getWeatherByCity(request.cityName)
                        handleResponse(response)
                    } catch (e: Throwable) {
                        Response().apply { resultCode = ResponseCode.Other.code }
                    }
                }
            }
        }
    }

    private fun handleResponse(response: retrofit2.Response<WeatherDataDto>): Response {
        return if (response.isSuccessful) {
            (response.body() as WeatherDataDto).apply {
                resultCode = ResponseCode.Success.code
            }
        } else {
            Response().apply { resultCode = response.code() }
        }
    }

    private fun isConnected(): Boolean {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}