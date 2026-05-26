package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MainMenuScreen(navController: NavController) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Chancho Va!",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 64.dp)
            )

            Button(
                onClick = { navController.navigate("new_game") },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text("Nueva Partida", style = MaterialTheme.typography.titleMedium)
            }

            OutlinedButton(
                onClick = { navController.navigate("history") },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp)
            ) {
                Text("Historial", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
