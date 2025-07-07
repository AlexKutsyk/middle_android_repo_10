package ru.yandex.buggyweatherapp.weather.data.impl

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.yandex.buggyweatherapp.model.WeatherData
import ru.yandex.buggyweatherapp.utils.Resource
import ru.yandex.buggyweatherapp.weather.data.api.NetworkClient
import ru.yandex.buggyweatherapp.weather.data.converter.WeatherDataConverter
import ru.yandex.buggyweatherapp.weather.data.dto.Request
import ru.yandex.buggyweatherapp.weather.data.dto.ResponseCode
import ru.yandex.buggyweatherapp.weather.data.dto.WeatherDataDto
import ru.yandex.buggyweatherapp.weather.domain.api.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    val networkClient: NetworkClient,
    val weatherDataConverter: WeatherDataConverter
) : WeatherRepository {
    override suspend fun getCurrentWeather(request: Request.CurrentWeather): Flow<Resource<WeatherData>> =
        flow {
            val result = networkClient.doRequest(request)
            when (result.resultCode) {
                ResponseCode.ConnectionFailed.code -> {
                    emit(Resource.Error(ResponseCode.ConnectionFailed.code))
                }

                ResponseCode.ServerFailed.code -> {
                    emit(Resource.Error(ResponseCode.ServerFailed.code))
                }

                ResponseCode.Success.code -> {
                    emit(Resource.Success(weatherDataConverter.mapToWeatherData(result as WeatherDataDto)))
                }
            }
        }

    override suspend fun getWeatherByCity(request: Request.WeatherByCity): Flow<Resource<WeatherData>> =
        flow {
            Log.i("alex", "Repository / Request - $request")

            val result = networkClient.doRequest(request)
            Log.i("alex", "Repository / Result - ${result.resultCode}")

            when (result.resultCode) {
                ResponseCode.ConnectionFailed.code -> {
                    emit(Resource.Error(ResponseCode.ConnectionFailed.code))
                }

                ResponseCode.ServerFailed.code -> {
                    emit(Resource.Error(ResponseCode.ServerFailed.code))
                }

                ResponseCode.Success.code -> {
                    emit(Resource.Success(weatherDataConverter.mapToWeatherData(result as WeatherDataDto)))
                }
            }
        }
}