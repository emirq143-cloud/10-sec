package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameScoreDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRecord(record: GameRecordEntity): Long

  @Query("SELECT * FROM game_records ORDER BY timestamp DESC")
  fun getAllRecords(): Flow<List<GameRecordEntity>>

  @Query("SELECT MAX(score) FROM game_records WHERE gameTypeId = :gameTypeId")
  fun getBestScoreForGame(gameTypeId: String): Flow<Int?>

  @Query("SELECT SUM(score) FROM game_records")
  fun getTotalAccumulatedScore(): Flow<Int?>

  @Query("SELECT COUNT(*) FROM game_records WHERE isVictory = 1")
  fun getVictoryCount(): Flow<Int>

  @Query("SELECT COUNT(*) FROM game_records")
  fun getTotalGamesCount(): Flow<Int>
}
