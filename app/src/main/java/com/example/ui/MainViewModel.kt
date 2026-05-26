package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppRepository
import com.example.data.GameWithPlayers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class MainViewModel(private val repository: AppRepository) : ViewModel() {

    val history: StateFlow<List<GameWithPlayers>> = repository.allGamesHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _currentGameId = MutableStateFlow<Long?>(null)
    
    val currentGame: StateFlow<GameWithPlayers?> = _currentGameId
        .flatMapLatest { id ->
            if (id != null) repository.getGameWithPlayers(id) else flowOf(null)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun loadGame(gameId: Long) {
        _currentGameId.value = gameId
    }

    fun startGame(players: List<String>, onGameStarted: (Long) -> Unit) {
        viewModelScope.launch {
            val gameId = repository.createGame(players)
            _currentGameId.value = gameId
            onGameStarted(gameId)
        }
    }

    fun incrementScore(playerId: Long) {
        viewModelScope.launch {
            repository.incrementPlayerScore(playerId)
        }
    }

    fun decrementScore(playerId: Long) {
        viewModelScope.launch {
            repository.decrementPlayerScore(playerId)
        }
    }

    fun finishGame(gameId: Long) {
        viewModelScope.launch {
            repository.finishGame(gameId)
            _currentGameId.value = null
        }
    }
}

class MainViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
