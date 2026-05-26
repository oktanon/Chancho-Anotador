package com.example.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.data.GameWithPlayers
import com.example.data.PlayerEntity
import com.example.ui.MainViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameResultScreen(navController: NavController, viewModel: MainViewModel, gameId: Long) {
    val gameState by viewModel.getGameFlow(gameId).collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showShareOptions by remember { mutableStateOf(false) }

    val graphicsLayer = rememberGraphicsLayer()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resultados") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("main_menu") { popUpTo(0) } }) {
                        Icon(Icons.Filled.Home, contentDescription = "Inicio")
                    }
                },
                actions = {
                    if (gameState != null) {
                        IconButton(onClick = { showShareOptions = true }) {
                            Icon(Icons.Filled.Share, contentDescription = "Compartir")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (gameState == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val game = gameState!!.game
            val players = gameState!!.players.sortedBy { it.score }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (showShareOptions) {
                    AlertDialog(
                        onDismissRequest = { showShareOptions = false },
                        title = { Text("Compartir Resultados") },
                        text = { Text("¿Cómo deseas compartir los resultados de esta partida?") },
                        confirmButton = {
                            TextButton(onClick = {
                                showShareOptions = false
                                coroutineScope.launch {
                                    val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                                    val imagesDir = File(context.cacheDir, "images")
                                    imagesDir.mkdirs()
                                    val file = File(imagesDir, "game_result.png")
                                    file.outputStream().use {
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                                    }
                                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "image/png"
                                        putExtra(Intent.EXTRA_STREAM, uri)
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Compartir Imagen"))
                                }
                            }) {
                                Text("Imagen (Screenshot)")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                showShareOptions = false
                                val text = generateResultText(gameState!!)
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, text)
                                }
                                context.startActivity(Intent.createChooser(intent, "Compartir Texto"))
                            }) {
                                Text("Texto")
                            }
                        }
                    )
                }

                // Everything inside here we draw into the graphics layer to capture
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawWithContent {
                            graphicsLayer.record {
                                this@drawWithContent.drawContent()
                            }
                            drawContent()
                        }
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Chancho Va! - Resultados",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val startTime = dateFormat.format(Date(game.startTime))
                    val endTime = game.endTime?.let { dateFormat.format(Date(it)) } ?: "Desconocido"

                    Text("Inicio: $startTime", style = MaterialTheme.typography.bodyMedium)
                    Text("Fin: $endTime", style = MaterialTheme.typography.bodyMedium)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Jugadores:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))

                    players.forEach { player ->
                        ResultPlayerCard(player = player)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ResultPlayerCard(player: PlayerEntity) {
    val word = "CHANCHO"
    val score = player.score.coerceIn(0, 7)
    val isEliminated = score >= 7

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = player.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                maxLines = 1
            )

            if (isEliminated) {
                Surface(
                    color = MaterialTheme.colorScheme.error,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "ELIMINADO",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onError,
                        letterSpacing = 0.5.sp
                    )
                }
            } else if (score == 0) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "INVICTO",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        letterSpacing = 0.5.sp
                    )
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    word.take(score).forEach { char ->
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = char.toString(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

fun generateResultText(state: GameWithPlayers): String {
    val builder = java.lang.StringBuilder()
    builder.append("🐷 Resultados de Chancho Va! 🐷\n\n")
    
    val game = state.game
    val players = state.players.sortedBy { it.score }
    
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val startTime = dateFormat.format(Date(game.startTime))
    val endTime = game.endTime?.let { dateFormat.format(Date(it)) } ?: "Desconocido"

    builder.append("Inicio: $startTime\n")
    builder.append("Fin: $endTime\n\n")
    
    builder.append("Posiciones:\n")
    players.forEachIndexed { index, player ->
        val position = index + 1
        val score = player.score.coerceIn(0, 7)
        val status = when {
            score >= 7 -> "Eliminado"
            score == 0 -> "Invicto"
            else -> "CHANCHO".take(score)
        }
        builder.append("$position. ${player.name} - $status\n")
    }
    
    return builder.toString()
}
