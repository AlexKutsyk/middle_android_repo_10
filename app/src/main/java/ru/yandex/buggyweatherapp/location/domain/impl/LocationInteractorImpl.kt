package ru.yandex.buggyweatherapp.location.domain.impl

import kotlinx.coroutines.flow.Flow
import ru.yandex.buggyweatherapp.location.domain.api.LocationInteractor
import ru.yandex.buggyweatherapp.location.domain.api.LocationRepository
import ru.yandex.buggyweatherapp.location.domain.models.Location
import javax.inject.Inject

class LocationInteractorImpl @Inject constructor(
    private val locationRepository: LocationRepository
): LocationInteractor {
    override suspend fun getCurrentLocation(): Flow<Location?> {
        return locationRepository.getCurrentLocation()
    }

    override suspend fun getCityNameFromLocation(location: Location): String? {
        return locationRepository.getCityNameFromLocation(location)
    }
}