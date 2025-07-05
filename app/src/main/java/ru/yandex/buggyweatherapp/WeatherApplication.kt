package ru.yandex.buggyweatherapp

import android.app.Application
import android.content.Context
import ru.yandex.buggyweatherapp.utils.ImageLoader
import ru.yandex.buggyweatherapp.utils.LocationTracker
import ru.yandex.buggyweatherapp.weather.di.AppComponent
import ru.yandex.buggyweatherapp.weather.di.DaggerAppComponent

class WeatherApplication : Application() {

    companion object {
        lateinit var appContext: Context
            private set
    }

    lateinit var appComponent: AppComponent
    
    override fun onCreate() {
        super.onCreate()
        appContext = this
        ImageLoader.initialize(this)
        LocationTracker.getInstance(this)
        appComponent = DaggerAppComponent.create()
    }
}