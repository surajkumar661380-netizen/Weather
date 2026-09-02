package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.WeatherScreen
import com.example.ui.theme.WeatherAppTheme
import com.example.ui.viewmodel.WeatherViewModel

class MainActivity : ComponentActivity() {

    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by weatherViewModel.themeMode.collectAsStateWithLifecycle()

            WeatherAppTheme(themeMode = themeMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WeatherScreen(viewModel = weatherViewModel)
                }
            }
        }
    }
}
