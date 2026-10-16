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
  val name: String = "Oyuncu",
  val level: Int = 1,
  val currentXp: Int = 0,
  val maxXp: Int = 100,
  val coins: Int = 0,
  val totalScore: Int = 0,
  val nationalRank: Int = 9999,
  val currentStreak: Int = 0,
  val maxStreak: Int = 0,
  val selectedAvatarId: String = "default",
  val gamesWon: Int = 0,
  val gamesPlayed: Int = 0,
  val brainMemoryScore: Int = 50,
  val brainReflexScore: Int = 50,
  val brainAttentionScore: Int = 50,
  val brainLogicScore: Int = 50,
  val soundEnabled: Boolean = true,
  val vibrationEnabled: Boolean = true,
  val avgReactionTimeMs: Int = 300,
  val bestReactionTimeMs: Int = 300,
  val reactionHistory: List<Int> = emptyList(),
  val hasSpunWheelToday: Boolean = false,
  val luckySpinsCount: Int = 1,
  val lastWheelSpinDate: String = "",
  val wheelAdSpinsToday: Int = 0,
  val maxDailyAdSpins: Int = 1
) {
  val canWatchAdForWheelSpin: Boolean
    get() = hasSpunWheelToday && wheelAdSpinsToday < maxDailyAdSpins
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

enum class AvatarRarity(val label: String, val color: Color) {
  COMMON("Yaygın", Color(0xFF64748B)),
  RARE("Nadir", Color(0xFF2563EB)),
  EPIC("Epik", Color(0xFF7C3AED)),
  LEGENDARY("Efsanevi", Color(0xFFD97706))
}

data class AvatarItem(
  val id: String,
  val name: String,
  val emoji: String,
  val unlockLevel: Int = 1,
  val priceCoins: Int = 0,
  val isUnlocked: Boolean = false,
  val description: String = "",
  val rarity: AvatarRarity = AvatarRarity.COMMON
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
  val league: League = League.BRONZE,
  val city: String = "İstanbul",
  val userId: String = ""
)
