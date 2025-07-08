package ru.yandex.buggyweatherapp.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.yandex.buggyweatherapp.location.data.impl.LocationRepositoryImpl
import ru.yandex.buggyweatherapp.location.domain.api.LocationInteractor
import ru.yandex.buggyweatherapp.location.domain.api.LocationRepository
import ru.yandex.buggyweatherapp.location.domain.impl.LocationInteractorImpl

@Module
@InstallIn(SingletonComponent::class)
interface LocationModule {

    @Binds
    fun bindLocationRepository(locationRepository: LocationRepositoryImpl): LocationRepository

    @Binds
    fun bindLocationInteractor(locationInteractor: LocationInteractorImpl): LocationInteractor
}