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
import ru.yandex.buggyweatherapp.location.domain.models.Location
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
                val addresses =
                    geocoder.getFromLocation(location.latitude, location.longitude, MAX_RESULT)

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
                Log.e(TAG, "Error getting city name", e)
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
                    Log.e(TAG, "Error getting location", e)
                    currentLocation.value = null
                }
        } catch (e: SecurityException) {
            Log.e(TAG, "Location permission not granted", e)
            currentLocation.value = null
        }
    }


    private fun requestLocationUpdates() {
        try {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, INTERVAL)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(MIN_UPDATE_INTERVAL)
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
            Log.e(TAG, "Location permission not granted", e)
            currentLocation.value = null
        }
    }

    companion object {
        private const val TAG = "LocationRepository"
        private const val MAX_RESULT = 5
        private const val INTERVAL = 10_000L
        private const val MIN_UPDATE_INTERVAL = 5_000L
    }
}