package ru.yandex.buggyweatherapp.location.domain.api

import kotlinx.coroutines.flow.Flow
import ru.yandex.buggyweatherapp.location.domain.models.Location

interface LocationRepository {
    suspend fun getCurrentLocation() : Flow<Location?>
    suspend fun getCityNameFromLocation(location: Location): String?
}