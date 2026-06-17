package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.R
import com.example.data.ThemeRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, themeRepository: ThemeRepository) {
    val themeMode by themeRepository.themeFlow.collectAsState()
    val showEmotions by themeRepository.showEmotionsFlow.collectAsState()
    val emotionPosition by themeRepository.emotionPositionFlow.collectAsState()
    val languageMode by themeRepository.languageFlow.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.app_theme),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            ThemeOptionRow(
                label = stringResource(R.string.system_default),
                selected = themeMode == 0,
                onClick = { themeRepository.setTheme(0) }
            )
            ThemeOptionRow(
                label = stringResource(R.string.light),
                selected = themeMode == 1,
                onClick = { themeRepository.setTheme(1) }
            )
            ThemeOptionRow(
                label = stringResource(R.string.dark),
                selected = themeMode == 2,
                onClick = { themeRepository.setTheme(2) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = stringResource(R.string.app_language),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            )

            ThemeOptionRow(
                label = stringResource(R.string.system_default),
                selected = languageMode == 0,
                onClick = { themeRepository.setLanguage(0) }
            )
            ThemeOptionRow(
                label = stringResource(R.string.english),
                selected = languageMode == 1,
                onClick = { themeRepository.setLanguage(1) }
            )
            ThemeOptionRow(
                label = stringResource(R.string.spanish),
                selected = languageMode == 2,
                onClick = { themeRepository.setLanguage(2) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = stringResource(R.string.emotion_icons),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { themeRepository.setShowEmotions(!showEmotions) }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = stringResource(R.string.show_pig_icons), style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = showEmotions,
                    onCheckedChange = { themeRepository.setShowEmotions(it) }
                )
            }

            if (showEmotions) {
                Text(
                    text = stringResource(R.string.icon_position),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                var expanded by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (emotionPosition == 0) stringResource(R.string.left_of_letters) else stringResource(R.string.full_height))
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.left_of_letters)) },
                            onClick = {
                                themeRepository.setEmotionPosition(0)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.full_height)) },
                            onClick = {
                                themeRepository.setEmotionPosition(1)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeOptionRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}
