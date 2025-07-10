package ru.yandex.buggyweatherapp.weather.data.dto

enum class ResponseCode(val code: Int) {
    Success(200),
    ConnectionFailed(-1),
    ServerFailed(500),
    SearchFail(404),
    Other(-2)
}