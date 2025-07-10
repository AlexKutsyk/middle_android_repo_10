package ru.yandex.buggyweatherapp.weather.data.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import ru.yandex.buggyweatherapp.model.WeatherData
import ru.yandex.buggyweatherapp.utils.Resource
import ru.yandex.buggyweatherapp.weather.data.api.NetworkClient
import ru.yandex.buggyweatherapp.weather.data.converter.WeatherDataConverter
import ru.yandex.buggyweatherapp.weather.data.dto.Request
import ru.yandex.buggyweatherapp.weather.data.dto.Response
import ru.yandex.buggyweatherapp.weather.data.dto.ResponseCode
import ru.yandex.buggyweatherapp.weather.data.dto.WeatherDataDto
import ru.yandex.buggyweatherapp.weather.domain.api.WeatherRepository
import ru.yandex.buggyweatherapp.weather.domain.models.RequestError
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    val networkClient: NetworkClient,
    val weatherDataConverter: WeatherDataConverter
) : WeatherRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getCurrentWeather(request: Request.CurrentWeather): Flow<Resource<WeatherData>> =
        flow {
            val result = networkClient.doRequest(request)
            emit(result)
        }.flatMapConcat {
            handleResponse(it)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getWeatherByCity(request: Request.WeatherByCity): Flow<Resource<WeatherData>> =
        flow {
            val result = networkClient.doRequest(request)
            emit(result)
        }.flatMapConcat {
            handleResponse(it)
        }

    private fun handleResponse(result: Response): Flow<Resource<WeatherData>> = flow {
        when (result.resultCode) {
            ResponseCode.Success.code -> {
                emit(Resource.Success(weatherDataConverter.mapToWeatherData(result as WeatherDataDto)))
            }

            ResponseCode.ConnectionFailed.code -> {
                emit(Resource.Error(RequestError.Connection))
            }

            ResponseCode.ServerFailed.code -> {
                emit(Resource.Error(RequestError.Server))
            }

            ResponseCode.SearchFail.code -> {
                emit(Resource.Error(RequestError.Search))
            }

            else -> {
                emit(Resource.Error(RequestError.Other))
            }
        }
    }
}