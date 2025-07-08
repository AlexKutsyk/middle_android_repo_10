package ru.yandex.buggyweatherapp.location.domain.api

import kotlinx.coroutines.flow.Flow
import ru.yandex.buggyweatherapp.model.Location

interface LocationInteractor {
    suspend fun getCurrentLocation() : Flow<Location?>
    suspend fun getCityNameFromLocation(location: Location): String?
}