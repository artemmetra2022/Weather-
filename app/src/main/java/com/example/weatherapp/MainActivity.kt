package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                WeatherScreen()
            }
        }
    }
}

@Composable
fun WeatherScreen() {
    var weatherData by remember { mutableStateOf("Нажмите кнопку, чтобы узнать погоду") }
    var isLoading by remember { mutableStateOf(false) }
    
    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Погода в Москве",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = weatherData,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    isLoading = true
                    coroutineScope.launch {
                        weatherData = fetchWeather()
                        isLoading = false
                    }
                },
                enabled = !isLoading
            ) {
                Text(if (isLoading) "Загрузка данных..." else "Обновить")
            }
        }
    }
}

suspend fun fetchWeather(): String {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL("https://api.open-meteo.com/v1/forecast?latitude=55.7522&longitude=37.6156&current_weather=true")
            val response = url.readText()
            
            val json = JSONObject(response)
            val current = json.getJSONObject("current_weather")
            val temp = current.getDouble("temperature")
            val windSpeed = current.getDouble("windspeed")
            
            "Температура: $temp °C\nСкорость ветра: $windSpeed км/ч"
        } catch (e: Exception) {
            "Ошибка при получении данных:\n${e.message}"
        }
    }
}
