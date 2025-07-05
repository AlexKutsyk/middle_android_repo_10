package ru.yandex.buggyweatherapp.weather.di

import dagger.Component
import ru.yandex.buggyweatherapp.MainActivity

@Component
interface AppComponent {
    fun inject(mainActivity: MainActivity)
}