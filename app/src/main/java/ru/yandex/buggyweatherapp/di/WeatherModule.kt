package ru.yandex.buggyweatherapp.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.yandex.buggyweatherapp.weather.data.WeatherNetworkClient
import ru.yandex.buggyweatherapp.weather.data.api.NetworkClient
import ru.yandex.buggyweatherapp.weather.data.impl.WeatherRepositoryImpl
import ru.yandex.buggyweatherapp.weather.domain.api.WeatherInteractor
import ru.yandex.buggyweatherapp.weather.domain.api.WeatherRepository
import ru.yandex.buggyweatherapp.weather.domain.impl.WeatherInteractorImpl

@Module
@InstallIn(SingletonComponent::class)
interface WeatherModule {

    @Binds
    fun bindNetworkClient(weatherNetworkClient: WeatherNetworkClient): NetworkClient

    @Binds
    fun bindWeatherRepository(weatherRepositoryImpl: WeatherRepositoryImpl): WeatherRepository

    @Binds
    fun bindWeatherInteractor(weatherInteractorImpl: WeatherInteractorImpl): WeatherInteractor
}