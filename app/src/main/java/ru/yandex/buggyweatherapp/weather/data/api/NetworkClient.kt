package ru.yandex.buggyweatherapp.weather.data.api

import okhttp3.Response

interface NetworkClient {
    suspend fun doRequest(request: Any): Response
}