package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.CurrentWeather
import com.example.data.model.TemperatureUnit
import com.example.data.model.WeatherConditionInfo
import com.example.data.model.WeatherConditionType
import com.example.ui.components.CurrentWeatherCard
import com.example.ui.theme.WeatherAppTheme
import com.example.ui.theme.WeatherAtmosphere
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun weather_card_screenshot() {
    val sampleCurrent = CurrentWeather(
      temperature = 22.0,
      feelsLike = 23.0,
      tempMinToday = 16.0,
      tempMaxToday = 26.0,
      condition = WeatherConditionInfo(
        title = "Partly Cloudy",
        description = "Sun and clouds mingling",
        type = WeatherConditionType.PARTLY_CLOUDY_DAY,
        iconCode = "02d"
      ),
      humidity = 58,
      windSpeed = 12.0,
      windDirection = 180.0,
      windGusts = 18.0,
      pressure = 1014.0,
      visibility = 10.0,
      uvIndex = 4.2,
      cloudCover = 35,
      precipitation = 0.0,
      isDay = true,
      timeFormatted = "Today • 12:00 PM",
      sunrise = "06:15 AM",
      sunset = "08:30 PM",
      dewPoint = 13.0
    )

    val atmosphere = WeatherAtmosphere.getAtmosphere(WeatherConditionType.PARTLY_CLOUDY_DAY, isDarkTheme = false)

    composeTestRule.setContent {
      WeatherAppTheme {
        CurrentWeatherCard(
          current = sampleCurrent,
          unit = TemperatureUnit.CELSIUS,
          atmosphere = atmosphere
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
