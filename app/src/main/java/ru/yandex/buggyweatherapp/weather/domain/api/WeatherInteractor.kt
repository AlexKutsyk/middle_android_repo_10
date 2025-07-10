package ru.yandex.buggyweatherapp.weather.domain.api

import kotlinx.coroutines.flow.Flow
import ru.yandex.buggyweatherapp.model.WeatherData
import ru.yandex.buggyweatherapp.weather.data.dto.Request
import ru.yandex.buggyweatherapp.weather.domain.models.RequestError

interface WeatherInteractor {
    suspend fun getCurrentWeather(request: Request.CurrentWeather): Flow<Pair<WeatherData?, RequestError?>>
    suspend fun getWeatherByCity(request: Request.WeatherByCity) : Flow<Pair<WeatherData?, RequestError?>>
}