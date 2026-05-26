package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewGameScreen(navController: NavController, viewModel: MainViewModel) {
    var players by remember { mutableStateOf(listOf("", "")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Partida") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    val validPlayers = players.filter { it.isNotBlank() }
                    if (validPlayers.size >= 2) {
                        viewModel.startGame(validPlayers) { gameId ->
                            navController.navigate("in_game/$gameId") {
                                popUpTo("main_menu")
                            }
                        }
                    }
                },
                icon = { Icon(Icons.Default.Add, contentDescription = "Empezar") },
                text = { Text("Empezar") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(players) { index, player ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = player,
                        onValueChange = { newValue ->
                            val newList = players.toMutableList()
                            newList[index] = newValue
                            players = newList
                        },
                        label = { Text("Jugador ${index + 1}") },
                        modifier = Modifier.weight(1f)
                    )
                    if (players.size > 2) {
                        IconButton(onClick = {
                            val newList = players.toMutableList()
                            newList.removeAt(index)
                            players = newList
                        }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Eliminar jugador",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
            item {
                Button(
                    onClick = {
                        players = players + ""
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Agregar Jugador")
                }
            }
        }
    }
}
