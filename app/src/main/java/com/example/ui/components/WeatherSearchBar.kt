package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LocationInfo
import com.example.ui.theme.WeatherAtmosphereColors
import com.example.ui.viewmodel.SearchUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WeatherSearchBar(
    isVisible: Boolean,
    searchState: SearchUiState,
    recentSearches: List<LocationInfo>,
    atmosphere: WeatherAtmosphereColors,
    onQueryChange: (String) -> Unit,
    onLocationSelected: (LocationInfo) -> Unit,
    onUseCurrentLocation: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(isVisible) {
        if (isVisible) {
            focusRequester.requestFocus()
        } else {
            searchQuery = ""
            focusManager.clearFocus()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            shape = RoundedCornerShape(20.dp),
            color = atmosphere.surfaceColor,
            border = androidx.compose.foundation.BorderStroke(1.dp, atmosphere.surfaceBorderColor),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Search Input Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        onQueryChange(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .testTag("search_text_input"),
                    placeholder = {
                        Text(
                            "Search city, region or country...",
                            color = atmosphere.secondaryTextColor.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = atmosphere.accentColor
                        )
                    },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = {
                                    searchQuery = ""
                                    onQueryChange("")
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = atmosphere.secondaryTextColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            IconButton(onClick = onDismiss) {
                                Text(
                                    "Close",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = atmosphere.secondaryTextColor
                                )
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        focusManager.clearFocus()
                    }),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = atmosphere.accentColor,
                        unfocusedBorderColor = atmosphere.surfaceBorderColor,
                        focusedTextColor = atmosphere.primaryTextColor,
                        unfocusedTextColor = atmosphere.primaryTextColor,
                        cursorColor = atmosphere.accentColor
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Action: GPS Location
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(atmosphere.accentColor.copy(alpha = 0.08f))
                        .clickable {
                            onUseCurrentLocation()
                            onDismiss()
                        }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(atmosphere.accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "GPS Location",
                            tint = atmosphere.accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Use Current Location",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = atmosphere.primaryTextColor
                            )
                        )
                        Text(
                            text = "Get real-time forecast via GPS coordinates",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = atmosphere.secondaryTextColor,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Bhubaneswar Localities Quick Suggestion Chips (when search is idle/blank)
                if (searchQuery.isBlank()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Bhubaneswar Localities",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = atmosphere.accentColor
                            )
                        )
                        Text(
                            text = "Instant weather",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = atmosphere.secondaryTextColor,
                                fontSize = 10.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        com.example.data.model.BhubaneswarData.LOCALITIES.take(8).forEach { area ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = atmosphere.accentColor.copy(alpha = 0.10f),
                                border = androidx.compose.foundation.BorderStroke(0.8.dp, atmosphere.accentColor.copy(alpha = 0.35f)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        onLocationSelected(area.toLocationInfo())
                                        onDismiss()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = atmosphere.accentColor,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = area.name,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = atmosphere.primaryTextColor,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Recent Searches Chips
                if (recentSearches.isNotEmpty() && searchQuery.isBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Recent Searches",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = atmosphere.secondaryTextColor
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        recentSearches.forEach { location ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = atmosphere.accentColor.copy(alpha = 0.08f),
                                border = androidx.compose.foundation.BorderStroke(0.8.dp, atmosphere.surfaceBorderColor),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        onLocationSelected(location)
                                        onDismiss()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = null,
                                        tint = atmosphere.secondaryTextColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = location.name,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Medium,
                                            color = atmosphere.primaryTextColor
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Search Results State
                when (searchState) {
                    is SearchUiState.Searching -> {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = atmosphere.accentColor
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Searching locations...",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = atmosphere.secondaryTextColor
                                )
                            )
                        }
                    }
                    is SearchUiState.Results -> {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Suggestions",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = atmosphere.secondaryTextColor
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 240.dp)
                        ) {
                            items(searchState.list, key = { it.id }) { loc ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            onLocationSelected(loc)
                                            onDismiss()
                                        }
                                        .padding(horizontal = 8.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = atmosphere.accentColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = loc.name,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = atmosphere.primaryTextColor
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = listOfNotNull(loc.admin1, loc.country).filter { it.isNotBlank() }.joinToString(", "),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = atmosphere.secondaryTextColor
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                    is SearchUiState.Empty -> {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No locations found for \"$searchQuery\". Try checking the spelling.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = atmosphere.secondaryTextColor
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                        )
                    }
                    is SearchUiState.Idle -> {
                        // Idle
                    }
                }
            }
        }
    }
}
