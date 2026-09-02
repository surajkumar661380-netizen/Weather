package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.location.LocationHelper
import com.example.data.model.LocationInfo
import com.example.data.model.TemperatureUnit
import com.example.data.model.ThemeMode
import com.example.ui.components.BhubaneswarLocalityChips
import com.example.ui.components.CurrentWeatherCard
import com.example.ui.components.DailyForecastSection
import com.example.ui.components.ErrorMessageCard
import com.example.ui.components.HourlyForecastSection
import com.example.ui.components.LoadingSkeleton
import com.example.ui.components.SavedCitiesSheet
import com.example.ui.components.WeatherDetailsSection
import com.example.ui.components.WeatherHeader
import com.example.ui.components.WeatherSearchBar
import com.example.ui.theme.WeatherAtmosphere
import com.example.ui.viewmodel.WeatherUiState
import com.example.ui.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val locationHelper = remember { LocationHelper(context) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchState by viewModel.searchState.collectAsStateWithLifecycle()
    val unit by viewModel.temperatureUnit.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val favoriteCities by viewModel.favoriteCities.collectAsStateWithLifecycle()
    val recentSearches by viewModel.recentSearches.collectAsStateWithLifecycle()
    val currentLocation by viewModel.currentLocation.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    var isSearchVisible by remember { mutableStateOf(false) }
    var isSavedCitiesVisible by remember { mutableStateOf(false) }

    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }

    val currentConditionType = (uiState as? WeatherUiState.Success)?.data?.current?.condition?.type
    val atmosphere = remember(currentConditionType, isDark) {
        WeatherAtmosphere.getAtmosphere(currentConditionType, isDark)
    }

    // Permission launcher for GPS Location
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineGranted || coarseGranted) {
            scope.launch {
                val coords = locationHelper.getCurrentCoordinates()
                if (coords != null) {
                    val (cityName, countryName) = locationHelper.getCityNameFromCoordinates(coords.first, coords.second)
                    viewModel.onGpsLocationObtained(coords.first, coords.second, cityName, countryName)
                } else {
                    snackbarHostState.showSnackbar("Unable to determine current GPS location. Please try again.")
                }
            }
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Location permission was denied. You can still search for cities manually.")
            }
        }
    }

    fun requestGpsLocation() {
        val finePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarsePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (finePermission == PackageManager.PERMISSION_GRANTED || coarsePermission == PackageManager.PERMISSION_GRANTED) {
            scope.launch {
                val coords = locationHelper.getCurrentCoordinates()
                if (coords != null) {
                    val (cityName, countryName) = locationHelper.getCityNameFromCoordinates(coords.first, coords.second)
                    viewModel.onGpsLocationObtained(coords.first, coords.second, cityName, countryName)
                } else {
                    snackbarHostState.showSnackbar("Unable to fetch current GPS coordinates.")
                }
            }
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent,
        modifier = modifier
            .fillMaxSize()
            .background(atmosphere.backgroundBrush)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 700.dp) // Perfect responsive constraint on tablets
                    .padding(horizontal = 14.dp)
            ) {
                // Top Weather Header
                WeatherHeader(
                    location = (uiState as? WeatherUiState.Success)?.data?.location ?: currentLocation,
                    unit = unit,
                    themeMode = themeMode,
                    isRefreshing = isRefreshing,
                    atmosphere = atmosphere,
                    onUnitToggle = {
                        val nextUnit = if (unit == TemperatureUnit.CELSIUS) TemperatureUnit.FAHRENHEIT else TemperatureUnit.CELSIUS
                        viewModel.setUnit(nextUnit)
                    },
                    onThemeToggle = {
                        val nextTheme = when (themeMode) {
                            ThemeMode.SYSTEM -> ThemeMode.DARK
                            ThemeMode.DARK -> ThemeMode.LIGHT
                            ThemeMode.LIGHT -> ThemeMode.SYSTEM
                        }
                        viewModel.setTheme(nextTheme)
                    },
                    onRefresh = { viewModel.refreshWeather() },
                    onSearchClick = { isSearchVisible = !isSearchVisible },
                    onSavedCitiesClick = { isSavedCitiesVisible = true },
                    onToggleFavorite = {
                        val loc = (uiState as? WeatherUiState.Success)?.data?.location ?: currentLocation
                        viewModel.toggleFavorite(loc)
                    }
                )

                // Search Bar Overlay
                WeatherSearchBar(
                    isVisible = isSearchVisible,
                    searchState = searchState,
                    recentSearches = recentSearches,
                    atmosphere = atmosphere,
                    onQueryChange = { viewModel.searchLocations(it) },
                    onLocationSelected = { loc ->
                        viewModel.selectLocation(loc)
                        isSearchVisible = false
                    },
                    onUseCurrentLocation = { requestGpsLocation() },
                    onDismiss = {
                        isSearchVisible = false
                        viewModel.clearSearch()
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Bhubaneswar Localities Quick-Picker Row
                BhubaneswarLocalityChips(
                    localities = viewModel.bhubaneswarLocalities,
                    currentLocation = (uiState as? WeatherUiState.Success)?.data?.location ?: currentLocation,
                    atmosphere = atmosphere,
                    onSelectArea = { area ->
                        viewModel.selectBhubaneswarArea(area)
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Offline Notice Banner
                val isOffline = (uiState as? WeatherUiState.Success)?.isOffline == true
                if (isOffline) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF3D2A1C),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6E4D3E)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudOff,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Viewing cached offline data. Reconnect to refresh.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFFDF8F5)
                                )
                            )
                        }
                    }
                }

                // Main Weather Content Area with Crossfade
                Crossfade(
                    targetState = uiState,
                    label = "uiStateCrossfade",
                    modifier = Modifier.fillMaxSize()
                ) { state ->
                    when (state) {
                        is WeatherUiState.Loading -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                item {
                                    LoadingSkeleton(atmosphere = atmosphere)
                                }
                            }
                        }

                        is WeatherUiState.Success -> {
                            val data = state.data
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // 1. Current Weather Hero
                                item {
                                    CurrentWeatherCard(
                                        current = data.current,
                                        unit = unit,
                                        atmosphere = atmosphere
                                    )
                                }

                                // 2. Hourly Forecast (24 Hours)
                                if (data.hourly.isNotEmpty()) {
                                    item {
                                        HourlyForecastSection(
                                            hourlyList = data.hourly,
                                            unit = unit,
                                            atmosphere = atmosphere
                                        )
                                    }
                                }

                                // 3. 7-Day Daily Forecast
                                if (data.daily.isNotEmpty()) {
                                    item {
                                        DailyForecastSection(
                                            dailyList = data.daily,
                                            unit = unit,
                                            atmosphere = atmosphere
                                        )
                                    }
                                }

                                // 4. Weather Details & Metrics
                                item {
                                    WeatherDetailsSection(
                                        current = data.current,
                                        unit = unit,
                                        atmosphere = atmosphere
                                    )
                                }

                                // Bottom Spacing
                                item {
                                    Spacer(modifier = Modifier.height(24.dp))
                                }
                            }
                        }

                        is WeatherUiState.Error -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                ErrorMessageCard(
                                    message = state.message,
                                    atmosphere = atmosphere,
                                    onRetry = { viewModel.refreshWeather() },
                                    onUseDefaultCity = {
                                        viewModel.selectLocation(
                                            LocationInfo(
                                                id = "default_bbsr",
                                                name = "Bhubaneswar",
                                                country = "India",
                                                admin1 = "Odisha",
                                                latitude = 20.2961,
                                                longitude = 85.8245
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Saved Cities Bottom Sheet
        SavedCitiesSheet(
            isVisible = isSavedCitiesVisible,
            favoriteCities = favoriteCities,
            currentLocation = (uiState as? WeatherUiState.Success)?.data?.location ?: currentLocation,
            atmosphere = atmosphere,
            onLocationSelected = { loc ->
                viewModel.selectLocation(loc)
            },
            onToggleFavorite = { loc ->
                viewModel.toggleFavorite(loc)
            },
            onDismiss = { isSavedCitiesVisible = false }
        )
    }
}
