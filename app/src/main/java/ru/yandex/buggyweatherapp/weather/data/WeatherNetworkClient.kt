package ru.yandex.buggyweatherapp.weather.data

import okhttp3.Response
import ru.yandex.buggyweatherapp.weather.data.api.NetworkClient

class WeatherNetworkClient: NetworkClient {
    override suspend fun doRequest(request: Any): Response {
        TODO("Not yet implemented")
    }
}