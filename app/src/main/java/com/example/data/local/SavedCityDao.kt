package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedCityDao {

    @Query("SELECT * FROM saved_cities WHERE isFavorite = 1 ORDER BY lastAccessedTime DESC")
    fun getFavoriteCities(): Flow<List<SavedCityEntity>>

    @Query("SELECT * FROM saved_cities WHERE isRecentSearch = 1 ORDER BY lastAccessedTime DESC LIMIT 6")
    fun getRecentSearches(): Flow<List<SavedCityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCity(city: SavedCityEntity)

    @Query("UPDATE saved_cities SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean)

    @Query("DELETE FROM saved_cities WHERE id = :id AND isFavorite = 0")
    suspend fun deleteRecentCity(id: String)

    @Query("DELETE FROM saved_cities WHERE id = :id")
    suspend fun deleteCityById(id: String)

    // Cache operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedWeather(cache: CachedWeatherEntity)

    @Query("SELECT * FROM weather_cache WHERE locationKey = :key LIMIT 1")
    suspend fun getCachedWeather(key: String): CachedWeatherEntity?

    @Query("SELECT * FROM weather_cache ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestCachedWeather(): CachedWeatherEntity?
}
