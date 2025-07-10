package ru.yandex.buggyweatherapp.di

import android.content.Context
import android.location.Geocoder
import android.net.ConnectivityManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.yandex.buggyweatherapp.BuildConfig
import ru.yandex.buggyweatherapp.weather.data.api.WeatherApiService
import java.util.Locale

@Module
@InstallIn(SingletonComponent::class)
class AppModule() {

    @Provides
    fun provideWeatherApiWeatherApiService(): WeatherApiService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
            .create(WeatherApiService::class.java)
    }

    @Provides
    fun provideConnectivityManager(
        @ApplicationContext context: Context
    ): ConnectivityManager {
        return context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
    }

    @Provides
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context
    ) : FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    fun provideGeocoder(
        @ApplicationContext context: Context
    ) : Geocoder {
        return Geocoder(context, Locale.getDefault())
    }
}