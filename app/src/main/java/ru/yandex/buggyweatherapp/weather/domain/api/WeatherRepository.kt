package ru.yandex.buggyweatherapp.weather.domain.api

import kotlinx.coroutines.flow.Flow
import ru.yandex.buggyweatherapp.model.WeatherData
import ru.yandex.buggyweatherapp.utils.Resource
import ru.yandex.buggyweatherapp.weather.data.dto.Request

interface WeatherRepository {
    suspend fun getCurrentWeather(request: Request.CurrentWeather): Flow<Resource<WeatherData>>
    suspend fun getWeatherByCity(request: Request.WeatherByCity) : Flow<Resource<WeatherData>>
}