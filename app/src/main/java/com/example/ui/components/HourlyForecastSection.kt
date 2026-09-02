package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WaterDrop
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HourlyForecastItem
import com.example.data.model.TemperatureUnit
import com.example.data.model.toFahrenheit
import com.example.ui.theme.WeatherAtmosphereColors
import kotlin.math.roundToInt

@Composable
fun HourlyForecastSection(
    hourlyList: List<HourlyForecastItem>,
    unit: TemperatureUnit,
    atmosphere: WeatherAtmosphereColors,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("hourly_forecast_section"),
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
                .padding(vertical = 20.dp)
        ) {
            // Header Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = atmosphere.accentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Hourly Forecast (24h)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = atmosphere.primaryTextColor
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Horizontal Hourly Items
            LazyRow(
                contentPadding = PaddingValues(horizontal = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(hourlyList, key = { it.timeIso }) { item ->
                    HourlyItemCard(
                        item = item,
                        unit = unit,
                        atmosphere = atmosphere
                    )
                }
            }
        }
    }
}

@Composable
private fun HourlyItemCard(
    item: HourlyForecastItem,
    unit: TemperatureUnit,
    atmosphere: WeatherAtmosphereColors
) {
    val tempDisplay = if (unit == TemperatureUnit.CELSIUS) {
        item.temperature.roundToInt()
    } else {
        item.temperature.toFahrenheit().roundToInt()
    }

    val isNow = item.isNow
    val cardBg = if (isNow) {
        atmosphere.accentColor.copy(alpha = 0.12f)
    } else {
        atmosphere.accentColor.copy(alpha = 0.04f)
    }

    val borderColor = if (isNow) {
        atmosphere.accentColor.copy(alpha = 0.7f)
    } else {
        atmosphere.surfaceBorderColor
    }

    Surface(
        modifier = Modifier
            .width(76.dp)
            .clip(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = cardBg,
        border = androidx.compose.foundation.BorderStroke(if (isNow) 1.5.dp else 0.8.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Time Label
            Text(
                text = item.hourLabel,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isNow) FontWeight.Bold else FontWeight.Medium,
                    color = if (isNow) atmosphere.accentColor else atmosphere.secondaryTextColor,
                    fontSize = 12.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Weather Icon
            WeatherConditionIcon(
                conditionType = item.condition.type,
                size = 28.dp,
                tint = if (item.condition.type.name.contains("CLEAR")) Color(0xFFF59E0B) else atmosphere.accentColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Temperature
            Text(
                text = "$tempDisplay°",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = atmosphere.primaryTextColor,
                    fontSize = 15.sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Rain Probability Pill
            if (item.precipitationProbability > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = "Rain chance",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${item.precipitationProbability}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0284C7),
                            fontSize = 10.sp
                        )
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}
