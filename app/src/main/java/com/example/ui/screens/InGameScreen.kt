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

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import com.example.R

import com.example.data.ThemeRepository

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun InGameScreen(navController: NavController, viewModel: MainViewModel, gameId: Long, themeRepository: ThemeRepository) {
    LaunchedEffect(gameId) {
        viewModel.loadGame(gameId)
    }

    val gameState by viewModel.currentGame.collectAsStateWithLifecycle()
    val showEmotions by themeRepository.showEmotionsFlow.collectAsState()
    val emotionPosition by themeRepository.emotionPositionFlow.collectAsState()
    var isCompactMode by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var showFinishDialog by remember { mutableStateOf(false) }

    BackHandler {
        showDialog = true
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.leave_game_title)) },
            text = { Text(stringResource(R.string.leave_game_text)) },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    viewModel.finishGame(gameId)
                    navController.navigate("game_result/$gameId") {
                        popUpTo("main_menu")
                    }
                }) {
                    Text(stringResource(R.string.finish), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    navController.navigate("main_menu") {
                        popUpTo(0)
                    }
                }) {
                    Text(stringResource(R.string.pause_continue_later))
                }
            }
        )
    }

    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = { Text(stringResource(R.string.finish_game_title)) },
            text = { Text(stringResource(R.string.finish_game_text)) },
            confirmButton = {
                TextButton(onClick = {
                    showFinishDialog = false
                    viewModel.finishGame(gameId)
                    navController.navigate("game_result/$gameId") {
                        popUpTo("main_menu")
                    }
                }) {
                    Text(stringResource(R.string.finish_completely), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.current_game), fontWeight = FontWeight.Medium) },
                actions = {
                    TextButton(onClick = { isCompactMode = !isCompactMode }) {
                        Text(
                            text = if (isCompactMode) stringResource(R.string.expanded) else stringResource(R.string.compact), 
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { showDialog = true }) { 
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
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
                        text = stringResource(R.string.tap_add_remove),
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
                        onClick = { navController.navigate("settings") },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.settings), fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { showFinishDialog = true },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.finish_completely), fontWeight = FontWeight.Bold)
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
                        text = stringResource(R.string.started_time, startTimeStr),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp,
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.in_game),
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
                            showEmotions = showEmotions,
                            emotionPosition = emotionPosition,
                            onIncrement = { viewModel.incrementScore(player.id) },
                            onDecrement = { viewModel.decrementScore(player.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerCardContentLayout(
    showFullHeightImage: Boolean,
    image: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    if (!showFullHeightImage) {
        content()
        return
    }

    Layout(
        content = {
            Box(modifier = Modifier.layoutId("image")) { image() }
            Box(modifier = Modifier.layoutId("content")) { content() }
        },
        modifier = Modifier.fillMaxWidth()
    ) { measurables, constraints ->
        val contentMeasurable = measurables.first { it.layoutId == "content" }
        val imageMeasurable = measurables.first { it.layoutId == "image" }

        val estimatedHeight = contentMeasurable.maxIntrinsicHeight(constraints.maxWidth)
        val imageWidth = estimatedHeight

        val remainingWidth = (constraints.maxWidth - imageWidth).coerceAtLeast(0)

        val contentPlaceable = contentMeasurable.measure(
            constraints.copy(minWidth = remainingWidth, maxWidth = remainingWidth, minHeight = 0)
        )

        val finalHeight = contentPlaceable.height

        val imagePlaceable = imageMeasurable.measure(
            Constraints.fixed(finalHeight, finalHeight)
        )

        layout(constraints.maxWidth, finalHeight) {
            imagePlaceable.placeRelative(0, 0)
            contentPlaceable.placeRelative(imagePlaceable.width, 0)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerCard(player: PlayerEntity, isCompact: Boolean, showEmotions: Boolean, emotionPosition: Int, onIncrement: () -> Unit, onDecrement: () -> Unit) {
    val word = "CHANCHO"
    val score = player.score.coerceIn(0, 7)
    val isEliminated = score >= 7

    val chanchoIcon = when (score) {
        0, 1 -> R.drawable.chancho_01
        2, 3 -> R.drawable.chancho_02
        4 -> R.drawable.chancho_03
        else -> R.drawable.chancho_04
    }

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
        val showFullHeightImage = showEmotions && emotionPosition == 1

        PlayerCardContentLayout(
            showFullHeightImage = showFullHeightImage,
            image = {
                Image(
                    painter = painterResource(id = chanchoIcon),
                    contentDescription = "Emoción del jugador",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
                    contentScale = ContentScale.Fit
                )
            },
            content = {
                if (isCompact) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f).padding(end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (showEmotions && emotionPosition == 0) {
                                Image(
                                    painter = painterResource(id = chanchoIcon),
                                    contentDescription = "Emoción del jugador",
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(
                                text = player.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                        }

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
                            Row(
                                modifier = Modifier.weight(1f).padding(end = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (showEmotions && emotionPosition == 0) {
                                    Image(
                                        painter = painterResource(id = chanchoIcon),
                                        contentDescription = "Emoción del jugador",
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                }
                                Text(
                                    text = player.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    maxLines = 1,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            
                            // Status Badge
                            val badgeText: String
                            val badgeContainerColor: Color
                            val badgeContentColor: Color

                            val eliminatedStr = stringResource(R.string.eliminated)
                            val penultimateStr = stringResource(R.string.penultimate)
                            val undefeatedStr = stringResource(R.string.undefeated)
                            val oneLetterStr = stringResource(R.string.one_letter)
                            val nLettersStr = stringResource(R.string.n_letters, score)

                            when {
                                isEliminated -> {
                                    badgeText = eliminatedStr
                                    badgeContainerColor = MaterialTheme.colorScheme.error
                                    badgeContentColor = MaterialTheme.colorScheme.onError
                                }
                                score == 6 -> {
                                    badgeText = penultimateStr
                                    badgeContainerColor = MaterialTheme.colorScheme.error
                                    badgeContentColor = MaterialTheme.colorScheme.onError
                                }
                                score == 0 -> {
                                    badgeText = undefeatedStr
                                    badgeContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                                    badgeContentColor = MaterialTheme.colorScheme.onSurface
                                }
                                else -> {
                                    badgeText = if (score == 1) oneLetterStr else nLettersStr
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
        )
    }
}
