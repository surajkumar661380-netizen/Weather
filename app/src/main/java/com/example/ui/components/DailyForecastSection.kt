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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyForecastItem
import com.example.data.model.TemperatureUnit
import com.example.data.model.toFahrenheit
import com.example.ui.theme.WeatherAtmosphereColors
import kotlin.math.roundToInt

@Composable
fun DailyForecastSection(
    dailyList: List<DailyForecastItem>,
    unit: TemperatureUnit,
    atmosphere: WeatherAtmosphereColors,
    modifier: Modifier = Modifier
) {
    val overallMin = dailyList.minOfOrNull { it.minTemp } ?: 0.0
    val overallMax = dailyList.maxOfOrNull { it.maxTemp } ?: 30.0
    val tempRange = (overallMax - overallMin).coerceAtLeast(1.0)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_forecast_section"),
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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = atmosphere.accentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "7-Day Forecast",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = atmosphere.primaryTextColor
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Daily Rows
            dailyList.forEachIndexed { index, item ->
                DailyItemRow(
                    item = item,
                    unit = unit,
                    overallMin = overallMin,
                    overallMax = overallMax,
                    tempRange = tempRange,
                    atmosphere = atmosphere
                )
                if (index < dailyList.lastIndex) {
                    HorizontalDivider(
                        color = atmosphere.surfaceBorderColor.copy(alpha = 0.6f),
                        thickness = 0.8.dp,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyItemRow(
    item: DailyForecastItem,
    unit: TemperatureUnit,
    overallMin: Double,
    overallMax: Double,
    tempRange: Double,
    atmosphere: WeatherAtmosphereColors
) {
    val minDisplay = if (unit == TemperatureUnit.CELSIUS) {
        item.minTemp.roundToInt()
    } else {
        item.minTemp.toFahrenheit().roundToInt()
    }

    val maxDisplay = if (unit == TemperatureUnit.CELSIUS) {
        item.maxTemp.roundToInt()
    } else {
        item.maxTemp.toFahrenheit().roundToInt()
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Day Label & Date
        Column(modifier = Modifier.width(86.dp)) {
            Text(
                text = item.dayLabel,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = atmosphere.primaryTextColor
                )
            )
            Text(
                text = item.formattedDate,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = atmosphere.secondaryTextColor,
                    fontSize = 11.sp
                )
            )
        }

        // Weather Icon & Rain probability
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(68.dp)
        ) {
            WeatherConditionIcon(
                conditionType = item.condition.type,
                size = 24.dp,
                tint = if (item.condition.type.name.contains("CLEAR")) Color(0xFFF59E0B) else atmosphere.accentColor
            )
            if (item.precipitationProbability > 10) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${item.precipitationProbability}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0284C7),
                        fontSize = 10.sp
                    )
                )
            }
        }

        // Temperature Bar Visualizer
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "$minDisplay°",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = atmosphere.secondaryTextColor
                ),
                modifier = Modifier.width(30.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Temperature Gradient Track
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(atmosphere.surfaceBorderColor)
            ) {
                val widthFraction = ((item.maxTemp - item.minTemp) / tempRange).toFloat().coerceIn(0.15f, 1f)

                Box(
                    modifier = Modifier
                        .fillMaxWidth(widthFraction)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF38BDF8), Color(0xFFF59E0B), Color(0xFFEF4444))
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "$maxDisplay°",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = atmosphere.primaryTextColor
                ),
                modifier = Modifier.width(30.dp)
            )
        }
    }
}
