package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Visibility
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
fun CurrentWeatherCard(
    current: CurrentWeather,
    unit: TemperatureUnit,
    atmosphere: WeatherAtmosphereColors,
    modifier: Modifier = Modifier
) {
    val tempDisplay = if (unit == TemperatureUnit.CELSIUS) {
        current.temperature.roundToInt()
    } else {
        current.temperature.toFahrenheit().roundToInt()
    }

    val feelsLikeDisplay = if (unit == TemperatureUnit.CELSIUS) {
        current.feelsLike.roundToInt()
    } else {
        current.feelsLike.toFahrenheit().roundToInt()
    }

    val maxTempDisplay = if (unit == TemperatureUnit.CELSIUS) {
        current.tempMaxToday.roundToInt()
    } else {
        current.tempMaxToday.toFahrenheit().roundToInt()
    }

    val minTempDisplay = if (unit == TemperatureUnit.CELSIUS) {
        current.tempMinToday.roundToInt()
    } else {
        current.tempMinToday.toFahrenheit().roundToInt()
    }

    val windDisplay = if (unit == TemperatureUnit.CELSIUS) {
        "${current.windSpeed.roundToInt()} km/h"
    } else {
        "${current.windSpeed.kmhToMph().roundToInt()} mph"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("current_weather_card"),
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
            // Top Condition & Graphic Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = atmosphere.accentColor.copy(alpha = 0.10f),
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, atmosphere.accentColor.copy(alpha = 0.25f)),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            WeatherConditionIcon(
                                conditionType = current.condition.type,
                                size = 16.dp,
                                tint = atmosphere.accentColor
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = current.condition.title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = atmosphere.accentColor,
                                    letterSpacing = 0.2.sp
                                )
                            )
                        }
                    }

                    Text(
                        text = current.condition.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = atmosphere.secondaryTextColor,
                            lineHeight = 16.sp
                        ),
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Modern Circular Badge
                WeatherBadgeIcon(
                    conditionType = current.condition.type,
                    size = 68.dp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Main Temperature Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    AnimatedContent(
                        targetState = tempDisplay,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "tempAnim"
                    ) { temp ->
                        Text(
                            text = "$temp",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 64.sp,
                                fontWeight = FontWeight.Bold,
                                color = atmosphere.primaryTextColor,
                                letterSpacing = (-2).sp
                            )
                        )
                    }
                    Text(
                        text = unit.symbol,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = atmosphere.accentColor
                        ),
                        modifier = Modifier.padding(top = 8.dp, start = 2.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Text(
                        text = "Feels like $feelsLikeDisplay${unit.symbol}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = atmosphere.primaryTextColor
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "H: $maxTempDisplay°",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444)
                            )
                        )
                        Text(
                            text = "•",
                            color = atmosphere.secondaryTextColor
                        )
                        Text(
                            text = "L: $minTempDisplay°",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3B82F6)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Snapshot Metric Pills Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SnapshotPill(
                    icon = Icons.Default.Opacity,
                    label = "Humidity",
                    value = "${current.humidity}%",
                    atmosphere = atmosphere,
                    modifier = Modifier.weight(1f)
                )
                SnapshotPill(
                    icon = Icons.Default.Air,
                    label = "Wind",
                    value = windDisplay,
                    atmosphere = atmosphere,
                    modifier = Modifier.weight(1f)
                )
                SnapshotPill(
                    icon = Icons.Default.Compress,
                    label = "Pressure",
                    value = "${current.pressure.roundToInt()} hPa",
                    atmosphere = atmosphere,
                    modifier = Modifier.weight(1f)
                )
                SnapshotPill(
                    icon = Icons.Default.Visibility,
                    label = "Visibility",
                    value = "${current.visibility.roundToInt()} km",
                    atmosphere = atmosphere,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SnapshotPill(
    icon: ImageVector,
    label: String,
    value: String,
    atmosphere: WeatherAtmosphereColors,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = atmosphere.accentColor.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, atmosphere.surfaceBorderColor)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = atmosphere.accentColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = atmosphere.primaryTextColor,
                    fontSize = 11.sp
                ),
                maxLines = 1
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = atmosphere.secondaryTextColor,
                    fontSize = 9.sp
                ),
                maxLines = 1
            )
        }
    }
}
