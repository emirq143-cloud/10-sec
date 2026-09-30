package com.example.data.model

import com.example.ui.theme.LeagueBronze
import com.example.ui.theme.LeagueDiamond
import com.example.ui.theme.LeagueGold
import com.example.ui.theme.LeagueMaster
import com.example.ui.theme.LeagueSilver
import androidx.compose.ui.graphics.Color

enum class League(
  val displayName: String,
  val minScore: Int,
  val iconEmoji: String,
  val color: Color
) {
  BRONZE("Bronz Lig", 0, "🥉", LeagueBronze),
  SILVER("Gümüş Lig", 3000, "🥈", LeagueSilver),
  GOLD("Altın Lig", 7000, "🥇", LeagueGold),
  DIAMOND("Elmas Lig", 12000, "💎", LeagueDiamond),
  MASTER("Usta Ligi", 20000, "👑", LeagueMaster);

  companion object {
    fun fromScore(score: Int): League {
      return when {
        score >= MASTER.minScore -> MASTER
        score >= DIAMOND.minScore -> DIAMOND
        score >= GOLD.minScore -> GOLD
        score >= SILVER.minScore -> SILVER
        else -> BRONZE
      }
    }
  }
}

data class UserProfile(
  val name: String = "Emir",
  val level: Int = 7,
  val currentXp: Int = 720,
  val maxXp: Int = 1000,
  val coins: Int = 548,
  val totalScore: Int = 8920,
  val nationalRank: Int = 2481,
  val currentStreak: Int = 3,
  val maxStreak: Int = 12,
  val selectedAvatarId: String = "default",
  val gamesWon: Int = 27,
  val gamesPlayed: Int = 31,
  val brainMemoryScore: Int = 87,
  val brainReflexScore: Int = 92,
  val brainAttentionScore: Int = 74,
  val brainLogicScore: Int = 89,
  val soundEnabled: Boolean = true,
  val vibrationEnabled: Boolean = true,
  val avgReactionTimeMs: Int = 278,
  val bestReactionTimeMs: Int = 194,
  val reactionHistory: List<Int> = listOf(315, 298, 284, 272, 259, 278),
  val hasSpunWheelToday: Boolean = false,
  val luckySpinsCount: Int = 1
) {
  val league: League
    get() = League.fromScore(totalScore)

  val overallBrainScore: Int
    get() = ((brainMemoryScore + brainReflexScore + brainAttentionScore + brainLogicScore) / 4)

  val reactionPercentile: Int
    get() = when {
      avgReactionTimeMs < 220 -> 99
      avgReactionTimeMs < 250 -> 95
      avgReactionTimeMs < 280 -> 88
      avgReactionTimeMs < 320 -> 76
      avgReactionTimeMs < 380 -> 60
      else -> 45
    }
}

data class AvatarItem(
  val id: String,
  val name: String,
  val emoji: String,
  val unlockLevel: Int,
  val isUnlocked: Boolean = false,
  val description: String = ""
)

data class DailyMission(
  val id: String,
  val title: String,
  val target: Int,
  val current: Int,
  val xpReward: Int,
  val isClaimed: Boolean = false
) {
  val isCompleted: Boolean get() = current >= target
}

data class LeaderboardEntry(
  val rank: Int,
  val name: String,
  val score: Int,
  val avatarEmoji: String,
  val isUser: Boolean = false,
  val country: String = "TR",
  val league: League = League.BRONZE
)
