package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.WeatherDatabase
import com.example.data.local.WeatherPreferences
import com.example.data.model.LocationInfo
import com.example.data.model.TemperatureUnit
import com.example.data.model.ThemeMode
import com.example.data.model.WeatherData
import com.example.data.repository.WeatherRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface WeatherUiState {
    data object Loading : WeatherUiState
    data class Success(val data: WeatherData, val isOffline: Boolean = false) : WeatherUiState
    data class Error(val message: String, val isNetworkError: Boolean = false) : WeatherUiState
}

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Searching : SearchUiState
    data class Results(val list: List<LocationInfo>) : SearchUiState
    data object Empty : SearchUiState
}

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val database = WeatherDatabase.getInstance(application)
    private val repository = WeatherRepository(savedCityDao = database.savedCityDao())
    private val preferences = WeatherPreferences(application)

    val temperatureUnit: StateFlow<TemperatureUnit> = preferences.unitFlow
    val themeMode: StateFlow<ThemeMode> = preferences.themeFlow

    val favoriteCities: StateFlow<List<LocationInfo>> = repository.favoriteCities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentSearches: StateFlow<List<LocationInfo>> = repository.recentSearches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _searchState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val searchState: StateFlow<SearchUiState> = _searchState.asStateFlow()

    private val _currentLocation = MutableStateFlow<LocationInfo>(
        LocationInfo(
            id = "bbsr_default",
            name = "Bhubaneswar",
            country = "India",
            admin1 = "Odisha",
            latitude = 20.2961,
            longitude = 85.8245
        )
    )
    val currentLocation: StateFlow<LocationInfo> = _currentLocation.asStateFlow()

    val bhubaneswarLocalities = com.example.data.model.BhubaneswarData.LOCALITIES

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var searchJob: Job? = null
    private var autoRefreshJob: Job? = null

    init {
        // Load initial or saved location
        val (savedLat, savedLon, savedName) = preferences.getLastLocation()
        val savedCountry = preferences.getLastLocationCountry()
        val savedAdmin = preferences.getLastLocationAdmin()
        val initialLoc = LocationInfo(
            id = "${savedLat}_${savedLon}",
            name = savedName,
            country = savedCountry,
            admin1 = savedAdmin,
            latitude = savedLat,
            longitude = savedLon
        )
        _currentLocation.value = initialLoc
        loadWeather(initialLoc)

        // Start periodic background refresh every 15 minutes
        startAutoRefresh()
    }

    fun loadWeather(location: LocationInfo, showLoadingState: Boolean = true) {
        viewModelScope.launch {
            if (showLoadingState) {
                _uiState.value = WeatherUiState.Loading
            }
            _currentLocation.value = location
            preferences.saveLastLocation(location.latitude, location.longitude, location.name, location.country, location.admin1)

            val result = repository.getWeatherData(location)
            result.onSuccess { data ->
                _uiState.value = WeatherUiState.Success(data)
            }.onFailure { error ->
                val errorMessage = error.localizedMessage ?: "Unable to fetch weather data. Check your internet connection."
                _uiState.value = WeatherUiState.Error(errorMessage, isNetworkError = true)
            }
        }
    }

    fun selectBhubaneswarArea(area: com.example.data.model.BhubaneswarArea) {
        val location = area.toLocationInfo()
        loadWeather(location)
        viewModelScope.launch {
            repository.addRecentSearch(location)
        }
    }

    fun refreshWeather() {
        viewModelScope.launch {
            _isRefreshing.value = true
            val loc = _currentLocation.value
            val result = repository.getWeatherData(loc)
            result.onSuccess { data ->
                _uiState.value = WeatherUiState.Success(data)
            }.onFailure { error ->
                // If we already have success data, don't overwrite with error, just stop refresh
                if (_uiState.value !is WeatherUiState.Success) {
                    _uiState.value = WeatherUiState.Error(error.localizedMessage ?: "Refresh failed")
                }
            }
            _isRefreshing.value = false
        }
    }

    fun searchLocations(query: String) {
        searchJob?.cancel()
        if (query.trim().length < 2) {
            _searchState.value = SearchUiState.Idle
            return
        }

        searchJob = viewModelScope.launch {
            delay(300) // Debounce
            _searchState.value = SearchUiState.Searching
            val result = repository.searchLocations(query)
            result.onSuccess { list ->
                if (list.isEmpty()) {
                    _searchState.value = SearchUiState.Empty
                } else {
                    _searchState.value = SearchUiState.Results(list)
                }
            }.onFailure {
                _searchState.value = SearchUiState.Empty
            }
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        _searchState.value = SearchUiState.Idle
    }

    fun selectLocation(location: LocationInfo) {
        clearSearch()
        viewModelScope.launch {
            repository.addRecentSearch(location)
        }
        loadWeather(location)
    }

    fun onGpsLocationObtained(latitude: Double, longitude: Double, cityName: String, countryName: String) {
        val gpsLoc = LocationInfo(
            id = "${latitude}_${longitude}",
            name = cityName,
            country = countryName,
            latitude = latitude,
            longitude = longitude,
            isCurrentLocation = true
        )
        selectLocation(gpsLoc)
    }

    fun toggleFavorite(location: LocationInfo) {
        viewModelScope.launch {
            val isFav = !location.isFavorite
            repository.toggleFavorite(location, isFav)
            // Update current state if matching
            val state = _uiState.value
            if (state is WeatherUiState.Success && state.data.location.id == location.id) {
                _uiState.value = state.copy(
                    data = state.data.copy(
                        location = state.data.location.copy(isFavorite = isFav)
                    )
                )
            }
        }
    }

    fun setUnit(unit: TemperatureUnit) {
        preferences.setUnit(unit)
    }

    fun setTheme(theme: ThemeMode) {
        preferences.setTheme(theme)
    }

    private fun startAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (true) {
                delay(15 * 60 * 1000L) // Every 15 minutes
                val loc = _currentLocation.value
                val result = repository.getWeatherData(loc)
                result.onSuccess { data ->
                    _uiState.value = WeatherUiState.Success(data)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        autoRefreshJob?.cancel()
        searchJob?.cancel()
    }
}
