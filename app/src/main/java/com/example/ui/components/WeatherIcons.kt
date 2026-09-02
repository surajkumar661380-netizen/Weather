package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material.icons.outlined.CloudQueue
import androidx.compose.material.icons.outlined.Grain
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.WeatherConditionType

@Composable
fun WeatherConditionIcon(
    conditionType: WeatherConditionType,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    tint: Color? = null
) {
    val (icon, defaultTint) = when (conditionType) {
        WeatherConditionType.CLEAR_DAY -> Pair(Icons.Filled.WbSunny, Color(0xFFF59E0B))
        WeatherConditionType.CLEAR_NIGHT -> Pair(Icons.Filled.NightsStay, Color(0xFF93C5FD))
        WeatherConditionType.PARTLY_CLOUDY_DAY -> Pair(Icons.Filled.Cloud, Color(0xFF60A5FA))
        WeatherConditionType.PARTLY_CLOUDY_NIGHT -> Pair(Icons.Filled.NightsStay, Color(0xFF94A3B8))
        WeatherConditionType.CLOUDY -> Pair(Icons.Filled.Cloud, Color(0xFF94A3B8))
        WeatherConditionType.FOG -> Pair(Icons.Filled.Grain, Color(0xFF94A3B8))
        WeatherConditionType.DRIZZLE -> Pair(Icons.Filled.WaterDrop, Color(0xFF38BDF8))
        WeatherConditionType.RAIN -> Pair(Icons.Filled.Grain, Color(0xFF0284C7))
        WeatherConditionType.HEAVY_RAIN -> Pair(Icons.Filled.WaterDrop, Color(0xFF1D4ED8))
        WeatherConditionType.SNOW -> Pair(Icons.Outlined.AcUnit, Color(0xFF38BDF8))
        WeatherConditionType.THUNDERSTORM -> Pair(Icons.Filled.Thunderstorm, Color(0xFF8B5CF6))
    }

    Icon(
        imageVector = icon,
        contentDescription = conditionType.name,
        modifier = modifier.size(size),
        tint = tint ?: defaultTint
    )
}

@Composable
fun WeatherBadgeIcon(
    conditionType: WeatherConditionType,
    size: Dp = 72.dp,
    modifier: Modifier = Modifier
) {
    val gradient = when (conditionType) {
        WeatherConditionType.CLEAR_DAY -> Brush.linearGradient(
            listOf(Color(0xFFFDE68A), Color(0xFFF59E0B))
        )
        WeatherConditionType.CLEAR_NIGHT -> Brush.linearGradient(
            listOf(Color(0xFF334155), Color(0xFF1E293B))
        )
        WeatherConditionType.RAIN, WeatherConditionType.HEAVY_RAIN -> Brush.linearGradient(
            listOf(Color(0xFFBAE6FD), Color(0xFF0284C7))
        )
        WeatherConditionType.THUNDERSTORM -> Brush.linearGradient(
            listOf(Color(0xFFDDD6FE), Color(0xFF7C3AED))
        )
        WeatherConditionType.SNOW -> Brush.linearGradient(
            listOf(Color(0xFFE0F2FE), Color(0xFF38BDF8))
        )
        else -> Brush.linearGradient(
            listOf(Color(0xFFE2E8F0), Color(0xFF94A3B8))
        )
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        WeatherConditionIcon(
            conditionType = conditionType,
            size = size * 0.55f,
            tint = Color.White
        )
    }
}
