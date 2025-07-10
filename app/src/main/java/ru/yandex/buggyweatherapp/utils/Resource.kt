package ru.yandex.buggyweatherapp.utils

import ru.yandex.buggyweatherapp.weather.domain.models.RequestError

sealed class Resource<T>(
    val data: T? = null,
    val code: RequestError? = null
) {
    class Success<T>(data: T) : Resource<T>(data = data)
    class Error<T>(code: RequestError?) : Resource<T>(code = code)
}