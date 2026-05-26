package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {

    val allGamesHistory: Flow<List<GameWithPlayers>> = appDao.getAllGamesWithPlayers()

    fun getGameWithPlayers(gameId: Long): Flow<GameWithPlayers?> {
        return appDao.getGameWithPlayers(gameId)
    }

    suspend fun createGame(playerNames: List<String>): Long {
        val gameId = appDao.insertGame(GameEntity(startTime = System.currentTimeMillis()))
        val players = playerNames.map { name ->
            PlayerEntity(gameId = gameId, name = name)
        }
        appDao.insertPlayers(players)
        return gameId
    }

    suspend fun incrementPlayerScore(playerId: Long) {
        val player = appDao.getPlayer(playerId)
        if (player != null && player.score < 7) {
            appDao.updatePlayer(player.copy(score = player.score + 1))
        }
    }

    suspend fun decrementPlayerScore(playerId: Long) {
        val player = appDao.getPlayer(playerId)
        if (player != null && player.score > 0) {
            appDao.updatePlayer(player.copy(score = player.score - 1))
        }
    }

    suspend fun finishGame(gameId: Long) {
        val game = appDao.getGame(gameId)
        if (game != null) {
            appDao.updateGame(game.copy(endTime = System.currentTimeMillis()))
        }
    }

    suspend fun deleteGame(gameId: Long) {
        appDao.deleteGame(gameId)
    }

    suspend fun deleteAllGames() {
        appDao.deleteAllGames()
    }

    suspend fun getActiveGame(): GameWithPlayers? {
        return appDao.getActiveGame()
    }
}
