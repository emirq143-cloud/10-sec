package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_records")
data class GameRecordEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val gameTypeId: String,
  val score: Int,
  val xpEarned: Int,
  val accuracyPercent: Int,
  val durationSeconds: Int,
  val isRiskMode: Boolean,
  val isVictory: Boolean,
  val timestamp: Long = System.currentTimeMillis()
)
