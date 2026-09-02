package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LocationInfo
import com.example.data.model.TemperatureUnit
import com.example.data.model.ThemeMode
import com.example.ui.theme.WeatherAtmosphereColors

@Composable
fun WeatherHeader(
    location: LocationInfo,
    unit: TemperatureUnit,
    themeMode: ThemeMode,
    isRefreshing: Boolean,
    atmosphere: WeatherAtmosphereColors,
    onUnitToggle: () -> Unit,
    onThemeToggle: () -> Unit,
    onRefresh: () -> Unit,
    onSearchClick: () -> Unit,
    onSavedCitiesClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "refreshRotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing)
        ),
        label = "rotationAngle"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Location Badge (clickable to search)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onSearchClick() }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(atmosphere.accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (location.isCurrentLocation) Icons.Default.NearMe else Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = atmosphere.accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = location.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = atmosphere.primaryTextColor
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (location.isCurrentLocation) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = atmosphere.accentColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "GPS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = atmosphere.accentColor,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    if (location.country.isNotBlank() || location.admin1 != null) {
                        Text(
                            text = location.admin1?.let { "$it, ${location.country}" } ?: location.country,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = atmosphere.secondaryTextColor
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Quick Actions Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Favorite / Bookmark Button
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("favorite_button")
                ) {
                    Icon(
                        imageVector = if (location.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Save City",
                        tint = if (location.isFavorite) atmosphere.accentColor else atmosphere.secondaryTextColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Search Icon
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("search_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Location",
                        tint = atmosphere.secondaryTextColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Saved Cities Sheet trigger
                IconButton(
                    onClick = onSavedCitiesClick,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("saved_cities_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, atmosphere.surfaceBorderColor, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "★",
                            fontSize = 14.sp,
                            color = atmosphere.secondaryTextColor
                        )
                    }
                }

                // Unit Toggle Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(atmosphere.surfaceColor)
                        .border(1.dp, atmosphere.surfaceBorderColor, RoundedCornerShape(12.dp))
                        .clickable { onUnitToggle() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("unit_toggle_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (unit == TemperatureUnit.CELSIUS) "°C" else "°F",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = atmosphere.accentColor
                        )
                    )
                }

                // Refresh Button
                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("refresh_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = atmosphere.secondaryTextColor,
                        modifier = Modifier
                            .size(22.dp)
                            .rotate(if (isRefreshing) rotation else 0f)
                    )
                }
            }
        }
    }
}
