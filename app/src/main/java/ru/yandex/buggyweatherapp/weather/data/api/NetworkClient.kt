package ru.yandex.buggyweatherapp.weather.data.api

import ru.yandex.buggyweatherapp.weather.data.dto.Request
import ru.yandex.buggyweatherapp.weather.data.dto.Response

interface NetworkClient {
    suspend fun doRequest(request: Request): Response
}