package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BhubaneswarArea
import com.example.data.model.LocationInfo
import com.example.ui.theme.WeatherAtmosphereColors

@Composable
fun BhubaneswarLocalityChips(
    localities: List<BhubaneswarArea>,
    currentLocation: LocationInfo,
    atmosphere: WeatherAtmosphereColors,
    onSelectArea: (BhubaneswarArea) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("bhubaneswar_localities_section")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(atmosphere.accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Bhubaneswar Localities",
                        tint = atmosphere.accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Bhubaneswar Localities",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = atmosphere.primaryTextColor,
                        letterSpacing = 0.2.sp
                    )
                )
            }

            Text(
                text = "${localities.size} Areas",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = atmosphere.secondaryTextColor,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(localities, key = { it.name }) { area ->
                val isSelected = currentLocation.name.equals(area.localityName, ignoreCase = true) ||
                        currentLocation.name.equals(area.name, ignoreCase = true) ||
                        (Math.abs(currentLocation.latitude - area.latitude) < 0.005 &&
                                Math.abs(currentLocation.longitude - area.longitude) < 0.005)

                LocalityChipItem(
                    area = area,
                    isSelected = isSelected,
                    atmosphere = atmosphere,
                    onClick = { onSelectArea(area) }
                )
            }
        }
    }
}

@Composable
private fun LocalityChipItem(
    area: BhubaneswarArea,
    isSelected: Boolean,
    atmosphere: WeatherAtmosphereColors,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) {
            atmosphere.accentColor.copy(alpha = 0.22f)
        } else {
            atmosphere.surfaceColor
        },
        label = "chipBg"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) {
            atmosphere.accentColor
        } else {
            atmosphere.surfaceBorderColor
        },
        label = "chipBorder"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) {
            atmosphere.accentColor
        } else {
            atmosphere.primaryTextColor
        },
        label = "chipText"
    )

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("locality_chip_${area.name}"),
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(if (isSelected) 1.5.dp else 0.9.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.NearMe,
                    contentDescription = null,
                    tint = atmosphere.accentColor,
                    modifier = Modifier
                        .size(12.dp)
                        .padding(end = 4.dp)
                )
            }
            Column {
                Text(
                    text = area.name,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        color = textColor,
                        fontSize = 13.sp
                    )
                )
                Text(
                    text = area.landmark,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = atmosphere.secondaryTextColor,
                        fontSize = 9.sp
                    ),
                    maxLines = 1
                )
            }
        }
    }
}
