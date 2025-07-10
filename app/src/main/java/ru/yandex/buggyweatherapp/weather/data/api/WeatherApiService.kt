package ru.yandex.buggyweatherapp.weather.data.api

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import ru.yandex.buggyweatherapp.BuildConfig.API_KEY
import ru.yandex.buggyweatherapp.weather.data.dto.WeatherDataDto

interface WeatherApiService {

    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = API_KEY,
        @Query("units") units: String = "metric"
    ): Response<WeatherDataDto>

    @GET("weather")
    suspend fun getWeatherByCity(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String = API_KEY,
        @Query("units") units: String = "metric"
    ): Response<WeatherDataDto>
}