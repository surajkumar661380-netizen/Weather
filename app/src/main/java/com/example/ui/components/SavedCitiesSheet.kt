package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BhubaneswarData
import com.example.data.model.LocationInfo
import com.example.ui.theme.WeatherAtmosphereColors

private val DEFAULT_POPULAR_CITIES = listOf(
    LocationInfo(id = "bbsr_main", name = "Bhubaneswar", country = "India", admin1 = "Odisha", latitude = 20.2961, longitude = 85.8245),
    LocationInfo(id = "51.5074_-0.1278", name = "London", country = "United Kingdom", latitude = 51.5074, longitude = -0.1278),
    LocationInfo(id = "40.7128_-74.006", name = "New York", country = "United States", latitude = 40.7128, longitude = -74.0060),
    LocationInfo(id = "35.6762_139.6503", name = "Tokyo", country = "Japan", latitude = 35.6762, longitude = 139.6503),
    LocationInfo(id = "28.6139_77.209", name = "New Delhi", country = "India", latitude = 28.6139, longitude = 77.2090),
    LocationInfo(id = "19.0760_72.8777", name = "Mumbai", country = "India", latitude = 19.0760, longitude = 72.8777),
    LocationInfo(id = "48.8566_2.3522", name = "Paris", country = "France", latitude = 48.8566, longitude = 2.3522)
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SavedCitiesSheet(
    isVisible: Boolean,
    favoriteCities: List<LocationInfo>,
    currentLocation: LocationInfo,
    atmosphere: WeatherAtmosphereColors,
    onLocationSelected: (LocationInfo) -> Unit,
    onToggleFavorite: (LocationInfo) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = atmosphere.surfaceColor,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = modifier.testTag("saved_cities_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(atmosphere.accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = atmosphere.accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Explore & Saved Places",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = atmosphere.primaryTextColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Bhubaneswar Localities Section
                item {
                    Text(
                        text = "Bhubaneswar Localities & Zones",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = atmosphere.accentColor
                        ),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        BhubaneswarData.LOCALITIES.forEach { area ->
                            val isSelected = currentLocation.name.equals(area.localityName, ignoreCase = true) ||
                                    currentLocation.name.equals(area.name, ignoreCase = true)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) atmosphere.accentColor.copy(alpha = 0.22f) else atmosphere.accentColor.copy(alpha = 0.07f),
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isSelected) 1.5.dp else 0.8.dp,
                                    if (isSelected) atmosphere.accentColor else atmosphere.surfaceBorderColor
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        onLocationSelected(area.toLocationInfo())
                                        onDismiss()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) Icons.Default.NearMe else Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = atmosphere.accentColor,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = area.name,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                            color = if (isSelected) atmosphere.accentColor else atmosphere.primaryTextColor,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    HorizontalDivider(
                        color = atmosphere.surfaceBorderColor,
                        thickness = 0.8.dp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }

                if (favoriteCities.isNotEmpty()) {
                    item {
                        Text(
                            text = "Your Saved Locations",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = atmosphere.secondaryTextColor
                            ),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    items(favoriteCities, key = { it.id }) { city ->
                        val isSelected = city.name.equals(currentLocation.name, ignoreCase = true)
                        CityItemCard(
                            city = city,
                            isSelected = isSelected,
                            isFavorite = true,
                            atmosphere = atmosphere,
                            onSelect = {
                                onLocationSelected(city)
                                onDismiss()
                            },
                            onToggleFavorite = { onToggleFavorite(city) }
                        )
                    }

                    item {
                        HorizontalDivider(
                            color = atmosphere.surfaceBorderColor,
                            thickness = 0.8.dp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                }

                item {
                    Text(
                        text = "Popular Global Cities",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = atmosphere.secondaryTextColor
                        ),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                items(DEFAULT_POPULAR_CITIES, key = { it.id }) { city ->
                    val isFav = favoriteCities.any { it.name.equals(city.name, ignoreCase = true) }
                    val isSelected = city.name.equals(currentLocation.name, ignoreCase = true)
                    CityItemCard(
                        city = city.copy(isFavorite = isFav),
                        isSelected = isSelected,
                        isFavorite = isFav,
                        atmosphere = atmosphere,
                        onSelect = {
                            onLocationSelected(city)
                            onDismiss()
                        },
                        onToggleFavorite = { onToggleFavorite(city) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun CityItemCard(
    city: LocationInfo,
    isSelected: Boolean,
    isFavorite: Boolean,
    atmosphere: WeatherAtmosphereColors,
    onSelect: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) atmosphere.accentColor.copy(alpha = 0.15f) else atmosphere.accentColor.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 1.5.dp else 0.8.dp,
            if (isSelected) atmosphere.accentColor else atmosphere.surfaceBorderColor
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onSelect() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationCity,
                    contentDescription = null,
                    tint = if (isSelected) atmosphere.accentColor else atmosphere.secondaryTextColor,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = city.name,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                            color = atmosphere.primaryTextColor
                        )
                    )
                    Text(
                        text = listOfNotNull(city.admin1, city.country).filter { it.isNotBlank() }.joinToString(", "),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = atmosphere.secondaryTextColor,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.Add,
                    contentDescription = "Toggle favorite",
                    tint = if (isFavorite) atmosphere.accentColor else atmosphere.secondaryTextColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
