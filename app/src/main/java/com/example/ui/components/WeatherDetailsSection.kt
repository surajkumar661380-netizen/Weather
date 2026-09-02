package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurrentWeather
import com.example.data.model.TemperatureUnit
import com.example.data.model.hpaToInHg
import com.example.data.model.kmhToMph
import com.example.data.model.toFahrenheit
import com.example.ui.theme.WeatherAtmosphereColors
import kotlin.math.roundToInt

@Composable
fun WeatherDetailsSection(
    current: CurrentWeather,
    unit: TemperatureUnit,
    atmosphere: WeatherAtmosphereColors,
    modifier: Modifier = Modifier
) {
    val windDisplay = if (unit == TemperatureUnit.CELSIUS) {
        "${current.windSpeed.roundToInt()} km/h"
    } else {
        "${current.windSpeed.kmhToMph().roundToInt()} mph"
    }

    val gustDisplay = if (unit == TemperatureUnit.CELSIUS) {
        "${current.windGusts.roundToInt()} km/h"
    } else {
        "${current.windGusts.kmhToMph().roundToInt()} mph"
    }

    val pressureDisplay = if (unit == TemperatureUnit.CELSIUS) {
        "${current.pressure.roundToInt()} hPa"
    } else {
        String.format("%.2f inHg", current.pressure.hpaToInHg())
    }

    val dewPointDisplay = if (unit == TemperatureUnit.CELSIUS) {
        "${current.dewPoint.roundToInt()}°C"
    } else {
        "${current.dewPoint.toFahrenheit().roundToInt()}°F"
    }

    val uvLevel = when {
        current.uvIndex < 3 -> Pair("Low", Color(0xFF10B981))
        current.uvIndex < 6 -> Pair("Moderate", Color(0xFFF59E0B))
        current.uvIndex < 8 -> Pair("High", Color(0xFFF97316))
        current.uvIndex < 11 -> Pair("Very High", Color(0xFFEF4444))
        else -> Pair("Extreme", Color(0xFF8B5CF6))
    }

    val humidityComfort = when {
        current.humidity < 30 -> "Dry air"
        current.humidity in 30..60 -> "Optimal comfort"
        current.humidity in 61..80 -> "Moderate humidity"
        else -> "High humidity"
    }

    val visibilityRating = when {
        current.visibility >= 10 -> "Excellent clarity"
        current.visibility >= 6 -> "Good visibility"
        current.visibility >= 3 -> "Moderate haze"
        else -> "Poor visibility"
    }

    val windDirectionCompass = getCompassDirection(current.windDirection)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weather_details_section"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = atmosphere.surfaceColor
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, atmosphere.surfaceBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ) {
            // Section Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = atmosphere.accentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Weather Details & Metrics",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = atmosphere.primaryTextColor
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grid of Cards (2 columns)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // UV Index Card
                DetailItemCard(
                    icon = Icons.Default.WbSunny,
                    title = "UV Index",
                    value = String.format("%.1f", current.uvIndex),
                    subtitle = uvLevel.first,
                    badgeColor = uvLevel.second,
                    atmosphere = atmosphere,
                    modifier = Modifier.weight(1f)
                )

                // Humidity Card
                DetailItemCard(
                    icon = Icons.Default.Opacity,
                    title = "Humidity",
                    value = "${current.humidity}%",
                    subtitle = humidityComfort,
                    badgeColor = Color(0xFF0284C7),
                    atmosphere = atmosphere,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Wind Card with Direction
                DetailItemCard(
                    icon = Icons.Default.Air,
                    title = "Wind",
                    value = windDisplay,
                    subtitle = "$windDirectionCompass (${current.windDirection.roundToInt()}°)",
                    extra = if (current.windGusts > current.windSpeed) "Gusts $gustDisplay" else null,
                    atmosphere = atmosphere,
                    modifier = Modifier.weight(1f)
                )

                // Pressure Card
                DetailItemCard(
                    icon = Icons.Default.Compress,
                    title = "Pressure",
                    value = pressureDisplay,
                    subtitle = if (current.pressure > 1013) "High barometric" else "Normal",
                    atmosphere = atmosphere,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Visibility Card
                DetailItemCard(
                    icon = Icons.Default.Visibility,
                    title = "Visibility",
                    value = "${current.visibility.roundToInt()} km",
                    subtitle = visibilityRating,
                    atmosphere = atmosphere,
                    modifier = Modifier.weight(1f)
                )

                // Dew Point / Cloud Cover
                DetailItemCard(
                    icon = Icons.Default.Cloud,
                    title = "Cloud Cover",
                    value = "${current.cloudCover}%",
                    subtitle = "Dew point: $dewPointDisplay",
                    atmosphere = atmosphere,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sun Cycle (Sunrise & Sunset) Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = atmosphere.accentColor.copy(alpha = 0.05f),
                border = androidx.compose.foundation.BorderStroke(0.8.dp, atmosphere.surfaceBorderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sunrise
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEF3C7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WbTwilight,
                                contentDescription = "Sunrise",
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Sunrise",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = atmosphere.secondaryTextColor
                                )
                            )
                            Text(
                                text = current.sunrise,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = atmosphere.primaryTextColor
                                )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(atmosphere.surfaceBorderColor)
                    )

                    // Sunset
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEDE9FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WbTwilight,
                                contentDescription = "Sunset",
                                tint = Color(0xFF8B5CF6),
                                modifier = Modifier
                                    .size(22.dp)
                                    .rotate(180f)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Sunset",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = atmosphere.secondaryTextColor
                                )
                            )
                            Text(
                                text = current.sunset,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = atmosphere.primaryTextColor
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailItemCard(
    icon: ImageVector,
    title: String,
    value: String,
    subtitle: String,
    extra: String? = null,
    badgeColor: Color? = null,
    atmosphere: WeatherAtmosphereColors,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = atmosphere.accentColor.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, atmosphere.surfaceBorderColor)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = atmosphere.accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = atmosphere.secondaryTextColor,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                if (badgeColor != null) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(badgeColor)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = atmosphere.primaryTextColor
                )
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = atmosphere.secondaryTextColor,
                    fontSize = 12.sp
                )
            )

            if (extra != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = extra,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = atmosphere.accentColor,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

private fun getCompassDirection(degrees: Double): String {
    val directions = arrayOf("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW")
    val index = (((degrees % 360) + 360) % 360 / 22.5 + 0.5).toInt() % 16
    return directions[index]
}
