package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert
    suspend fun insertGame(game: GameEntity): Long

    @Insert
    suspend fun insertPlayers(players: List<PlayerEntity>)
    
    @Update
    suspend fun updatePlayer(player: PlayerEntity)

    @Update
    suspend fun updateGame(game: GameEntity)

    @Transaction
    @Query("SELECT * FROM games ORDER BY startTime DESC")
    fun getAllGamesWithPlayers(): Flow<List<GameWithPlayers>>

    @Transaction
    @Query("SELECT * FROM games WHERE id = :gameId")
    fun getGameWithPlayers(gameId: Long): Flow<GameWithPlayers?>
    
    @Query("SELECT * FROM players WHERE id = :playerId")
    suspend fun getPlayer(playerId: Long): PlayerEntity?
    
    @Query("SELECT * FROM games WHERE id = :gameId")
    suspend fun getGame(gameId: Long): GameEntity?

    @Query("DELETE FROM games WHERE id = :gameId")
    suspend fun deleteGame(gameId: Long)

    @Query("DELETE FROM games")
    suspend fun deleteAllGames()

    @Transaction
    @Query("SELECT * FROM games WHERE endTime IS NULL ORDER BY startTime DESC LIMIT 1")
    suspend fun getActiveGame(): GameWithPlayers?
}
