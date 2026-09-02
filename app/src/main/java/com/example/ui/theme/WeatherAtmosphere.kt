package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.data.model.WeatherConditionType

data class WeatherAtmosphereColors(
    val backgroundBrush: Brush,
    val surfaceColor: Color,
    val surfaceBorderColor: Color,
    val cardGradient: Brush,
    val primaryTextColor: Color,
    val secondaryTextColor: Color,
    val accentColor: Color,
    val isDarkBackground: Boolean
)

object WeatherAtmosphere {

    fun getAtmosphere(
        conditionType: WeatherConditionType?,
        isDarkTheme: Boolean
    ): WeatherAtmosphereColors {
        return if (isDarkTheme) {
            getDarkAtmosphere(conditionType)
        } else {
            getLightAtmosphere(conditionType)
        }
    }

    private fun getLightAtmosphere(conditionType: WeatherConditionType?): WeatherAtmosphereColors {
        return when (conditionType) {
            WeatherConditionType.CLEAR_DAY -> WeatherAtmosphereColors(
                backgroundBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFAF3EC), // Warm Sand 50
                        Color(0xFFF5EBE1), // Latte Cream
                        Color(0xFFFFFFFF)  // Warm White
                    )
                ),
                surfaceColor = Color(0xFFFFFFFF),
                surfaceBorderColor = Color(0xFFE6D7C8),
                cardGradient = Brush.linearGradient(
                    listOf(Color(0xFFFFFFFF), Color(0xFFFDF9F5))
                ),
                primaryTextColor = LatteTextPrimary,
                secondaryTextColor = LatteTextSecondary,
                accentColor = BronzeGold,
                isDarkBackground = false
            )
            WeatherConditionType.CLEAR_NIGHT -> WeatherAtmosphereColors(
                backgroundBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF231814),
                        Color(0xFF19110F),
                        Color(0xFF100B09)
                    )
                ),
                surfaceColor = Color(0xFF261B17),
                surfaceBorderColor = MochaBorder,
                cardGradient = Brush.linearGradient(
                    listOf(Color(0xFF2A1E19), Color(0xFF1E1512))
                ),
                primaryTextColor = CreamTextPrimary,
                secondaryTextColor = CreamTextSecondary,
                accentColor = WarmHoney,
                isDarkBackground = true
            )
            WeatherConditionType.RAIN, WeatherConditionType.HEAVY_RAIN, WeatherConditionType.DRIZZLE -> WeatherAtmosphereColors(
                backgroundBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF3ECE4),
                        Color(0xFFEBE2D8),
                        Color(0xFFF8F4EF)
                    )
                ),
                surfaceColor = Color(0xFFFFFFFF),
                surfaceBorderColor = Color(0xFFDFD0C2),
                cardGradient = Brush.linearGradient(
                    listOf(Color(0xFFFFFFFF), Color(0xFFF6EFE8))
                ),
                primaryTextColor = LatteTextPrimary,
                secondaryTextColor = LatteTextSecondary,
                accentColor = BronzeGold,
                isDarkBackground = false
            )
            WeatherConditionType.THUNDERSTORM -> WeatherAtmosphereColors(
                backgroundBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2A1826),
                        Color(0xFF1C1118),
                        Color(0xFF120B10)
                    )
                ),
                surfaceColor = Color(0xFF271720),
                surfaceBorderColor = Color(0xFF5A3050),
                cardGradient = Brush.linearGradient(
                    listOf(Color(0xFF2D1B26), Color(0xFF1E111B))
                ),
                primaryTextColor = CreamTextPrimary,
                secondaryTextColor = CreamTextSecondary,
                accentColor = WarmTerracotta,
                isDarkBackground = true
            )
            WeatherConditionType.SNOW -> WeatherAtmosphereColors(
                backgroundBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFAF6F2),
                        Color(0xFFF2ECE6),
                        Color(0xFFFFFFFF)
                    )
                ),
                surfaceColor = Color(0xFFFFFFFF),
                surfaceBorderColor = Color(0xFFE5D8CC),
                cardGradient = Brush.linearGradient(
                    listOf(Color(0xFFFFFFFF), Color(0xFFF9F5F1))
                ),
                primaryTextColor = LatteTextPrimary,
                secondaryTextColor = LatteTextSecondary,
                accentColor = BronzeGold,
                isDarkBackground = false
            )
            else -> WeatherAtmosphereColors(
                backgroundBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFAF5F0),
                        Color(0xFFF4ECE2),
                        Color(0xFFFFFFFF)
                    )
                ),
                surfaceColor = Color(0xFFFFFFFF),
                surfaceBorderColor = Color(0xFFE5D7C8),
                cardGradient = Brush.linearGradient(
                    listOf(Color(0xFFFFFFFF), Color(0xFFF9F5F0))
                ),
                primaryTextColor = LatteTextPrimary,
                secondaryTextColor = LatteTextSecondary,
                accentColor = BronzeGold,
                isDarkBackground = false
            )
        }
    }

    private fun getDarkAtmosphere(conditionType: WeatherConditionType?): WeatherAtmosphereColors {
        return when (conditionType) {
            WeatherConditionType.CLEAR_DAY -> WeatherAtmosphereColors(
                backgroundBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2F1D15), // Warm Espresso with Amber Zenith
                        Color(0xFF1E1310), // Deep Mocha
                        Color(0xFF120B09)  // Deepest Dark Chocolate
                    )
                ),
                surfaceColor = Color(0xFF281C18),
                surfaceBorderColor = MochaBorder,
                cardGradient = Brush.linearGradient(
                    listOf(Color(0xFF30221D), Color(0xFF221713))
                ),
                primaryTextColor = CreamTextPrimary,
                secondaryTextColor = CreamTextSecondary,
                accentColor = WarmAmber,
                isDarkBackground = true
            )
            WeatherConditionType.CLEAR_NIGHT -> WeatherAtmosphereColors(
                backgroundBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E1518), // Starlit Mocha Midnight
                        Color(0xFF150F12),
                        Color(0xFF0E090B)
                    )
                ),
                surfaceColor = Color(0xFF23191D),
                surfaceBorderColor = MochaBorder,
                cardGradient = Brush.linearGradient(
                    listOf(Color(0xFF291D22), Color(0xFF1A1216))
                ),
                primaryTextColor = CreamTextPrimary,
                secondaryTextColor = CreamTextSecondary,
                accentColor = WarmHoney,
                isDarkBackground = true
            )
            WeatherConditionType.RAIN, WeatherConditionType.HEAVY_RAIN, WeatherConditionType.DRIZZLE -> WeatherAtmosphereColors(
                backgroundBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF201917), // Misty Cocoa
                        Color(0xFF171211),
                        Color(0xFF0F0B0A)
                    )
                ),
                surfaceColor = Color(0xFF251C19),
                surfaceBorderColor = MochaBorder,
                cardGradient = Brush.linearGradient(
                    listOf(Color(0xFF2B201D), Color(0xFF1C1412))
                ),
                primaryTextColor = CreamTextPrimary,
                secondaryTextColor = CreamTextSecondary,
                accentColor = WarmCaramel,
                isDarkBackground = true
            )
            WeatherConditionType.THUNDERSTORM -> WeatherAtmosphereColors(
                backgroundBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2D1822), // Storm Cocoa Amethyst
                        Color(0xFF1D0F16),
                        Color(0xFF12090E)
                    )
                ),
                surfaceColor = Color(0xFF281720),
                surfaceBorderColor = Color(0xFF5E354A),
                cardGradient = Brush.linearGradient(
                    listOf(Color(0xFF311C27), Color(0xFF201019))
                ),
                primaryTextColor = CreamTextPrimary,
                secondaryTextColor = CreamTextSecondary,
                accentColor = WarmTerracotta,
                isDarkBackground = true
            )
            WeatherConditionType.SNOW -> WeatherAtmosphereColors(
                backgroundBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF231C1A),
                        Color(0xFF181312),
                        Color(0xFF0F0C0B)
                    )
                ),
                surfaceColor = Color(0xFF27201E),
                surfaceBorderColor = MochaBorder,
                cardGradient = Brush.linearGradient(
                    listOf(Color(0xFF2F2624), Color(0xFF1E1715))
                ),
                primaryTextColor = CreamTextPrimary,
                secondaryTextColor = CreamTextSecondary,
                accentColor = WarmHoney,
                isDarkBackground = true
            )
            else -> WeatherAtmosphereColors(
                backgroundBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF251A15), // Deep Espresso
                        Color(0xFF1A120F), // Dark Chocolate
                        Color(0xFF100A08)  // Obsidian Brown
                    )
                ),
                surfaceColor = Color(0xFF271B17),
                surfaceBorderColor = MochaBorder,
                cardGradient = Brush.linearGradient(
                    listOf(Color(0xFF2E201B), Color(0xFF1F1411))
                ),
                primaryTextColor = CreamTextPrimary,
                secondaryTextColor = CreamTextSecondary,
                accentColor = WarmCaramel,
                isDarkBackground = true
            )
        }
    }
}
