package com.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.data.ThemeRepository
import com.example.ui.MainViewModel
import com.example.ui.MainViewModelFactory
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.InGameScreen
import com.example.ui.screens.GameResultScreen
import com.example.ui.screens.MainMenuScreen
import com.example.ui.screens.NewGameScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val database = AppDatabase.getDatabase(this)
        val repository = AppRepository(database.appDao())
        val themeRepository = ThemeRepository(this)
        
        setContent {
            val themeMode by themeRepository.themeFlow.collectAsState()
            
            val darkTheme = when (themeMode) {
                1 -> false
                2 -> true
                else -> isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = darkTheme) {
                val viewModel: MainViewModel = viewModel(factory = MainViewModelFactory(repository))
                ChanchoApp(viewModel, themeRepository)
            }
        }
    }
}

@Composable
fun ChanchoApp(viewModel: MainViewModel, themeRepository: ThemeRepository) {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(navController = navController, startDestination = "main_menu") {
            composable("main_menu") {
                MainMenuScreen(navController, viewModel)
            }
            composable("new_game") {
                NewGameScreen(navController, viewModel)
            }
            composable("history") {
                HistoryScreen(navController, viewModel)
            }
            composable("settings") {
                SettingsScreen(navController, themeRepository)
            }
            composable(
                "in_game/{gameId}",
                arguments = listOf(navArgument("gameId") { type = NavType.LongType })
            ) { backStackEntry ->
                val gameId = backStackEntry.arguments?.getLong("gameId")
                if (gameId != null) {
                    InGameScreen(navController, viewModel, gameId, themeRepository)
                }
            }
            composable(
                "game_result/{gameId}",
                arguments = listOf(navArgument("gameId") { type = NavType.LongType })
            ) { backStackEntry ->
                val gameId = backStackEntry.arguments?.getLong("gameId")
                if (gameId != null) {
                    GameResultScreen(navController, viewModel, gameId)
                }
            }
        }
    }
}
