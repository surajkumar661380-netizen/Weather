package com.example.data.repository

import com.example.data.api.RetrofitClient
import com.example.data.api.WeatherApiService
import com.example.data.local.CachedWeatherEntity
import com.example.data.local.SavedCityDao
import com.example.data.local.SavedCityEntity
import com.example.data.model.CurrentWeather
import com.example.data.model.DailyForecastItem
import com.example.data.model.HourlyForecastItem
import com.example.data.model.LocationInfo
import com.example.data.model.OpenMeteoWeatherResponse
import com.example.data.model.WeatherData
import com.example.data.model.WmoWeatherMapper
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class WeatherRepository(
    private val apiService: WeatherApiService = RetrofitClient.apiService,
    private val savedCityDao: SavedCityDao
) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val responseAdapter = moshi.adapter(OpenMeteoWeatherResponse::class.java)

    val favoriteCities: Flow<List<LocationInfo>> = savedCityDao.getFavoriteCities().map { entities ->
        entities.map { it.toLocationInfo() }
    }

    val recentSearches: Flow<List<LocationInfo>> = savedCityDao.getRecentSearches().map { entities ->
        entities.map { it.toLocationInfo() }
    }

    suspend fun getWeatherData(
        location: LocationInfo
    ): Result<WeatherData> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getForecast(
                latitude = location.latitude,
                longitude = location.longitude
            )

            // Cache response locally
            try {
                val json = responseAdapter.toJson(response)
                val key = "${location.latitude}_${location.longitude}"
                savedCityDao.insertCachedWeather(
                    CachedWeatherEntity(
                        locationKey = key,
                        locationName = location.name,
                        country = location.country,
                        admin1 = location.admin1,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        jsonPayload = json
                    )
                )
            } catch (_: Exception) {
                // Ignore cache serialization error
            }

            val domainData = mapToDomain(location, response)
            Result.success(domainData)
        } catch (e: Exception) {
            // Check offline cache for this location or latest
            val cached = getCachedWeatherForLocation(location.latitude, location.longitude)
                ?: savedCityDao.getLatestCachedWeather()

            if (cached != null) {
                try {
                    val response = responseAdapter.fromJson(cached.jsonPayload)
                    if (response != null) {
                        val cachedLocation = LocationInfo(
                            id = "${cached.latitude}_${cached.longitude}",
                            name = cached.locationName,
                            country = cached.country,
                            admin1 = cached.admin1,
                            latitude = cached.latitude,
                            longitude = cached.longitude,
                            isCurrentLocation = location.isCurrentLocation
                        )
                        val domainData = mapToDomain(cachedLocation, response, isOffline = true)
                        return@withContext Result.success(domainData)
                    }
                } catch (_: Exception) { }
            }

            Result.failure(e)
        }
    }

    private suspend fun getCachedWeatherForLocation(lat: Double, lon: Double): CachedWeatherEntity? {
        val key = "${lat}_${lon}"
        return savedCityDao.getCachedWeather(key)
    }

    suspend fun searchLocations(query: String): Result<List<LocationInfo>> = withContext(Dispatchers.IO) {
        val cleanQuery = query.trim()
        if (cleanQuery.length < 2) return@withContext Result.success(emptyList())

        // 1. Search built-in Bhubaneswar localities first for instant local responsiveness
        val localMatches = com.example.data.model.BhubaneswarData.searchLocalities(cleanQuery)

        try {
            val response = apiService.searchLocations(name = cleanQuery)
            val apiResults = response.results?.map { dto ->
                LocationInfo(
                    id = "${dto.latitude}_${dto.longitude}_${dto.id}",
                    name = dto.name,
                    country = dto.country ?: "",
                    admin1 = dto.admin1,
                    latitude = dto.latitude,
                    longitude = dto.longitude
                )
            } ?: emptyList()

            // Merge local Bhubaneswar matches + remote API results without duplicates
            val combined = mutableListOf<LocationInfo>()
            combined.addAll(localMatches)
            for (res in apiResults) {
                if (combined.none { it.name.equals(res.name, ignoreCase = true) && it.country.equals(res.country, ignoreCase = true) }) {
                    combined.add(res)
                }
            }
            Result.success(combined)
        } catch (e: Exception) {
            // If network fails, return local Bhubaneswar matches if any
            if (localMatches.isNotEmpty()) {
                Result.success(localMatches)
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun addRecentSearch(location: LocationInfo) = withContext(Dispatchers.IO) {
        val entity = SavedCityEntity(
            id = "${location.latitude}_${location.longitude}",
            name = location.name,
            country = location.country,
            admin1 = location.admin1,
            latitude = location.latitude,
            longitude = location.longitude,
            isRecentSearch = true,
            lastAccessedTime = System.currentTimeMillis()
        )
        savedCityDao.insertOrUpdateCity(entity)
    }

    suspend fun toggleFavorite(location: LocationInfo, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        val id = "${location.latitude}_${location.longitude}"
        val entity = SavedCityEntity(
            id = id,
            name = location.name,
            country = location.country,
            admin1 = location.admin1,
            latitude = location.latitude,
            longitude = location.longitude,
            isFavorite = isFavorite,
            isRecentSearch = true,
            lastAccessedTime = System.currentTimeMillis()
        )
        savedCityDao.insertOrUpdateCity(entity)
    }

    suspend fun removeRecentSearch(id: String) = withContext(Dispatchers.IO) {
        savedCityDao.deleteRecentCity(id)
    }

    // ----------------- Domain Mapping Helpers -----------------

    private fun mapToDomain(
        location: LocationInfo,
        dto: OpenMeteoWeatherResponse,
        isOffline: Boolean = false
    ): WeatherData {
        val currentDto = dto.current
        val hourlyDto = dto.hourly
        val dailyDto = dto.daily

        val isDay = (currentDto?.isDay ?: 1) == 1
        val wmoCode = currentDto?.weatherCode ?: 0
        val condition = WmoWeatherMapper.getCondition(wmoCode, isDay)

        // Today's min & max from daily
        val todayMin = dailyDto?.temperature2mMin?.firstOrNull() ?: (currentDto?.temperature2m ?: 20.0) - 4.0
        val todayMax = dailyDto?.temperature2mMax?.firstOrNull() ?: (currentDto?.temperature2m ?: 20.0) + 4.0

        val todaySunrise = dailyDto?.sunrise?.firstOrNull()?.let { formatTimeIso(it) } ?: "06:00 AM"
        val todaySunset = dailyDto?.sunset?.firstOrNull()?.let { formatTimeIso(it) } ?: "08:00 PM"

        val currentDomain = CurrentWeather(
            temperature = currentDto?.temperature2m ?: 0.0,
            feelsLike = currentDto?.apparentTemperature ?: currentDto?.temperature2m ?: 0.0,
            tempMinToday = todayMin,
            tempMaxToday = todayMax,
            condition = condition,
            humidity = currentDto?.relativeHumidity2m?.toInt() ?: 50,
            windSpeed = currentDto?.windSpeed10m ?: 0.0,
            windDirection = currentDto?.windDirection10m ?: 0.0,
            windGusts = currentDto?.windGusts10m ?: 0.0,
            pressure = currentDto?.pressureMsl ?: currentDto?.surfacePressure ?: 1013.25,
            visibility = (hourlyDto?.visibility?.firstOrNull() ?: 10000.0) / 1000.0, // convert m to km
            uvIndex = dailyDto?.uvIndexMax?.firstOrNull() ?: hourlyDto?.uvIndex?.firstOrNull() ?: 3.0,
            cloudCover = currentDto?.cloudCover?.toInt() ?: 20,
            precipitation = currentDto?.precipitation ?: 0.0,
            isDay = isDay,
            timeFormatted = formatCurrentTime(currentDto?.time),
            sunrise = todaySunrise,
            sunset = todaySunset,
            dewPoint = hourlyDto?.dewPoint2m?.firstOrNull() ?: 12.0
        )

        // Map hourly (next 24-48 hours starting from current hour)
        val hourlyList = mutableListOf<HourlyForecastItem>()
        if (hourlyDto != null && hourlyDto.time.isNotEmpty()) {
            val now = LocalDateTime.now()
            val totalHours = hourlyDto.time.size

            // Find index closest to now
            var startIndex = 0
            for (i in 0 until totalHours) {
                try {
                    val itemTime = LocalDateTime.parse(hourlyDto.time[i], DateTimeFormatter.ISO_DATE_TIME)
                    if (itemTime.isAfter(now.minusHours(1))) {
                        startIndex = i
                        break
                    }
                } catch (_: Exception) { }
            }

            val endIndex = (startIndex + 24).coerceAtMost(totalHours)
            for (i in startIndex until endIndex) {
                val timeStr = hourlyDto.time[i]
                val hourLabel = formatHourLabel(timeStr, i == startIndex)
                val hourCode = hourlyDto.weatherCode.getOrNull(i) ?: 0
                val hourIsDay = (hourlyDto.isDay?.getOrNull(i) ?: 1) == 1
                val hourCondition = WmoWeatherMapper.getCondition(hourCode, hourIsDay)
                val temp = hourlyDto.temperature2m.getOrNull(i) ?: 0.0
                val precipProb = hourlyDto.precipitationProbability?.getOrNull(i) ?: 0
                val precipAmount = hourlyDto.precipitation?.getOrNull(i) ?: 0.0
                val wind = hourlyDto.windSpeed10m?.getOrNull(i) ?: 0.0

                hourlyList.add(
                    HourlyForecastItem(
                        timeIso = timeStr,
                        hourLabel = hourLabel,
                        temperature = temp,
                        condition = hourCondition,
                        precipitationProbability = precipProb,
                        precipitationAmount = precipAmount,
                        windSpeed = wind,
                        isNow = i == startIndex
                    )
                )
            }
        }

        // Map daily (7-10 days)
        val dailyList = mutableListOf<DailyForecastItem>()
        if (dailyDto != null && dailyDto.time.isNotEmpty()) {
            val totalDays = dailyDto.time.size.coerceAtMost(10)
            for (i in 0 until totalDays) {
                val dateStr = dailyDto.time[i]
                val dayLabel = formatDayLabel(dateStr, i)
                val formattedDate = formatDateDisplay(dateStr)
                val code = dailyDto.weatherCode.getOrNull(i) ?: 0
                val dailyCondition = WmoWeatherMapper.getCondition(code, isDay = true)
                val minT = dailyDto.temperature2mMin.getOrNull(i) ?: 0.0
                val maxT = dailyDto.temperature2mMax.getOrNull(i) ?: 0.0
                val prob = dailyDto.precipitationProbabilityMax?.getOrNull(i) ?: 0
                val sum = dailyDto.precipitationSum?.getOrNull(i) ?: 0.0
                val uv = dailyDto.uvIndexMax?.getOrNull(i) ?: 0.0
                val sr = dailyDto.sunrise?.getOrNull(i)?.let { formatTimeIso(it) } ?: "06:00 AM"
                val ss = dailyDto.sunset?.getOrNull(i)?.let { formatTimeIso(it) } ?: "08:00 PM"
                val maxWind = dailyDto.windSpeed10mMax?.getOrNull(i) ?: 0.0

                dailyList.add(
                    DailyForecastItem(
                        dateIso = dateStr,
                        dayLabel = dayLabel,
                        formattedDate = formattedDate,
                        minTemp = minT,
                        maxTemp = maxT,
                        condition = dailyCondition,
                        precipitationProbability = prob,
                        precipitationSum = sum,
                        uvIndexMax = uv,
                        sunrise = sr,
                        sunset = ss,
                        windSpeedMax = maxWind
                    )
                )
            }
        }

        return WeatherData(
            location = location,
            current = currentDomain,
            hourly = hourlyList,
            daily = dailyList
        )
    }

    private fun formatCurrentTime(isoTime: String?): String {
        if (isoTime.isNullOrBlank()) return "Updated just now"
        return try {
            val dt = LocalDateTime.parse(isoTime, DateTimeFormatter.ISO_DATE_TIME)
            dt.format(DateTimeFormatter.ofPattern("EEE, MMM d • h:mm a", Locale.getDefault()))
        } catch (e: Exception) {
            "Updated just now"
        }
    }

    private fun formatHourLabel(isoTime: String, isFirst: Boolean): String {
        if (isFirst) return "Now"
        return try {
            val dt = LocalDateTime.parse(isoTime, DateTimeFormatter.ISO_DATE_TIME)
            dt.format(DateTimeFormatter.ofPattern("h a", Locale.getDefault()))
        } catch (e: Exception) {
            isoTime.takeLast(5)
        }
    }

    private fun formatDayLabel(isoDate: String, index: Int): String {
        if (index == 0) return "Today"
        if (index == 1) return "Tomorrow"
        return try {
            val dt = LocalDate.parse(isoDate, DateTimeFormatter.ISO_DATE)
            dt.format(DateTimeFormatter.ofPattern("EEE", Locale.getDefault()))
        } catch (e: Exception) {
            "Day $index"
        }
    }

    private fun formatDateDisplay(isoDate: String): String {
        return try {
            val dt = LocalDate.parse(isoDate, DateTimeFormatter.ISO_DATE)
            dt.format(DateTimeFormatter.ofPattern("MMM d", Locale.getDefault()))
        } catch (e: Exception) {
            isoDate
        }
    }

    private fun formatTimeIso(isoDateTime: String): String {
        return try {
            val dt = LocalDateTime.parse(isoDateTime, DateTimeFormatter.ISO_DATE_TIME)
            dt.format(DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()))
        } catch (e: Exception) {
            isoDateTime.takeLast(5)
        }
    }

    private fun SavedCityEntity.toLocationInfo(): LocationInfo {
        return LocationInfo(
            id = id,
            name = name,
            country = country,
            admin1 = admin1,
            latitude = latitude,
            longitude = longitude,
            isFavorite = isFavorite
        )
    }
}
