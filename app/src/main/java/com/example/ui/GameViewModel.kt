package com.example.ui

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AvatarItem
import com.example.data.model.DailyMission
import com.example.data.model.GameType
import com.example.data.model.LeaderboardEntry
import com.example.data.model.UserProfile
import com.example.data.repository.GameRepository
import com.example.ui.components.AdRewardType
import com.example.ui.util.SoundHapticManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max

enum class ScreenState {
  START,
  HOME,
  GAMES_LIST,
  LEADERBOARD,
  PROFILE,
  GAME_PLAY,
  GAME_RESULT
}

data class GamePlaySession(
  val gameType: GameType,
  val isRiskMode: Boolean = false,
  val durationSeconds: Float = 10f,
  val remainingSeconds: Float = 10f,
  val currentScore: Int = 0,
  val currentStreak: Int = 0,
  val questionsAnswered: Int = 0,
  val correctCount: Int = 0,
  val mistakeCount: Int = 0,
  val coinsEarned: Int = 0,
  val comboStreak: Int = 0,
  val isTimerPaused: Boolean = false,
  val isFinished: Boolean = false,
  val isVictory: Boolean = false,
  val accuracyPercent: Int = 100,
  val feedbackText: String = "",
  val xpGained: Int = 0
)

class GameViewModel(application: Application) : AndroidViewModel(application) {
  private val repository = GameRepository(application)

  val userProfile: StateFlow<UserProfile> = repository.userProfile
  val avatars: StateFlow<List<AvatarItem>> = repository.avatars
  val dailyMissions: StateFlow<List<DailyMission>> = repository.dailyMissions

  private val _screenState = MutableStateFlow(ScreenState.START)
  val screenState: StateFlow<ScreenState> = _screenState.asStateFlow()

  private val _selectedTab = MutableStateFlow(0) // 0: Home, 1: Games, 2: Leaderboard, 3: Profile
  val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

  private val _leaderboardTab = MutableStateFlow(1) // 0: Dünya, 1: Türkiye, 2: Arkadaşlar
  val leaderboardTab: StateFlow<Int> = _leaderboardTab.asStateFlow()

  private val _activeSession = MutableStateFlow<GamePlaySession?>(null)
  val activeSession: StateFlow<GamePlaySession?> = _activeSession.asStateFlow()

  private val _pendingRiskGame = MutableStateFlow<GameType?>(null)
  val pendingRiskGame: StateFlow<GameType?> = _pendingRiskGame.asStateFlow()

  private val _showStreakCelebration = MutableStateFlow(false)
  val showStreakCelebration: StateFlow<Boolean> = _showStreakCelebration.asStateFlow()

  private val _showLuckyWheel = MutableStateFlow(false)
  val showLuckyWheel: StateFlow<Boolean> = _showLuckyWheel.asStateFlow()

  private val _showReactionAnalytics = MutableStateFlow(false)
  val showReactionAnalytics: StateFlow<Boolean> = _showReactionAnalytics.asStateFlow()

  private val _showSettings = MutableStateFlow(false)
  val showSettings: StateFlow<Boolean> = _showSettings.asStateFlow()

  private val _showPrivacyPolicy = MutableStateFlow(false)
  val showPrivacyPolicy: StateFlow<Boolean> = _showPrivacyPolicy.asStateFlow()

  private val _showAdSimulation = MutableStateFlow<AdRewardType?>(null)
  val showAdSimulation: StateFlow<AdRewardType?> = _showAdSimulation.asStateFlow()

  private val _isAdFree = MutableStateFlow(false)
  val isAdFree: StateFlow<Boolean> = _isAdFree.asStateFlow()

  private var timerJob: Job? = null
  private val context: Context get() = getApplication<Application>().applicationContext

  fun requestRewardedAd(type: AdRewardType) {
    _showAdSimulation.value = type
  }

  fun dismissAdSimulation() {
    _showAdSimulation.value = null
  }

  fun onAdRewardGranted(type: AdRewardType) {
    when (type) {
      AdRewardType.DOUBLE_REWARD -> {
        val session = _activeSession.value
        if (session != null) {
          repository.addBonusReward(
            scoreBonus = session.currentScore,
            xpBonus = session.xpGained,
            coinsBonus = 50
          )
        }
      }
      AdRewardType.SECOND_CHANCE -> {
        val session = _activeSession.value
        if (session != null) {
          _activeSession.value = session.copy(
            isFinished = false,
            remainingSeconds = 5f,
            feedbackText = "İkinci şans! 5 saniyen var!"
          )
          _screenState.value = ScreenState.GAME_PLAY
          startTimer()
        }
      }
      AdRewardType.EXTRA_SPIN -> {
        repository.grantExtraSpin()
      }
      AdRewardType.BONUS_COINS -> {
        repository.addBonusReward(0, 0, 100)
      }
    }
    SoundHapticManager.playFanfare(userProfile.value.soundEnabled)
  }

  fun removeAdsVip() {
    _isAdFree.value = true
  }

  fun openLuckyWheel() { _showLuckyWheel.value = true }
  fun dismissLuckyWheel() { _showLuckyWheel.value = false }

  fun claimLuckyWheelReward(coins: Int, xp: Int) {
    repository.claimLuckyWheel(coins, xp)
  }

  fun openReactionAnalytics() { _showReactionAnalytics.value = true }
  fun dismissReactionAnalytics() { _showReactionAnalytics.value = false }

  fun openSettings() { _showSettings.value = true }
  fun dismissSettings() { _showSettings.value = false }

  fun openPrivacyPolicy() { _showPrivacyPolicy.value = true }
  fun dismissPrivacyPolicy() { _showPrivacyPolicy.value = false }

  fun toggleSound(enabled: Boolean) { repository.toggleSound(enabled) }
  fun toggleVibration(enabled: Boolean) { repository.toggleVibration(enabled) }

  fun navigateTo(screen: ScreenState) {
    _screenState.value = screen
  }

  fun selectTab(tabIndex: Int) {
    _selectedTab.value = tabIndex
    _screenState.value = when (tabIndex) {
      0 -> ScreenState.HOME
      1 -> ScreenState.GAMES_LIST
      2 -> ScreenState.LEADERBOARD
      3 -> ScreenState.PROFILE
      else -> ScreenState.HOME
    }
  }

  fun setLeaderboardTab(index: Int) {
    _leaderboardTab.value = index
  }

  fun setUserName(name: String) {
    repository.updateUserName(name)
  }

  fun getLeaderboard(): List<LeaderboardEntry> {
    return repository.getLeaderboard(_leaderboardTab.value)
  }

  fun requestStartGame(gameType: GameType) {
    _pendingRiskGame.value = gameType
  }

  fun dismissRiskDialog() {
    _pendingRiskGame.value = null
  }

  fun launchGame(gameType: GameType, isRisk: Boolean) {
    _pendingRiskGame.value = null
    val duration = if (isRisk) gameType.riskDurationSeconds.toFloat() else gameType.defaultDurationSeconds.toFloat()

    _activeSession.value = GamePlaySession(
      gameType = gameType,
      isRiskMode = isRisk,
      durationSeconds = duration,
      remainingSeconds = duration,
      currentScore = 0,
      currentStreak = userProfile.value.currentStreak
    )
    _screenState.value = ScreenState.GAME_PLAY
    vibrate(40)
    startTimer()
  }

  private fun startTimer() {
    timerJob?.cancel()
    timerJob = viewModelScope.launch {
      val step = 0.05f
      while (true) {
        delay(50)
        val current = _activeSession.value ?: break
        if (current.isFinished) break

        if (current.isTimerPaused) {
          continue
        }

        val nextTime = max(0f, current.remainingSeconds - step)
        if (nextTime <= 0f) {
          val isWin = current.currentScore > 0 || current.questionsAnswered > 0
          _activeSession.value = current.copy(
            remainingSeconds = 0f,
            isFinished = true,
            isVictory = isWin,
            feedbackText = if (isWin) "${current.questionsAnswered} soru başarıyla çözüldü!" else "Süre doldu!"
          )
          onGameCompleted(isVictory = isWin, accuracy = if (isWin) 85 else 30)
          break
        } else {
          // Warning vibration when <= 3 seconds left
          if (nextTime in 2.95f..3.05f || nextTime in 1.95f..2.05f || nextTime in 0.95f..1.05f) {
            vibrate(20)
          }
          _activeSession.value = current.copy(remainingSeconds = nextTime)
        }
      }
    }
  }

  fun setTimerPaused(paused: Boolean) {
    _activeSession.update { it?.copy(isTimerPaused = paused) }
  }

  fun onRoundSuccess(scoreBonus: Int = 100, xpBonus: Int = 15, coinBonus: Int = 5) {
    val session = _activeSession.value ?: return
    if (session.isFinished) return

    val multiplier = if (session.isRiskMode) 3 else 1
    val addedScore = scoreBonus * multiplier
    val addedXp = xpBonus * multiplier
    val addedCoins = coinBonus * multiplier

    _activeSession.value = session.copy(
      currentScore = session.currentScore + addedScore,
      xpGained = session.xpGained + addedXp,
      coinsEarned = session.coinsEarned + addedCoins,
      questionsAnswered = session.questionsAnswered + 1,
      correctCount = session.correctCount + 1,
      comboStreak = session.comboStreak + 1
      // No time addition: saniye artırma yok!
    )

    val profile = userProfile.value
    SoundHapticManager.playSuccess(profile.soundEnabled)
    SoundHapticManager.vibrateSuccess(context, profile.vibrationEnabled)
  }

  fun onRoundSuccess(scoreBonus: Int, timeBonus: Float) {
    onRoundSuccess(scoreBonus = scoreBonus, xpBonus = 15, coinBonus = 5)
  }

  fun onRoundMistake(reason: String = "Yanlış cevap!") {
    val session = _activeSession.value ?: return
    if (session.isFinished) return

    val profile = userProfile.value
    SoundHapticManager.playFail(profile.soundEnabled)
    SoundHapticManager.vibrateFail(context, profile.vibrationEnabled)

    if (session.isRiskMode) {
      timerJob?.cancel()
      _activeSession.value = session.copy(
        isFinished = true,
        isVictory = false,
        questionsAnswered = session.questionsAnswered + 1,
        mistakeCount = session.mistakeCount + 1,
        feedbackText = "Risk Modunda Hata Yaptın: $reason"
      )
      onGameCompleted(isVictory = false, accuracy = 20)
    } else {
      // Normal mod: Saniye azaltma yok! Direkt yanlış olarak işaretle ve devam et
      _activeSession.value = session.copy(
        questionsAnswered = session.questionsAnswered + 1,
        mistakeCount = session.mistakeCount + 1,
        comboStreak = 0,
        feedbackText = reason
      )
    }
  }

  fun onRoundMistake(timePenalty: Float, reason: String) {
    onRoundMistake(reason = reason)
  }

  fun purchaseAvatar(avatarId: String): Boolean {
    val success = repository.purchaseAvatar(avatarId)
    val profile = userProfile.value
    if (success) {
      SoundHapticManager.playFanfare(profile.soundEnabled)
      SoundHapticManager.vibrateSuccess(context, profile.vibrationEnabled)
    } else {
      SoundHapticManager.playFail(profile.soundEnabled)
      SoundHapticManager.vibrateFail(context, profile.vibrationEnabled)
    }
    return success
  }

  fun onGameSuccess(scoreBonus: Int, accuracy: Int = 100) {
    val session = _activeSession.value ?: return
    if (session.isFinished) return

    val multiplier = if (session.isRiskMode) 3 else 1
    val finalScore = (session.gameType.baseScore + scoreBonus) * multiplier
    val xp = (session.gameType.baseExp) * multiplier

    timerJob?.cancel()
    _activeSession.value = session.copy(
      currentScore = finalScore,
      xpGained = xp,
      isFinished = true,
      isVictory = true,
      accuracyPercent = accuracy,
      feedbackText = "Harika! Başardın!"
    )
    val profile = userProfile.value
    SoundHapticManager.playSuccess(profile.soundEnabled)
    SoundHapticManager.vibrateSuccess(context, profile.vibrationEnabled)

    val timeTakenSeconds = session.durationSeconds - session.remainingSeconds
    val measuredReactionMs = ((timeTakenSeconds * 1000) / 4).toInt().coerceIn(180, 390)
    repository.recordReactionTime(measuredReactionMs)

    onGameCompleted(isVictory = true, accuracy = accuracy)
  }

  fun onGameFail(reason: String = "Hatalı hamle!") {
    val session = _activeSession.value ?: return
    if (session.isFinished) return

    val isWin = session.currentScore > 0 || session.questionsAnswered > 0
    timerJob?.cancel()
    _activeSession.value = session.copy(
      isFinished = true,
      isVictory = isWin,
      accuracyPercent = if (isWin) 70 else 20,
      feedbackText = if (isWin) "${session.correctCount} doğru, ${session.mistakeCount} yanlış!" else reason
    )
    val profile = userProfile.value
    SoundHapticManager.playFail(profile.soundEnabled)
    SoundHapticManager.vibrateFail(context, profile.vibrationEnabled)
    onGameCompleted(isVictory = isWin, accuracy = if (isWin) 70 else 20)
  }

  private fun onGameCompleted(isVictory: Boolean, accuracy: Int) {
    val session = _activeSession.value ?: return
    val totalTime = session.durationSeconds - session.remainingSeconds

    val totalCorrect = session.correctCount
    val totalMistakes = session.mistakeCount
    val computedAccuracy = if (totalCorrect + totalMistakes > 0) {
      ((totalCorrect.toFloat() / (totalCorrect + totalMistakes)) * 100).toInt()
    } else accuracy

    repository.recordGameResult(
      gameType = session.gameType,
      score = if (isVictory) session.currentScore else 30,
      xpEarned = if (isVictory) session.xpGained else 15,
      accuracyPercent = computedAccuracy,
      durationSeconds = max(1, totalTime.toInt()),
      isRiskMode = session.isRiskMode,
      isVictory = isVictory,
      coinsEarned = session.coinsEarned
    )

    if (isVictory && (userProfile.value.currentStreak + 1) % 5 == 0) {
      _showStreakCelebration.value = true
    }

    _screenState.value = ScreenState.GAME_RESULT
  }

  fun dismissStreakCelebration() {
    _showStreakCelebration.value = false
  }

  fun playAgain() {
    val current = _activeSession.value ?: return
    launchGame(current.gameType, current.isRiskMode)
  }

  fun backToHome() {
    timerJob?.cancel()
    _activeSession.value = null
    _screenState.value = ScreenState.HOME
    _selectedTab.value = 0
  }

  fun selectAvatar(avatarId: String) {
    repository.selectAvatar(avatarId)
    vibrate(30)
  }

  fun claimMission(missionId: String) {
    repository.claimMissionReward(missionId)
    vibrate(50)
  }

  fun quickPlay() {
    val allGames = GameType.values()
    val randomGame = allGames.random()
    requestStartGame(randomGame)
  }

  private fun vibrate(durationMs: Long) {
    if (!userProfile.value.vibrationEnabled) return
    SoundHapticManager.vibrateClick(context, true)
  }
}
