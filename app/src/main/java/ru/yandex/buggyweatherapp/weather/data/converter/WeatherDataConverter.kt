package ru.yandex.buggyweatherapp.weather.data.converter

import ru.yandex.buggyweatherapp.model.WeatherData
import ru.yandex.buggyweatherapp.weather.data.dto.WeatherDataDto
import javax.inject.Inject

class WeatherDataConverter @Inject constructor() {
    fun mapToWeatherData(weatherDataDto: WeatherDataDto): WeatherData {
        return with(weatherDataDto) {
            WeatherData(
                cityName = name,
                country = sys.country,
                temperature = main.temp,
                feelsLike = main.feelsLike,
                minTemp = main.tempMin,
                maxTemp = main.tempMin,
                humidity = main.humidity,
                pressure = main.pressure,
                windSpeed = wind.speed,
                windDirection = wind.deg,
                description = weather.first().description,
                icon = weather.first().icon,
                cloudiness = clouds.all,
                sunriseTime = sys.sunrise.toLong(),
                sunsetTime = sys.sunset.toLong(),
                timezone = timezone,
                timestamp = dt.toLong(),
                rain = rain?.precipitation,
                snow = snow?.precipitation
            )
        }
    }
}