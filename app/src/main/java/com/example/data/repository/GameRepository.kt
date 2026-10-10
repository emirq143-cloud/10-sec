package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.AppDatabase
import com.example.data.local.GameRecordEntity
import com.example.data.model.AvatarItem
import com.example.data.model.AvatarRarity
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

data class Competitor(
  val id: String,
  val name: String,
  val baseScore: Int,
  val avatarEmoji: String,
  val country: String,
  val league: League,
  val city: String = "İstanbul"
)

class GameRepository(context: Context) {
  private val database = AppDatabase.getDatabase(context)
  private val gameDao = database.gameScoreDao()
  private val scope = CoroutineScope(Dispatchers.IO)
  private val prefs: SharedPreferences = context.getSharedPreferences("brain_game_prefs", Context.MODE_PRIVATE)

  private fun getTodayDateString(): String {
    val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    return sdf.format(Date())
  }

  // Seed pool of active Turkey & Global players
  private val competitorsTurkey = listOf(
    Competitor("p1", "BerkAy", 15842, "⚡", "TR", League.DIAMOND, "İstanbul"),
    Competitor("p2", "Zehra", 14920, "🔥", "TR", League.DIAMOND, "Ankara"),
    Competitor("p3", "Yusuf", 13765, "🦁", "TR", League.DIAMOND, "İzmir"),
    Competitor("p4", "Can_07", 12400, "🏎️", "TR", League.DIAMOND, "Antalya"),
    Competitor("p5", "Elif_K", 11850, "🎯", "TR", League.GOLD, "Bursa"),
    Competitor("p6", "Baris", 10920, "🐺", "TR", League.GOLD, "Eskişehir"),
    Competitor("p7", "Mert", 9400, "🧑‍💻", "TR", League.GOLD, "Adana"),
    Competitor("p8", "Ada", 8660, "🦊", "TR", League.GOLD, "Trabzon"),
    Competitor("p9", "Deniz", 8540, "🌊", "TR", League.GOLD, "Muğla"),
    Competitor("p10", "Kaan", 8310, "🦅", "TR", League.GOLD, "Konya"),
    Competitor("p11", "Burak", 7600, "🎮", "TR", League.GOLD, "Kayseri"),
    Competitor("p12", "Selin", 6850, "🌸", "TR", League.SILVER, "Gaziantep"),
    Competitor("p13", "Emre_TR", 5900, "🚀", "TR", League.SILVER, "Samsun")
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

  private val allCatalogAvatars = listOf(
    AvatarItem("default", "Astronot", "🧑‍🚀", unlockLevel = 1, priceCoins = 0, isUnlocked = true, description = "İlk cesur maceracı (Ücretsiz)", rarity = AvatarRarity.COMMON),
    AvatarItem("ninja", "Gölge Ninja", "🥷", unlockLevel = 1, priceCoins = 350, isUnlocked = false, description = "Sessiz ve yıldırım refleks ustası", rarity = AvatarRarity.COMMON),
    AvatarItem("cat", "Uğurlu Kedi", "🐱", unlockLevel = 1, priceCoins = 500, isUnlocked = false, description = "Dokuz canlı refleks uzmanı", rarity = AvatarRarity.COMMON),
    AvatarItem("robot", "Siber Robot", "🤖", unlockLevel = 1, priceCoins = 750, isUnlocked = false, description = "Hızlı işlemci ve hesaplama uzmanı", rarity = AvatarRarity.COMMON),
    AvatarItem("alien", "Uzaylı Gezgin", "👽", unlockLevel = 1, priceCoins = 1000, isUnlocked = false, description = "Bilinmeyen boyutların kaşifi", rarity = AvatarRarity.RARE),
    AvatarItem("detective", "Dedektif", "🕵️‍♂️", unlockLevel = 1, priceCoins = 1350, isUnlocked = false, description = "Farkları milisaniyede sezen göz", rarity = AvatarRarity.RARE),
    AvatarItem("lion", "Kral Aslan", "🦁", unlockLevel = 1, priceCoins = 1800, isUnlocked = false, description = "Reflekslerin ve cesaretin kralı", rarity = AvatarRarity.RARE),
    AvatarItem("fox", "Kurnaz Tilki", "🦊", unlockLevel = 1, priceCoins = 2200, isUnlocked = false, description = "Zeka ve taktik dehası", rarity = AvatarRarity.RARE),
    AvatarItem("wizard", "Zihin Büyücüsü", "🧙‍♂️", unlockLevel = 1, priceCoins = 2800, isUnlocked = false, description = "Derin mantık ve hafıza büyücüsü", rarity = AvatarRarity.EPIC),
    AvatarItem("dragon", "Ateş Ejderi", "🐲", unlockLevel = 1, priceCoins = 3500, isUnlocked = false, description = "Alev saçan seri galibiyetler", rarity = AvatarRarity.EPIC),
    AvatarItem("samurai", "Neon Samuray", "⚔️", unlockLevel = 1, priceCoins = 4200, isUnlocked = false, description = "Kusursuz keskinlikte zihin", rarity = AvatarRarity.EPIC),
    AvatarItem("eagle", "Göklerin Kartalı", "🦅", unlockLevel = 1, priceCoins = 4900, isUnlocked = false, description = "Yükseklerden gören keskin bakış", rarity = AvatarRarity.EPIC),
    AvatarItem("crown_king", "Altın Kral", "👑", unlockLevel = 1, priceCoins = 5800, isUnlocked = false, description = "Tüm liglerin zirvesindeki efsane", rarity = AvatarRarity.LEGENDARY),
    AvatarItem("diamond_hero", "Elmas Şampiyon", "💎", unlockLevel = 1, priceCoins = 6800, isUnlocked = false, description = "Saf odaklanma ve konsantrasyon", rarity = AvatarRarity.LEGENDARY),
    AvatarItem("lightning", "Yıldırım Tanrısı", "⚡", unlockLevel = 1, priceCoins = 7900, isUnlocked = false, description = "Işık hızında tepki gücü", rarity = AvatarRarity.LEGENDARY),
    AvatarItem("unicorn", "Kozmik Unicorn", "🦄", unlockLevel = 1, priceCoins = 9200, isUnlocked = false, description = "Nadir bulunan olağanüstü sezgi", rarity = AvatarRarity.LEGENDARY),
    AvatarItem("agent", "Siber Ajan", "🕶️", unlockLevel = 1, priceCoins = 10500, isUnlocked = false, description = "Matriks düzeyinde algı", rarity = AvatarRarity.LEGENDARY),
    AvatarItem("gladiator", "Altın Gladyatör", "🏆", unlockLevel = 1, priceCoins = 12000, isUnlocked = false, description = "Asla pes etmeyen arenanın fatihi", rarity = AvatarRarity.LEGENDARY)
  )

  private val savedUnlockedIds: MutableSet<String>
  private val savedCoins: Int
  private val savedSelectedAvatar: String
  private val savedLastWheelDate: String

  init {
    savedCoins = prefs.getInt("user_coins", 75)
    savedSelectedAvatar = prefs.getString("selected_avatar", "default") ?: "default"
    savedLastWheelDate = prefs.getString("last_wheel_date", "") ?: ""
    val defaultUnlocked = setOf("default")
    savedUnlockedIds = (prefs.getStringSet("unlocked_avatars", defaultUnlocked) ?: defaultUnlocked).toMutableSet()
    savedUnlockedIds.add("default")
  }

  private val _userProfile = MutableStateFlow(
    UserProfile(
      name = prefs.getString("user_name", "Emir") ?: "Emir",
      level = prefs.getInt("user_level", 7),
      currentXp = prefs.getInt("user_xp", 720),
      maxXp = prefs.getInt("user_max_xp", 1000),
      coins = savedCoins,
      totalScore = prefs.getInt("user_total_score", 8920),
      nationalRank = 37,
      currentStreak = prefs.getInt("user_streak", 4),
      maxStreak = prefs.getInt("user_max_streak", 12),
      selectedAvatarId = savedSelectedAvatar,
      gamesWon = prefs.getInt("user_games_won", 27),
      gamesPlayed = prefs.getInt("user_games_played", 31),
      brainMemoryScore = prefs.getInt("user_memory_score", 87),
      brainReflexScore = prefs.getInt("user_reflex_score", 92),
      brainAttentionScore = prefs.getInt("user_attention_score", 74),
      brainLogicScore = prefs.getInt("user_logic_score", 89),
      avgReactionTimeMs = prefs.getInt("user_avg_reaction", 278),
      bestReactionTimeMs = prefs.getInt("user_best_reaction", 194),
      soundEnabled = prefs.getBoolean("user_sound_enabled", true),
      vibrationEnabled = prefs.getBoolean("user_vibration_enabled", true),
      luckySpinsCount = prefs.getInt("user_lucky_spins", 1),
      hasSpunWheelToday = (savedLastWheelDate == getTodayDateString()),
      lastWheelSpinDate = savedLastWheelDate
    )
  )
  val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

  private val _avatars = MutableStateFlow(
    allCatalogAvatars.map { item ->
      if (item.id == "default" || savedUnlockedIds.contains(item.id)) {
        item.copy(isUnlocked = true)
      } else {
        item.copy(isUnlocked = false)
      }
    }
  )
  val avatars: StateFlow<List<AvatarItem>> = _avatars.asStateFlow()

  private val _dailyMissions = MutableStateFlow(
    run {
      val today = getTodayDateString()
      val savedMissionsDate = prefs.getString("missions_saved_date", "")
      val isSameDay = (savedMissionsDate == today)

      listOf(
        DailyMission(
          id = "m1",
          title = "5 mini oyun kazan",
          target = 5,
          current = if (isSameDay) prefs.getInt("mission_m1_curr", 0) else 0,
          xpReward = 200,
          isClaimed = if (isSameDay) prefs.getBoolean("mission_m1_claimed", false) else false
        ),
        DailyMission(
          id = "m2",
          title = "3 refleks oyunu tamamla",
          target = 3,
          current = if (isSameDay) prefs.getInt("mission_m2_curr", 0) else 0,
          xpReward = 300,
          isClaimed = if (isSameDay) prefs.getBoolean("mission_m2_claimed", false) else false
        ),
        DailyMission(
          id = "m3",
          title = "10 seri yap",
          target = 10,
          current = if (isSameDay) prefs.getInt("mission_m3_curr", 0) else 0,
          xpReward = 500,
          isClaimed = if (isSameDay) prefs.getBoolean("mission_m3_claimed", false) else false
        )
      )
    }
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

  private fun persistUserData() {
    val u = _userProfile.value
    val missions = _dailyMissions.value
    val today = getTodayDateString()

    val editor = prefs.edit()
      .putInt("user_coins", u.coins)
      .putString("selected_avatar", u.selectedAvatarId)
      .putString("last_wheel_date", u.lastWheelSpinDate)
      .putString("user_name", u.name)
      .putInt("user_level", u.level)
      .putInt("user_xp", u.currentXp)
      .putInt("user_max_xp", u.maxXp)
      .putInt("user_total_score", u.totalScore)
      .putInt("user_streak", u.currentStreak)
      .putInt("user_max_streak", u.maxStreak)
      .putInt("user_games_won", u.gamesWon)
      .putInt("user_games_played", u.gamesPlayed)
      .putInt("user_memory_score", u.brainMemoryScore)
      .putInt("user_reflex_score", u.brainReflexScore)
      .putInt("user_attention_score", u.brainAttentionScore)
      .putInt("user_logic_score", u.brainLogicScore)
      .putInt("user_avg_reaction", u.avgReactionTimeMs)
      .putInt("user_best_reaction", u.bestReactionTimeMs)
      .putBoolean("user_sound_enabled", u.soundEnabled)
      .putBoolean("user_vibration_enabled", u.vibrationEnabled)
      .putInt("user_lucky_spins", u.luckySpinsCount)
      .putStringSet("unlocked_avatars", savedUnlockedIds)
      .putString("missions_saved_date", today)

    missions.find { it.id == "m1" }?.let {
      editor.putInt("mission_m1_curr", it.current).putBoolean("mission_m1_claimed", it.isClaimed)
    }
    missions.find { it.id == "m2" }?.let {
      editor.putInt("mission_m2_curr", it.current).putBoolean("mission_m2_claimed", it.isClaimed)
    }
    missions.find { it.id == "m3" }?.let {
      editor.putInt("mission_m3_curr", it.current).putBoolean("mission_m3_claimed", it.isClaimed)
    }

    editor.apply()
  }

  fun updateUserName(newName: String) {
    if (newName.isNotBlank()) {
      _userProfile.update { it.copy(name = newName.trim()) }
      persistUserData()
    }
  }

  fun toggleSound(enabled: Boolean) {
    _userProfile.update { it.copy(soundEnabled = enabled) }
    persistUserData()
  }

  fun toggleVibration(enabled: Boolean) {
    _userProfile.update { it.copy(vibrationEnabled = enabled) }
    persistUserData()
  }

  fun claimLuckyWheel(coins: Int, xp: Int) {
    val today = getTodayDateString()
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
        hasSpunWheelToday = true,
        lastWheelSpinDate = today
      )
    }
    persistUserData()
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
    persistUserData()
  }

  fun grantExtraSpin() {
    _userProfile.update { it.copy(hasSpunWheelToday = false, luckySpinsCount = it.luckySpinsCount + 1) }
    persistUserData()
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
    persistUserData()
  }

  fun recordGameResult(
    gameType: GameType,
    score: Int,
    xpEarned: Int,
    accuracyPercent: Int,
    durationSeconds: Int,
    isRiskMode: Boolean,
    isVictory: Boolean,
    coinsEarned: Int = 0
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
        val baseCoins = if (isVictory) (if (isRiskMode) 6 else 3) else 1
        val newCoins = current.coins + baseCoins + coinsEarned

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

      // Persist user progress
      persistUserData()
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
      persistUserData()
    }
  }

  fun purchaseAvatar(avatarId: String): Boolean {
    val avatar = _avatars.value.find { it.id == avatarId } ?: return false
    if (avatar.isUnlocked) {
      selectAvatar(avatarId)
      return true
    }
    val user = _userProfile.value
    if (user.coins >= avatar.priceCoins) {
      val remaining = user.coins - avatar.priceCoins
      savedUnlockedIds.add(avatarId)
      _userProfile.update { it.copy(coins = remaining, selectedAvatarId = avatarId) }
      _avatars.update { list ->
        list.map { if (it.id == avatarId) it.copy(isUnlocked = true) else it }
      }
      persistUserData()
      return true
    }
    return false
  }

  fun selectAvatar(avatarId: String) {
    val item = _avatars.value.find { it.id == avatarId }
    if (item?.isUnlocked == true) {
      _userProfile.update { it.copy(selectedAvatarId = avatarId) }
      persistUserData()
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
        league = it.league,
        city = it.city
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
        league = user.league,
        city = "İstanbul"
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
