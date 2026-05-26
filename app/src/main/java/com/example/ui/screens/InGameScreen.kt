package com.example.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.data.PlayerEntity
import com.example.ui.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun InGameScreen(navController: NavController, viewModel: MainViewModel, gameId: Long) {
    LaunchedEffect(gameId) {
        viewModel.loadGame(gameId)
    }

    val gameState by viewModel.currentGame.collectAsStateWithLifecycle()
    var isCompactMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Partida Actual", fontWeight = FontWeight.Medium) },
                actions = {
                    TextButton(onClick = { isCompactMode = !isCompactMode }) {
                        Text(
                            text = if (isCompactMode) "AMPLIADO" else "COMPACTO", 
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { 
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                // Interaction Guide
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tap para sumar letra • Long press para quitar",
                        color = MaterialTheme.colorScheme.outline,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.size(48.dp, 1.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)))
                }

                // Footer Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { /* Handle settings/adjustments if needed */ },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ajustes", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            viewModel.finishGame(gameId)
                            navController.navigate("main_menu") {
                                popUpTo(0)
                            }
                        },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Terminar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { paddingValues ->
        if (gameState == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Game Info Bar
                val dateFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
                val startTimeStr = remember(gameState?.game?.startTime) {
                    gameState?.game?.startTime?.let { dateFormat.format(Date(it)) } ?: ""
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Iniciada: $startTimeStr",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp,
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "EN JUEGO",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(if (isCompactMode) 8.dp else 12.dp)
                ) {
                    items(gameState!!.players) { player ->
                        PlayerCard(
                            player = player,
                            isCompact = isCompactMode,
                            onIncrement = { viewModel.incrementScore(player.id) },
                            onDecrement = { viewModel.decrementScore(player.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerCard(player: PlayerEntity, isCompact: Boolean, onIncrement: () -> Unit, onDecrement: () -> Unit) {
    val word = "CHANCHO"
    val score = player.score.coerceIn(0, 7)
    val isEliminated = score >= 7

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(if (isCompact) 16.dp else 24.dp))
            .combinedClickable(
                onClick = { if (!isEliminated) onIncrement() },
                onLongClick = onDecrement
            ),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(if (isCompact) 16.dp else 24.dp)
    ) {
        if (isCompact) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
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
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        word.forEachIndexed { index, char ->
                            val isActive = index < score
                            val blockBg = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                            val blockColor = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.outline
                            
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .background(color = blockBg, shape = RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = char.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = blockColor
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                // Status Badge
                val badgeText: String
                val badgeContainerColor: Color
                val badgeContentColor: Color

                when {
                    isEliminated -> {
                        badgeText = "ELIMINADO"
                        badgeContainerColor = MaterialTheme.colorScheme.error
                        badgeContentColor = MaterialTheme.colorScheme.onError
                    }
                    score == 6 -> {
                        badgeText = "PENÚLTIMA"
                        badgeContainerColor = MaterialTheme.colorScheme.error
                        badgeContentColor = MaterialTheme.colorScheme.onError
                    }
                    score == 0 -> {
                        badgeText = "INVICTO"
                        badgeContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        badgeContentColor = MaterialTheme.colorScheme.onSurface
                    }
                    else -> {
                        badgeText = if (score == 1) "1 LETRA" else "$score LETRAS"
                        badgeContainerColor = MaterialTheme.colorScheme.primaryContainer
                        badgeContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    }
                }

                Surface(
                    color = badgeContainerColor,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = badgeText,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeContentColor,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Word Blocks
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                word.forEachIndexed { index, char ->
                    val isActive = index < score
                    val blockBg = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    val blockColor = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.outline
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .background(color = blockBg, shape = RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = blockColor
                        )
                    }
                }
            }
        }
    }
}
}
