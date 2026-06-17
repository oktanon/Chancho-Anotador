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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.R
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
                title = { Text(stringResource(R.string.results)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("main_menu") { popUpTo(0) } }) {
                        Icon(Icons.Filled.Home, contentDescription = stringResource(R.string.home))
                    }
                },
                actions = {
                    if (gameState != null) {
                        IconButton(onClick = { showShareOptions = true }) {
                            Icon(Icons.Filled.Share, contentDescription = stringResource(R.string.share))
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
                    val shareTitle = stringResource(R.string.share_results)
                    val shareTextBody = generateResultText(gameState!!, LocalContext.current)
                    AlertDialog(
                        onDismissRequest = { showShareOptions = false },
                        title = { Text(stringResource(R.string.share_results)) },
                        text = { Text(stringResource(R.string.share_how)) },
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
                                    context.startActivity(Intent.createChooser(intent, shareTitle))
                                }
                            }) {
                                Text(stringResource(R.string.image_screenshot))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                showShareOptions = false
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, shareTextBody)
                                }
                                context.startActivity(Intent.createChooser(intent, shareTitle))
                            }) {
                                Text(stringResource(R.string.text))
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
                        text = stringResource(R.string.results_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val startTime = dateFormat.format(Date(game.startTime))
                    val endTime = game.endTime?.let { dateFormat.format(Date(it)) } ?: stringResource(R.string.unknown)

                    Text(stringResource(R.string.start_time, startTime), style = MaterialTheme.typography.bodyMedium)
                    Text(stringResource(R.string.end_time, endTime), style = MaterialTheme.typography.bodyMedium)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(stringResource(R.string.players), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
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
                        text = stringResource(R.string.eliminated),
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
                        text = stringResource(R.string.undefeated),
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

fun generateResultText(state: GameWithPlayers, context: android.content.Context): String {
    val builder = java.lang.StringBuilder()
    builder.append(context.getString(R.string.results_share_text_title))
    
    val game = state.game
    val players = state.players.sortedBy { it.score }
    
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val startTime = dateFormat.format(Date(game.startTime))
    val endTime = game.endTime?.let { dateFormat.format(Date(it)) } ?: context.getString(R.string.unknown)

    builder.append(context.getString(R.string.start_time, startTime)).append("\n")
    builder.append(context.getString(R.string.end_time, endTime)).append("\n\n")
    
    builder.append(context.getString(R.string.positions))
    players.forEachIndexed { index, player ->
        val position = index + 1
        val score = player.score.coerceIn(0, 7)
        val status = when {
            score >= 7 -> context.getString(R.string.eliminated)
            score == 0 -> context.getString(R.string.undefeated)
            else -> "CHANCHO".take(score)
        }
        builder.append("$position. ${player.name} - $status\n")
    }
    
    return builder.toString()
}
