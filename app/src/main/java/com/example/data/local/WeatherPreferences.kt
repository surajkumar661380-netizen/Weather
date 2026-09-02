package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.TemperatureUnit
import com.example.data.model.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WeatherPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("weather_preferences", Context.MODE_PRIVATE)

    private val _unitFlow = MutableStateFlow(loadUnit())
    val unitFlow: StateFlow<TemperatureUnit> = _unitFlow.asStateFlow()

    private val _themeFlow = MutableStateFlow(loadTheme())
    val themeFlow: StateFlow<ThemeMode> = _themeFlow.asStateFlow()

    private val _lastSelectedLat = MutableStateFlow(prefs.getFloat(KEY_LAT, 20.2961f).toDouble())
    private val _lastSelectedLon = MutableStateFlow(prefs.getFloat(KEY_LON, 85.8245f).toDouble())
    private val _lastSelectedName = MutableStateFlow(prefs.getString(KEY_NAME, "Bhubaneswar") ?: "Bhubaneswar")
    private val _lastSelectedCountry = MutableStateFlow(prefs.getString(KEY_COUNTRY, "India") ?: "India")
    private val _lastSelectedAdmin = MutableStateFlow(prefs.getString(KEY_ADMIN, "Odisha") ?: "Odisha")

    fun setUnit(unit: TemperatureUnit) {
        prefs.edit().putString(KEY_UNIT, unit.name).apply()
        _unitFlow.value = unit
    }

    fun setTheme(theme: ThemeMode) {
        prefs.edit().putString(KEY_THEME, theme.name).apply()
        _themeFlow.value = theme
    }

    fun saveLastLocation(lat: Double, lon: Double, name: String, country: String, admin1: String? = null) {
        prefs.edit()
            .putFloat(KEY_LAT, lat.toFloat())
            .putFloat(KEY_LON, lon.toFloat())
            .putString(KEY_NAME, name)
            .putString(KEY_COUNTRY, country)
            .putString(KEY_ADMIN, admin1 ?: "Odisha")
            .apply()
        _lastSelectedLat.value = lat
        _lastSelectedLon.value = lon
        _lastSelectedName.value = name
        _lastSelectedCountry.value = country
        _lastSelectedAdmin.value = admin1 ?: "Odisha"
    }

    fun getLastLocation(): Triple<Double, Double, String> {
        val lat = prefs.getFloat(KEY_LAT, 20.2961f).toDouble()
        val lon = prefs.getFloat(KEY_LON, 85.8245f).toDouble()
        val name = prefs.getString(KEY_NAME, "Bhubaneswar") ?: "Bhubaneswar"
        return Triple(lat, lon, name)
    }

    fun getLastLocationAdmin(): String {
        return prefs.getString(KEY_ADMIN, "Odisha") ?: "Odisha"
    }

    fun getLastLocationCountry(): String {
        return prefs.getString(KEY_COUNTRY, "India") ?: "India"
    }

    private fun loadUnit(): TemperatureUnit {
        val name = prefs.getString(KEY_UNIT, TemperatureUnit.CELSIUS.name)
        return try {
            TemperatureUnit.valueOf(name ?: TemperatureUnit.CELSIUS.name)
        } catch (e: Exception) {
            TemperatureUnit.CELSIUS
        }
    }

    private fun loadTheme(): ThemeMode {
        val name = prefs.getString(KEY_THEME, ThemeMode.SYSTEM.name)
        return try {
            ThemeMode.valueOf(name ?: ThemeMode.SYSTEM.name)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }
    }

    companion object {
        private const val KEY_UNIT = "temperature_unit"
        private const val KEY_THEME = "theme_mode"
        private const val KEY_LAT = "last_lat"
        private const val KEY_LON = "last_lon"
        private const val KEY_NAME = "last_name"
        private const val KEY_COUNTRY = "last_country"
        private const val KEY_ADMIN = "last_admin"
    }
}
