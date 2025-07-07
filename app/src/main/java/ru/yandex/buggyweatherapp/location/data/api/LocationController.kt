package ru.yandex.buggyweatherapp.location.data.api

import kotlinx.coroutines.flow.Flow
import ru.yandex.buggyweatherapp.model.Location

interface LocationController {
    suspend fun getCurrentLocation() : Flow<Location?>
    suspend fun getCityNameFromLocation(): Flow<String?>
}