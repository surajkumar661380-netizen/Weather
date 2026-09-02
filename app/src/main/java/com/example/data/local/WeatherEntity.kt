package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_cities")
data class SavedCityEntity(
    @PrimaryKey
    val id: String, // e.g., "lat_lon" or "name_country"
    val name: String,
    val country: String,
    val admin1: String?,
    val latitude: Double,
    val longitude: Double,
    val isFavorite: Boolean = false,
    val isRecentSearch: Boolean = false,
    val lastAccessedTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "weather_cache")
data class CachedWeatherEntity(
    @PrimaryKey
    val locationKey: String, // e.g. "lat_lon" or "current_location"
    val locationName: String,
    val country: String,
    val admin1: String?,
    val latitude: Double,
    val longitude: Double,
    val jsonPayload: String,
    val timestamp: Long = System.currentTimeMillis()
)
