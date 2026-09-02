package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.WeatherAtmosphereColors

@Composable
fun ShimmerBrush(
    isDark: Boolean
): Brush {
    val shimmerColors = if (isDark) {
        listOf(
            Color(0xFF281C18).copy(alpha = 0.7f),
            Color(0xFF422F28).copy(alpha = 0.5f),
            Color(0xFF281C18).copy(alpha = 0.7f)
        )
    } else {
        listOf(
            Color(0xFFEFE4D8).copy(alpha = 0.6f),
            Color(0xFFFAF5EE).copy(alpha = 0.8f),
            Color(0xFFEFE4D8).copy(alpha = 0.6f)
        )
    }

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )
}

@Composable
fun LoadingSkeleton(
    atmosphere: WeatherAtmosphereColors,
    modifier: Modifier = Modifier
) {
    val shimmer = ShimmerBrush(isDark = atmosphere.isDarkBackground)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("loading_skeleton"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Weather Card Skeleton
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = atmosphere.surfaceColor),
            border = androidx.compose.foundation.BorderStroke(1.dp, atmosphere.surfaceBorderColor)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(24.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(shimmer)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .width(180.dp)
                                .height(14.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(shimmer)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(shimmer)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Box(
                        modifier = Modifier
                            .width(140.dp)
                            .height(60.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(shimmer)
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        Box(
                            modifier = Modifier
                                .width(90.dp)
                                .height(16.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(shimmer)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .width(70.dp)
                                .height(14.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(shimmer)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(4) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(shimmer)
                        )
                    }
                }
            }
        }

        // Hourly Forecast Skeleton
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = atmosphere.surfaceColor),
            border = androidx.compose.foundation.BorderStroke(1.dp, atmosphere.surfaceBorderColor)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Box(
                    modifier = Modifier
                        .width(160.dp)
                        .height(20.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(shimmer)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    repeat(5) {
                        Box(
                            modifier = Modifier
                                .width(64.dp)
                                .height(100.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(shimmer)
                        )
                    }
                }
            }
        }

        // Daily Forecast Skeleton
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = atmosphere.surfaceColor),
            border = androidx.compose.foundation.BorderStroke(1.dp, atmosphere.surfaceBorderColor)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(20.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(shimmer)
                )
                Spacer(modifier = Modifier.height(16.dp))
                repeat(4) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(16.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(shimmer)
                        )
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(shimmer)
                        )
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(shimmer)
                        )
                    }
                }
            }
        }
    }
}
