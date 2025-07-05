package ru.yandex.buggyweatherapp.weather.di

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.yandex.buggyweatherapp.BuildConfig
import ru.yandex.buggyweatherapp.weather.data.api.WeatherApiService

@Module
class WeatherModule {

    @Provides
    fun provideWeatherApiWeatherApiService(): WeatherApiService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
            .create(WeatherApiService::class.java)
    }
}