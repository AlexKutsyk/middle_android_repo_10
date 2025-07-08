package ru.yandex.buggyweatherapp.location.data.impl

import android.location.Geocoder
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import ru.yandex.buggyweatherapp.location.domain.api.LocationRepository
import ru.yandex.buggyweatherapp.model.Location
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val geocoder: Geocoder
) : LocationRepository {

    var currentLocation = MutableStateFlow<Location?>(null)
        private set

    override suspend fun getCurrentLocation(): Flow<Location?> {
        return withContext(Dispatchers.IO) {
            searchCurrentLocation()
            currentLocation
        }
    }

    override suspend fun getCityNameFromLocation(location: Location): String? {
        return withContext(Dispatchers.IO) {
            try {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    if (address.locality != null) {
                        address.locality
                    } else if (address.subAdminArea != null) {
                        address.subAdminArea
                    } else {
                        address.adminArea
                    }
                } else {
                    null
                }
            } catch (e: Exception) {
                Log.e("LocationRepository", "Error getting city name", e)
                null
            }
        }
    }


    private fun searchCurrentLocation() {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val userLocation = Location(
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                        currentLocation.value = userLocation
                    } else {
                        requestLocationUpdates()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("LocationRepository", "Error getting location", e)
                    currentLocation.value = null
                }
        } catch (e: SecurityException) {
            Log.e("LocationRepository", "Location permission not granted", e)
            currentLocation.value = null
        }
    }


    private fun requestLocationUpdates() {
        try {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(5000)
                .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        val userLocation = Location(
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                        currentLocation.value = userLocation
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Log.e("LocationRepository", "Location permission not granted", e)
            currentLocation.value = null
        }
    }
}