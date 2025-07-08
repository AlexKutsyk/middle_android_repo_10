/*
package ru.yandex.buggyweatherapp.api

import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.yandex.buggyweatherapp.BuildConfig
import ru.yandex.buggyweatherapp.weather.data.api.WeatherApiService

object RetrofitInstance {
    
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
    }

    val weatherApi: WeatherApiService = retrofit.create(WeatherApiService::class.java)
}*/
