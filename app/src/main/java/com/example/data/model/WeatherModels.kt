package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class TemperatureUnit(val symbol: String, val speedUnit: String, val precipitationUnit: String, val pressureUnit: String) {
    CELSIUS("°C", "km/h", "mm", "hPa"),
    FAHRENHEIT("°F", "mph", "in", "inHg")
}

enum class ThemeMode {
    SYSTEM, DARK, LIGHT
}

enum class WeatherConditionType {
    CLEAR_DAY,
    CLEAR_NIGHT,
    PARTLY_CLOUDY_DAY,
    PARTLY_CLOUDY_NIGHT,
    CLOUDY,
    FOG,
    DRIZZLE,
    RAIN,
    HEAVY_RAIN,
    SNOW,
    THUNDERSTORM
}

data class WeatherConditionInfo(
    val title: String,
    val description: String,
    val type: WeatherConditionType,
    val iconCode: String
)

// Helper for mapping WMO Weather Codes
object WmoWeatherMapper {
    fun getCondition(wmoCode: Int, isDay: Boolean = true): WeatherConditionInfo {
        return when (wmoCode) {
            0 -> WeatherConditionInfo(
                title = if (isDay) "Sunny" else "Clear Sky",
                description = if (isDay) "Clear skies with plenty of sunshine" else "Clear starry night sky",
                type = if (isDay) WeatherConditionType.CLEAR_DAY else WeatherConditionType.CLEAR_NIGHT,
                iconCode = if (isDay) "01d" else "01n"
            )
            1 -> WeatherConditionInfo(
                title = if (isDay) "Mainly Sunny" else "Mostly Clear",
                description = "Few clouds in the sky",
                type = if (isDay) WeatherConditionType.CLEAR_DAY else WeatherConditionType.CLEAR_NIGHT,
                iconCode = if (isDay) "01d" else "01n"
            )
            2 -> WeatherConditionInfo(
                title = "Partly Cloudy",
                description = "Sun and clouds mingling",
                type = if (isDay) WeatherConditionType.PARTLY_CLOUDY_DAY else WeatherConditionType.PARTLY_CLOUDY_NIGHT,
                iconCode = if (isDay) "02d" else "02n"
            )
            3 -> WeatherConditionInfo(
                title = "Overcast",
                description = "Thick cloud blanket overhead",
                type = WeatherConditionType.CLOUDY,
                iconCode = "03d"
            )
            45, 48 -> WeatherConditionInfo(
                title = "Foggy",
                description = "Mist and reduced visibility",
                type = WeatherConditionType.FOG,
                iconCode = "50d"
            )
            51, 53, 55 -> WeatherConditionInfo(
                title = "Drizzle",
                description = "Gentle light drizzle",
                type = WeatherConditionType.DRIZZLE,
                iconCode = "09d"
            )
            56, 57 -> WeatherConditionInfo(
                title = "Freezing Drizzle",
                description = "Chilly freezing precipitation",
                type = WeatherConditionType.DRIZZLE,
                iconCode = "09d"
            )
            61, 63 -> WeatherConditionInfo(
                title = "Rain",
                description = "Moderate rainfall",
                type = WeatherConditionType.RAIN,
                iconCode = "10d"
            )
            65 -> WeatherConditionInfo(
                title = "Heavy Rain",
                description = "Intense pouring rain",
                type = WeatherConditionType.HEAVY_RAIN,
                iconCode = "10d"
            )
            66, 67 -> WeatherConditionInfo(
                title = "Freezing Rain",
                description = "Icy cold rain showers",
                type = WeatherConditionType.RAIN,
                iconCode = "10d"
            )
            71, 73 -> WeatherConditionInfo(
                title = "Snowfall",
                description = "Fresh powdery snowflakes",
                type = WeatherConditionType.SNOW,
                iconCode = "13d"
            )
            75, 77 -> WeatherConditionInfo(
                title = "Heavy Snow",
                description = "Dense snowfall accumulations",
                type = WeatherConditionType.SNOW,
                iconCode = "13d"
            )
            80, 81 -> WeatherConditionInfo(
                title = "Showers",
                description = "Passing rain showers",
                type = WeatherConditionType.RAIN,
                iconCode = "09d"
            )
            82 -> WeatherConditionInfo(
                title = "Violent Showers",
                description = "Heavy sudden downpours",
                type = WeatherConditionType.HEAVY_RAIN,
                iconCode = "09d"
            )
            85, 86 -> WeatherConditionInfo(
                title = "Snow Showers",
                description = "Brief bursts of flurries",
                type = WeatherConditionType.SNOW,
                iconCode = "13d"
            )
            95 -> WeatherConditionInfo(
                title = "Thunderstorm",
                description = "Lightning strikes and thunder",
                type = WeatherConditionType.THUNDERSTORM,
                iconCode = "11d"
            )
            96, 99 -> WeatherConditionInfo(
                title = "Severe Storm & Hail",
                description = "Severe thunderstorms with hail",
                type = WeatherConditionType.THUNDERSTORM,
                iconCode = "11d"
            )
            else -> WeatherConditionInfo(
                title = "Cloudy",
                description = "Variable cloud cover",
                type = WeatherConditionType.CLOUDY,
                iconCode = "03d"
            )
        }
    }
}

// ----------------- Network Response Models -----------------

@JsonClass(generateAdapter = true)
data class OpenMeteoWeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val timezone: String?,
    @Json(name = "timezone_abbreviation") val timezoneAbbreviation: String?,
    val elevation: Double?,
    val current: CurrentUnitsDto?,
    val hourly: HourlyForecastDto?,
    val daily: DailyForecastDto?
)

@JsonClass(generateAdapter = true)
data class CurrentUnitsDto(
    val time: String,
    @Json(name = "temperature_2m") val temperature2m: Double,
    @Json(name = "relative_humidity_2m") val relativeHumidity2m: Double,
    @Json(name = "apparent_temperature") val apparentTemperature: Double,
    @Json(name = "is_day") val isDay: Int,
    val precipitation: Double?,
    val rain: Double?,
    val showers: Double?,
    val snowfall: Double?,
    @Json(name = "weather_code") val weatherCode: Int,
    @Json(name = "cloud_cover") val cloudCover: Double?,
    @Json(name = "pressure_msl") val pressureMsl: Double?,
    @Json(name = "surface_pressure") val surfacePressure: Double?,
    @Json(name = "wind_speed_10m") val windSpeed10m: Double?,
    @Json(name = "wind_direction_10m") val windDirection10m: Double?,
    @Json(name = "wind_gusts_10m") val windGusts10m: Double?
)

@JsonClass(generateAdapter = true)
data class HourlyForecastDto(
    val time: List<String>,
    @Json(name = "temperature_2m") val temperature2m: List<Double>,
    @Json(name = "relative_humidity_2m") val relativeHumidity2m: List<Double>?,
    @Json(name = "dew_point_2m") val dewPoint2m: List<Double>?,
    @Json(name = "apparent_temperature") val apparentTemperature: List<Double>?,
    @Json(name = "precipitation_probability") val precipitationProbability: List<Int>?,
    val precipitation: List<Double>?,
    @Json(name = "weather_code") val weatherCode: List<Int>,
    @Json(name = "pressure_msl") val pressureMsl: List<Double>?,
    @Json(name = "surface_pressure") val surfacePressure: List<Double>?,
    @Json(name = "cloud_cover") val cloudCover: List<Double>?,
    val visibility: List<Double>?,
    @Json(name = "wind_speed_10m") val windSpeed10m: List<Double>?,
    @Json(name = "wind_direction_10m") val windDirection10m: List<Double>?,
    @Json(name = "uv_index") val uvIndex: List<Double>?,
    @Json(name = "is_day") val isDay: List<Int>?
)

@JsonClass(generateAdapter = true)
data class DailyForecastDto(
    val time: List<String>,
    @Json(name = "weather_code") val weatherCode: List<Int>,
    @Json(name = "temperature_2m_max") val temperature2mMax: List<Double>,
    @Json(name = "temperature_2m_min") val temperature2mMin: List<Double>,
    @Json(name = "apparent_temperature_max") val apparentTemperatureMax: List<Double>?,
    @Json(name = "apparent_temperature_min") val apparentTemperatureMin: List<Double>?,
    val sunrise: List<String>?,
    val sunset: List<String>?,
    @Json(name = "uv_index_max") val uvIndexMax: List<Double>?,
    @Json(name = "precipitation_sum") val precipitationSum: List<Double>?,
    @Json(name = "precipitation_hours") val precipitationHours: List<Double>?,
    @Json(name = "precipitation_probability_max") val precipitationProbabilityMax: List<Int>?,
    @Json(name = "wind_speed_10m_max") val windSpeed10mMax: List<Double>?,
    @Json(name = "wind_gusts_10m_max") val windGusts10mMax: List<Double>?,
    @Json(name = "wind_direction_10m_dominant") val windDirection10mDominant: List<Double>?
)

// ----------------- Geocoding Models -----------------

@JsonClass(generateAdapter = true)
data class GeocodingResponse(
    val results: List<GeocodingLocationDto>?
)

@JsonClass(generateAdapter = true)
data class GeocodingLocationDto(
    val id: Long?,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val elevation: Double?,
    @Json(name = "feature_code") val featureCode: String?,
    @Json(name = "country_code") val countryCode: String?,
    val country: String?,
    val admin1: String?,
    val admin2: String?,
    val timezone: String?,
    val population: Long?
)

// ----------------- Clean UI Domain Models -----------------

data class BhubaneswarArea(
    val name: String,
    val localityName: String,
    val subAdmin: String = "Bhubaneswar",
    val state: String = "Odisha",
    val country: String = "India",
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val landmark: String
) {
    fun toLocationInfo(): LocationInfo {
        return LocationInfo(
            id = "bbsr_${latitude}_${longitude}",
            name = localityName,
            country = country,
            admin1 = "$subAdmin, $state",
            latitude = latitude,
            longitude = longitude
        )
    }
}

object BhubaneswarData {
    val BBSR_CENTER = LocationInfo(
        id = "bbsr_center",
        name = "Bhubaneswar",
        country = "India",
        admin1 = "Odisha",
        latitude = 20.2961,
        longitude = 85.8245
    )

    val LOCALITIES = listOf(
        BhubaneswarArea(
            name = "Patia",
            localityName = "Patia",
            latitude = 20.3588,
            longitude = 85.8166,
            description = "IT Corridor & Education District",
            landmark = "Near KIIT & Infocity"
        ),
        BhubaneswarArea(
            name = "Chandrasekharpur",
            localityName = "Chandrasekharpur",
            latitude = 20.3243,
            longitude = 85.8189,
            description = "Commercial & Residential Zone",
            landmark = "Damana / Sailashree Vihar"
        ),
        BhubaneswarArea(
            name = "Jayadev Vihar",
            localityName = "Jayadev Vihar",
            latitude = 20.3019,
            longitude = 85.8209,
            description = "Central Urban Hub & Mayfair",
            landmark = "Pal Heights & BDA Park"
        ),
        BhubaneswarArea(
            name = "Saheed Nagar",
            localityName = "Saheed Nagar",
            latitude = 20.2902,
            longitude = 85.8458,
            description = "Bustling Shopping & Business Hub",
            landmark = "Near Rama Devi / Vani Vihar"
        ),
        BhubaneswarArea(
            name = "Khandagiri",
            localityName = "Khandagiri",
            latitude = 20.2586,
            longitude = 85.7865,
            description = "Historic Cave Monuments & NH",
            landmark = "Udayagiri-Khandagiri Caves"
        ),
        BhubaneswarArea(
            name = "Nayapalli",
            localityName = "Nayapalli",
            latitude = 20.2950,
            longitude = 85.8142,
            description = "Central Commercial District",
            landmark = "IRC Village & ISKCON Temple"
        ),
        BhubaneswarArea(
            name = "Rasulgarh",
            localityName = "Rasulgarh",
            latitude = 20.2981,
            longitude = 85.8647,
            description = "Eastern Gateway & Industrial Hub",
            landmark = "Rasulgarh Square & Flyover"
        ),
        BhubaneswarArea(
            name = "KIIT Square",
            localityName = "KIIT Square",
            latitude = 20.3533,
            longitude = 85.8188,
            description = "University Town & Tech Park",
            landmark = "KIIT Campus & Food Street"
        ),
        BhubaneswarArea(
            name = "Master Canteen",
            localityName = "Master Canteen",
            latitude = 20.2667,
            longitude = 85.8415,
            description = "Heart of the City & Main Station",
            landmark = "Bhubaneswar Railway Station"
        ),
        BhubaneswarArea(
            name = "Old Town / Lingaraj",
            localityName = "Old Town",
            latitude = 20.2392,
            longitude = 85.8335,
            description = "Ancient Temple Cultural Zone",
            landmark = "Lingaraj Temple & Bindusagar"
        ),
        BhubaneswarArea(
            name = "Infocity",
            localityName = "Infocity",
            latitude = 20.3582,
            longitude = 85.8078,
            description = "Software Tech Park & Corporate Hub",
            landmark = "Infosys & TCS Campuses"
        ),
        BhubaneswarArea(
            name = "Mancheswar",
            localityName = "Mancheswar",
            latitude = 20.3282,
            longitude = 85.8596,
            description = "Industrial Estate & Railway Zone",
            landmark = "Mancheswar Industrial Area"
        ),
        BhubaneswarArea(
            name = "Vani Vihar",
            localityName = "Vani Vihar",
            latitude = 20.3005,
            longitude = 85.8488,
            description = "Utkal University Education Area",
            landmark = "Vani Vihar Square & NH16"
        ),
        BhubaneswarArea(
            name = "Baramunda",
            localityName = "Baramunda",
            latitude = 20.2762,
            longitude = 85.7944,
            description = "Major Transit & Bus Terminal",
            landmark = "Babasaheb Ambedkar Bus Terminal"
        ),
        BhubaneswarArea(
            name = "Airport / BBI",
            localityName = "Airport Area",
            latitude = 20.2525,
            longitude = 85.8178,
            description = "Biju Patnaik International Airport",
            landmark = "BBI Airport Terminal 1 & 2"
        ),
        BhubaneswarArea(
            name = "Patrapada / AIIMS",
            localityName = "Patrapada",
            latitude = 20.2601,
            longitude = 85.7650,
            description = "Healthcare & Research Hub",
            landmark = "AIIMS Bhubaneswar"
        )
    )

    fun searchLocalities(query: String): List<LocationInfo> {
        val q = query.trim().lowercase()
        if (q.isBlank()) return emptyList()
        return LOCALITIES.filter {
            it.name.lowercase().contains(q) ||
            it.localityName.lowercase().contains(q) ||
            it.description.lowercase().contains(q) ||
            it.landmark.lowercase().contains(q) ||
            "bhubaneswar".contains(q) ||
            "odisha".contains(q)
        }.map { it.toLocationInfo() }
    }
}

data class LocationInfo(
    val id: String = "",
    val name: String,
    val country: String,
    val admin1: String? = null,
    val latitude: Double,
    val longitude: Double,
    val isCurrentLocation: Boolean = false,
    val isFavorite: Boolean = false
) {
    val displayName: String
        get() = when {
            admin1.isNullOrBlank() && country.isBlank() -> name
            admin1.isNullOrBlank() -> "$name, $country"
            country.isBlank() -> "$name, $admin1"
            else -> "$name, $admin1, $country"
        }
    
    val shortName: String
        get() = if (admin1.isNullOrBlank()) name else "$name, $admin1"
}

data class CurrentWeather(
    val temperature: Double,
    val feelsLike: Double,
    val tempMinToday: Double,
    val tempMaxToday: Double,
    val condition: WeatherConditionInfo,
    val humidity: Int,
    val windSpeed: Double,
    val windDirection: Double,
    val windGusts: Double,
    val pressure: Double,
    val visibility: Double,
    val uvIndex: Double,
    val cloudCover: Int,
    val precipitation: Double,
    val isDay: Boolean,
    val timeFormatted: String,
    val sunrise: String,
    val sunset: String,
    val dewPoint: Double
)

data class HourlyForecastItem(
    val timeIso: String,
    val hourLabel: String,
    val temperature: Double,
    val condition: WeatherConditionInfo,
    val precipitationProbability: Int,
    val precipitationAmount: Double,
    val windSpeed: Double,
    val isNow: Boolean = false
)

data class DailyForecastItem(
    val dateIso: String,
    val dayLabel: String,
    val formattedDate: String,
    val minTemp: Double,
    val maxTemp: Double,
    val condition: WeatherConditionInfo,
    val precipitationProbability: Int,
    val precipitationSum: Double,
    val uvIndexMax: Double,
    val sunrise: String,
    val sunset: String,
    val windSpeedMax: Double
)

data class WeatherData(
    val location: LocationInfo,
    val current: CurrentWeather,
    val hourly: List<HourlyForecastItem>,
    val daily: List<DailyForecastItem>,
    val fetchedAtMillis: Long = System.currentTimeMillis()
)

// Conversion utilities
fun Double.toFahrenheit(): Double = (this * 9.0 / 5.0) + 32.0
fun Double.toCelsius(): Double = (this - 32.0) * 5.0 / 9.0
fun Double.kmhToMph(): Double = this * 0.621371
fun Double.hpaToInHg(): Double = this * 0.02953
fun Double.mmToInches(): Double = this * 0.0393701
