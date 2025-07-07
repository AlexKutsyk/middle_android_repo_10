package ru.yandex.buggyweatherapp.utils

sealed class Resource<T>(
    val data: T? = null,
    val code: Int? = null
) {
    class Success<T>(data: T) : Resource<T>(data = data)
    class Error<T>(code: Int?) : Resource<T>(code = code)
}