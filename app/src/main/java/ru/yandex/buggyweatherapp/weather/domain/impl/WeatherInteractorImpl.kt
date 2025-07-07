package ru.yandex.buggyweatherapp.weather.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.yandex.buggyweatherapp.model.WeatherData
import ru.yandex.buggyweatherapp.utils.Resource
import ru.yandex.buggyweatherapp.weather.data.dto.Request
import ru.yandex.buggyweatherapp.weather.data.impl.WeatherRepositoryImpl
import ru.yandex.buggyweatherapp.weather.domain.api.WeatherInteractor
import javax.inject.Inject

class WeatherInteractorImpl @Inject constructor(
    private val repository: WeatherRepositoryImpl
) : WeatherInteractor {
    override suspend fun getCurrentWeather(request: Request.CurrentWeather): Flow<Pair<WeatherData?, Int?>> {
        return repository.getCurrentWeather(request).map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.code)
                }
            }
        }

    }

    override suspend fun getWeatherByCity(request: Request.WeatherByCity): Flow<Pair<WeatherData?, Int?>> {
        return repository.getWeatherByCity(request).map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.code)
                }
            }
        }
    }
}