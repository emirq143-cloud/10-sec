package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.GameRecordEntity
import com.example.data.model.AvatarItem
import com.example.data.model.DailyMission
import com.example.data.model.GameCategory
import com.example.data.model.GameType
import com.example.data.model.LeaderboardEntry
import com.example.data.model.League
import com.example.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

data class Competitor(
  val id: String,
  val name: String,
  val baseScore: Int,
  val avatarEmoji: String,
  val country: String,
  val league: League
)

class GameRepository(context: Context) {
  private val database = AppDatabase.getDatabase(context)
  private val gameDao = database.gameScoreDao()
  private val scope = CoroutineScope(Dispatchers.IO)

  // Seed pool of active Turkey & Global players
  private val competitorsTurkey = listOf(
    Competitor("p1", "BerkAy", 15842, "⚡", "TR", League.DIAMOND),
    Competitor("p2", "Zehra", 14920, "🔥", "TR", League.DIAMOND),
    Competitor("p3", "Yusuf", 13765, "🦁", "TR", League.DIAMOND),
    Competitor("p4", "Can_07", 12400, "🏎️", "TR", League.DIAMOND),
    Competitor("p5", "Elif_K", 11850, "🎯", "TR", League.GOLD),
    Competitor("p6", "Baris", 10920, "🐺", "TR", League.GOLD),
    Competitor("p7", "Mert", 9400, "🧑‍💻", "TR", League.GOLD),
    Competitor("p8", "Ada", 8660, "🦊", "TR", League.GOLD),
    Competitor("p9", "Deniz", 8540, "🌊", "TR", League.GOLD),
    Competitor("p10", "Kaan", 8310, "🦅", "TR", League.GOLD),
    Competitor("p11", "Burak", 7600, "🎮", "TR", League.GOLD),
    Competitor("p12", "Selin", 6850, "🌸", "TR", League.SILVER),
    Competitor("p13", "Emre_TR", 5900, "🚀", "TR", League.SILVER)
  )

  private val competitorsGlobal = listOf(
    Competitor("g1", "Alex_V", 19450, "👑", "US", League.MASTER),
    Competitor("g2", "Kenji", 18210, "🥷", "JP", League.MASTER),
    Competitor("g3", "Elena_R", 17640, "💎", "DE", League.MASTER),
    Competitor("g4", "BerkAy", 15842, "⚡", "TR", League.DIAMOND),
    Competitor("g5", "Lucas", 15120, "🚀", "BR", League.DIAMOND),
    Competitor("g6", "Zehra", 14920, "🔥", "TR", League.DIAMOND),
    Competitor("g7", "Matteo", 14100, "🇮🇹", "IT", League.DIAMOND),
    Competitor("g8", "Sophie", 13450, "🇫🇷", "FR", League.DIAMOND)
  )

  private val competitorsFriends = listOf(
    Competitor("f1", "BerkAy", 15842, "⚡", "TR", League.DIAMOND),
    Competitor("f2", "Mert", 9400, "🧑‍💻", "TR", League.GOLD),
    Competitor("f3", "Ada", 8660, "🦊", "TR", League.GOLD),
    Competitor("f4", "Deniz", 8540, "🌊", "TR", League.GOLD),
    Competitor("f5", "Burak", 7600, "🎮", "TR", League.SILVER)
  )

  private val _userProfile = MutableStateFlow(
    UserProfile(
      name = "Emir",
      level = 7,
      currentXp = 720,
      maxXp = 1000,
      coins = 548,
      totalScore = 8920,
      nationalRank = 37,
      currentStreak = 4,
      maxStreak = 12,
      selectedAvatarId = "default",
      gamesWon = 27,
      gamesPlayed = 31,
      brainMemoryScore = 87,
      brainReflexScore = 92,
      brainAttentionScore = 74,
      brainLogicScore = 89
    )
  )
  val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

  private val _avatars = MutableStateFlow(
    listOf(
      AvatarItem("default", "Varsayılan", "🧑‍🚀", 1, true, "İlk maceracı"),
      AvatarItem("ninja", "Ninja", "🥷", 3, true, "Sessiz ve yıldırım hızlı"),
      AvatarItem("robot", "Robot", "🤖", 5, true, "Hesaplama uzmanı"),
      AvatarItem("alien", "Uzaylı", "👽", 6, true, "Bilinmeyen boyutlardan"),
      AvatarItem("cat", "Kedi", "🐱", 7, true, "Dokuz canlı refleks ustası"),
      AvatarItem("fire", "Ateş", "🔥", 9, false, "Alev saçan seriler"),
      AvatarItem("captain", "Kaptan", "👨‍✈️", 12, false, "Zirvenin lideri"),
      AvatarItem("astronaut", "Astronot", "👨‍🚀", 15, false, "Kozmik sınırları aşan"),
      AvatarItem("king", "Kral", "👑", 20, false, "Tüm liglerin efsanesi")
    )
  )
  val avatars: StateFlow<List<AvatarItem>> = _avatars.asStateFlow()

  private val _dailyMissions = MutableStateFlow(
    listOf(
      DailyMission("m1", "5 mini oyun kazan", 5, 3, 200, false),
      DailyMission("m2", "3 refleks oyunu tamamla", 3, 1, 300, false),
      DailyMission("m3", "10 seri yap", 10, 4, 500, false)
    )
  )
  val dailyMissions: StateFlow<List<DailyMission>> = _dailyMissions.asStateFlow()

  val allRecords: Flow<List<GameRecordEntity>> = gameDao.getAllRecords()

  init {
    updateDynamicRank()
  }

  private fun calculateNationalRank(score: Int): Int {
    // Count how many competitors in Turkey have a higher score
    val higherCount = competitorsTurkey.count { it.baseScore > score }
    return higherCount + 1
  }

  private fun updateDynamicRank() {
    _userProfile.update { current ->
      val calculatedRank = calculateNationalRank(current.totalScore)
      current.copy(nationalRank = calculatedRank)
    }
  }

  fun updateUserName(newName: String) {
    if (newName.isNotBlank()) {
      _userProfile.update { it.copy(name = newName.trim()) }
    }
  }

  fun toggleSound(enabled: Boolean) {
    _userProfile.update { it.copy(soundEnabled = enabled) }
  }

  fun toggleVibration(enabled: Boolean) {
    _userProfile.update { it.copy(vibrationEnabled = enabled) }
  }

  fun claimLuckyWheel(coins: Int, xp: Int) {
    _userProfile.update { current ->
      var newXp = current.currentXp + xp
      var newLevel = current.level
      var newMaxXp = current.maxXp
      while (newXp >= newMaxXp) {
        newXp -= newMaxXp
        newLevel++
        newMaxXp = (newMaxXp * 1.25f).toInt()
      }
      current.copy(
        coins = current.coins + coins,
        currentXp = newXp,
        level = newLevel,
        maxXp = newMaxXp,
        hasSpunWheelToday = true
      )
    }
  }

  fun addBonusReward(scoreBonus: Int, xpBonus: Int, coinsBonus: Int) {
    _userProfile.update { current ->
      val newTotalScore = current.totalScore + scoreBonus
      var newXp = current.currentXp + xpBonus
      var newLevel = current.level
      var newMaxXp = current.maxXp
      while (newXp >= newMaxXp) {
        newXp -= newMaxXp
        newLevel++
        newMaxXp = (newMaxXp * 1.25f).toInt()
      }
      val newRank = calculateNationalRank(newTotalScore)
      current.copy(
        totalScore = newTotalScore,
        coins = current.coins + coinsBonus,
        currentXp = newXp,
        level = newLevel,
        maxXp = newMaxXp,
        nationalRank = newRank
      )
    }
  }

  fun grantExtraSpin() {
    _userProfile.update { it.copy(hasSpunWheelToday = false, luckySpinsCount = it.luckySpinsCount + 1) }
  }

  fun recordReactionTime(ms: Int) {
    _userProfile.update { current ->
      val newHistory = (current.reactionHistory + ms).takeLast(6)
      val newAvg = (current.avgReactionTimeMs * 2 + ms) / 3
      val newBest = min(current.bestReactionTimeMs, ms)
      current.copy(
        avgReactionTimeMs = newAvg,
        bestReactionTimeMs = newBest,
        reactionHistory = newHistory
      )
    }
  }

  fun recordGameResult(
    gameType: GameType,
    score: Int,
    xpEarned: Int,
    accuracyPercent: Int,
    durationSeconds: Int,
    isRiskMode: Boolean,
    isVictory: Boolean
  ) {
    scope.launch {
      gameDao.insertRecord(
        GameRecordEntity(
          gameTypeId = gameType.id,
          score = score,
          xpEarned = xpEarned,
          accuracyPercent = accuracyPercent,
          durationSeconds = durationSeconds,
          isRiskMode = isRiskMode,
          isVictory = isVictory
        )
      )

      _userProfile.update { current ->
        val newGamesPlayed = current.gamesPlayed + 1
        val newGamesWon = if (isVictory) current.gamesWon + 1 else current.gamesWon
        val newStreak = if (isVictory) current.currentStreak + 1 else 0
        val newMaxStreak = max(current.maxStreak, newStreak)
        val newTotalScore = current.totalScore + score
        val newCoins = current.coins + if (isVictory) (if (isRiskMode) 35 else 15) else 3

        var newXp = current.currentXp + xpEarned
        var newLevel = current.level
        var newMaxXp = current.maxXp

        while (newXp >= newMaxXp) {
          newXp -= newMaxXp
          newLevel++
          newMaxXp = (newMaxXp * 1.25f).toInt()
        }

        // Live calculation of rank based on new score!
        val newRank = calculateNationalRank(newTotalScore)

        val delta = if (isVictory) min(3, max(1, (accuracyPercent - 70) / 10)) else -2
        val newMemory = if (gameType.category == GameCategory.MEMORY) min(99, max(50, current.brainMemoryScore + delta)) else current.brainMemoryScore
        val newReflex = if (gameType.category == GameCategory.REFLEX) min(99, max(50, current.brainReflexScore + delta)) else current.brainReflexScore
        val newAttention = if (gameType.category == GameCategory.ATTENTION) min(99, max(50, current.brainAttentionScore + delta)) else current.brainAttentionScore
        val newLogic = if (gameType.category == GameCategory.LOGIC) min(99, max(50, current.brainLogicScore + delta)) else current.brainLogicScore

        current.copy(
          level = newLevel,
          currentXp = newXp,
          maxXp = newMaxXp,
          coins = newCoins,
          totalScore = newTotalScore,
          nationalRank = newRank,
          currentStreak = newStreak,
          maxStreak = newMaxStreak,
          gamesPlayed = newGamesPlayed,
          gamesWon = newGamesWon,
          brainMemoryScore = newMemory,
          brainReflexScore = newReflex,
          brainAttentionScore = newAttention,
          brainLogicScore = newLogic
        )
      }

      // Check daily missions
      _dailyMissions.update { missions ->
        missions.map { m ->
          when (m.id) {
            "m1" -> if (isVictory && !m.isCompleted) m.copy(current = min(m.target, m.current + 1)) else m
            "m2" -> if (gameType.category == GameCategory.REFLEX && !m.isCompleted) m.copy(current = min(m.target, m.current + 1)) else m
            "m3" -> if (isVictory && !m.isCompleted) m.copy(current = min(m.target, _userProfile.value.currentStreak)) else m
            else -> m
          }
        }
      }

      // Unlock avatars if level increased
      _avatars.update { list ->
        list.map { avatar ->
          if (!avatar.isUnlocked && _userProfile.value.level >= avatar.unlockLevel) {
            avatar.copy(isUnlocked = true)
          } else avatar
        }
      }
    }
  }

  fun claimMissionReward(missionId: String) {
    val mission = _dailyMissions.value.find { it.id == missionId } ?: return
    if (mission.isCompleted && !mission.isClaimed) {
      _dailyMissions.update { list ->
        list.map { if (it.id == missionId) it.copy(isClaimed = true) else it }
      }
      _userProfile.update { current ->
        var newXp = current.currentXp + mission.xpReward
        var newLevel = current.level
        var newMaxXp = current.maxXp
        while (newXp >= newMaxXp) {
          newXp -= newMaxXp
          newLevel++
          newMaxXp = (newMaxXp * 1.25f).toInt()
        }
        current.copy(
          currentXp = newXp,
          level = newLevel,
          maxXp = newMaxXp,
          coins = current.coins + 50
        )
      }
    }
  }

  fun selectAvatar(avatarId: String) {
    val item = _avatars.value.find { it.id == avatarId }
    if (item?.isUnlocked == true) {
      _userProfile.update { it.copy(selectedAvatarId = avatarId) }
    }
  }

  fun getLeaderboard(tabIndex: Int): List<LeaderboardEntry> {
    val user = _userProfile.value
    val userAvatarEmoji = _avatars.value.find { it.id == user.selectedAvatarId }?.emoji ?: "🧑‍🚀"

    val basePool = when (tabIndex) {
      0 -> competitorsGlobal
      1 -> competitorsTurkey
      else -> competitorsFriends
    }

    // Merge online pool with current user's real live score!
    val allPlayers = basePool.map {
      LeaderboardEntry(
        rank = 0,
        name = it.name,
        score = it.baseScore,
        avatarEmoji = it.avatarEmoji,
        isUser = false,
        country = it.country,
        league = it.league
      )
    }.toMutableList()

    // Add current user
    allPlayers.add(
      LeaderboardEntry(
        rank = 0,
        name = "${user.name} (Sen)",
        score = user.totalScore,
        avatarEmoji = userAvatarEmoji,
        isUser = true,
        country = "TR",
        league = user.league
      )
    )

    // Sort descending by score
    val sorted = allPlayers.sortedByDescending { it.score }

    // Assign 1-indexed ranks
    return sorted.mapIndexed { index, entry ->
      entry.copy(rank = index + 1)
    }
  }
}
