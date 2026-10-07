package com.example.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.GameType
import com.example.ui.components.AdRewardType
import com.example.ui.components.AdSimulationDialog
import com.example.ui.components.GameResultScreen
import com.example.ui.components.LuckyWheelDialog
import com.example.ui.components.PrivacyPolicyDialog
import com.example.ui.components.ReactionAnalyticsDialog
import com.example.ui.components.ReviveContinueDialog
import com.example.ui.components.RiskSelectDialog
import com.example.ui.components.SettingsDialog
import com.example.ui.components.StreakCelebrationDialog
import com.example.ui.components.TransitionAdDialog
import com.example.ui.screens.GamesListScreen
import com.example.ui.screens.GameplayContainerScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.StartScreen
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.CardWhite
import com.example.ui.theme.LightBg
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.TextDarkSecondary

data class NavItem(
  val label: String,
  val icon: @Composable () -> Unit
)

@Composable
fun MainScreen(viewModel: GameViewModel) {
  val screenState by viewModel.screenState.collectAsStateWithLifecycle()
  val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
  val avatars by viewModel.avatars.collectAsStateWithLifecycle()
  val dailyMissions by viewModel.dailyMissions.collectAsStateWithLifecycle()
  val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
  val activeSession by viewModel.activeSession.collectAsStateWithLifecycle()
  val pendingRiskGame by viewModel.pendingRiskGame.collectAsStateWithLifecycle()
  val showStreakCelebration by viewModel.showStreakCelebration.collectAsStateWithLifecycle()
  val leaderboardTab by viewModel.leaderboardTab.collectAsStateWithLifecycle()

  val showLuckyWheel by viewModel.showLuckyWheel.collectAsStateWithLifecycle()
  val showReactionAnalytics by viewModel.showReactionAnalytics.collectAsStateWithLifecycle()
  val showSettings by viewModel.showSettings.collectAsStateWithLifecycle()
  val showPrivacyPolicy by viewModel.showPrivacyPolicy.collectAsStateWithLifecycle()
  val showAdSimulation by viewModel.showAdSimulation.collectAsStateWithLifecycle()
  val showTransitionAdSimulation by viewModel.showTransitionAdSimulation.collectAsStateWithLifecycle()
  val showReviveDialog by viewModel.showReviveDialog.collectAsStateWithLifecycle()
  val isAdFree by viewModel.isAdFree.collectAsStateWithLifecycle()
  val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
  val selectedCity by viewModel.selectedCity.collectAsStateWithLifecycle()
  val isSubmittingScore by viewModel.isSubmittingScore.collectAsStateWithLifecycle()
  val syncStatusMessage by viewModel.syncStatusMessage.collectAsStateWithLifecycle()
  val cloudLeaderboard by viewModel.cloudLeaderboard.collectAsStateWithLifecycle()

  val context = LocalContext.current
  val activity = context as? Activity

  // Handle Android back button
  BackHandler(enabled = screenState != ScreenState.START && screenState != ScreenState.HOME) {
    if (screenState == ScreenState.GAME_PLAY || screenState == ScreenState.GAME_RESULT) {
      viewModel.backToHome()
    } else {
      viewModel.navigateTo(ScreenState.HOME)
    }
  }

  val navItems = listOf(
    NavItem("Ana Sayfa") { Icon(Icons.Default.Home, contentDescription = "Ana Sayfa") },
    NavItem("Oyunlar") { Icon(Icons.Default.SportsEsports, contentDescription = "Oyunlar") },
    NavItem("Liderlik") { Icon(Icons.Default.EmojiEvents, contentDescription = "Liderlik") },
    NavItem("Profil") { Icon(Icons.Default.Person, contentDescription = "Profil") }
  )

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = Color.Transparent,
    bottomBar = {
      // Crisp white bottom navigation bar matching the mockup!
      if (screenState in listOf(ScreenState.HOME, ScreenState.GAMES_LIST, ScreenState.LEADERBOARD, ScreenState.PROFILE)) {
        NavigationBar(
          containerColor = CardWhite,
          contentColor = TextDarkSecondary,
          tonalElevation = 6.dp,
          modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .border(1.dp, CardBorderLight)
        ) {
          navItems.forEachIndexed { index, item ->
            val isSelected = selectedTab == index
            NavigationBarItem(
              selected = isSelected,
              onClick = { viewModel.selectTab(index) },
              icon = item.icon,
              label = {
                Text(
                  text = item.label,
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
              },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BluePrimary,
                selectedTextColor = BluePrimary,
                indicatorColor = Color(0xFFEFF6FF),
                unselectedIconColor = Color(0xFF94A3B8),
                unselectedTextColor = Color(0xFF94A3B8)
              )
            )
          }
        }
      }
    }
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      AnimatedContent(
        targetState = screenState,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "ScreenTransition"
      ) { state ->
        when (state) {
          ScreenState.START -> StartScreen(
            onStartGame = { viewModel.navigateTo(ScreenState.HOME) },
            onOpenLeaderboard = {
              viewModel.selectTab(2)
            },
            onOpenProfile = {
              viewModel.selectTab(3)
            },
            onOpenPrivacyPolicy = {
              viewModel.openPrivacyPolicy()
            }
          )
          ScreenState.HOME -> HomeScreen(
            userProfile = userProfile,
            dailyMissions = dailyMissions,
            onQuickPlay = { viewModel.quickPlay() },
            onSelectGame = { gameType -> viewModel.requestStartGame(gameType) },
            onOpenLeaderboard = { viewModel.selectTab(2) },
            onOpenGames = { viewModel.selectTab(1) },
            onClaimMission = { missionId -> viewModel.claimMission(missionId) },
            onOpenLuckyWheel = { viewModel.openLuckyWheel() },
            onOpenReactionAnalytics = { viewModel.openReactionAnalytics() },
            onOpenSettings = { viewModel.openSettings() }
          )
          ScreenState.GAMES_LIST -> GamesListScreen(
            onSelectGame = { gameType -> viewModel.requestStartGame(gameType) }
          )
          ScreenState.LEADERBOARD -> LeaderboardScreen(
            userProfile = userProfile,
            entries = viewModel.getLeaderboard(selectedCity),
            selectedTab = leaderboardTab,
            onSelectTab = { viewModel.setLeaderboardTab(it) },
            onUpdateName = { viewModel.setUserName(it) },
            currentUser = currentUser,
            selectedCity = selectedCity,
            onSelectCity = { viewModel.setSelectedCity(it) },
            isSubmittingScore = isSubmittingScore,
            syncStatusMessage = syncStatusMessage,
            onDismissSyncMessage = { viewModel.clearSyncStatusMessage() },
            onSignInWithGoogle = { context, onResult -> viewModel.signInWithGoogle(context, onResult) },
            onSignOut = { context -> viewModel.signOut(context) },
            onSubmitScoreToCloud = { onComplete -> viewModel.submitScoreToCloud(onComplete) }
          )
          ScreenState.PROFILE -> ProfileScreen(
            userProfile = userProfile,
            avatars = avatars,
            onSelectAvatar = { viewModel.selectAvatar(it) },
            onPurchaseAvatar = { viewModel.purchaseAvatar(it) },
            onOpenSettings = { viewModel.openSettings() },
            onOpenReactionAnalytics = { viewModel.openReactionAnalytics() }
          )
          ScreenState.GAME_PLAY -> {
            activeSession?.let { session ->
              GameplayContainerScreen(
                session = session,
                onBack = { viewModel.backToHome() },
                onRoundSuccess = { scoreBonus, timeBonus -> viewModel.onRoundSuccess(scoreBonus, timeBonus) },
                onRoundMistake = { timePenalty, reason -> viewModel.onRoundMistake(timePenalty, reason) },
                onSetTimerPaused = { paused -> viewModel.setTimerPaused(paused) },
                onSuccess = { scoreBonus, accuracy -> viewModel.onGameSuccess(scoreBonus, accuracy) },
                onFail = { reason -> viewModel.onGameFail(reason) },
                onWatchAdRevive = { viewModel.triggerReviveRewardedAd(activity) }
              )
            }
          }
          ScreenState.GAME_RESULT -> {
            activeSession?.let { session ->
              GameResultScreen(
                session = session,
                onContinue = { viewModel.handleGameTransition(activity) { viewModel.backToHome() } },
                onPlayAgain = { viewModel.handleGameTransition(activity) { viewModel.playAgain() } },
                onWatchAdDouble = { viewModel.requestRewardedAdWithActivity(activity, AdRewardType.DOUBLE_REWARD) },
                onWatchAdSecondChance = { viewModel.requestRewardedAdWithActivity(activity, AdRewardType.SECOND_CHANCE) }
              )
            }
          }
        }
      }

      // Risk selection dialog popup
      pendingRiskGame?.let { game ->
        RiskSelectDialog(
          gameType = game,
          onSelectMode = { isRisk -> viewModel.launchGame(game, isRisk) },
          onDismiss = { viewModel.dismissRiskDialog() }
        )
      }

      // Streak celebration modal
      if (showStreakCelebration) {
        StreakCelebrationDialog(
          streak = userProfile.currentStreak,
          onDismiss = { viewModel.dismissStreakCelebration() }
        )
      }

      // Feature 5: Lucky Wheel Dialog
      if (showLuckyWheel) {
        LuckyWheelDialog(
          soundEnabled = userProfile.soundEnabled,
          vibrationEnabled = userProfile.vibrationEnabled,
          hasSpunToday = userProfile.hasSpunWheelToday,
          onRewardClaimed = { coins, xp -> viewModel.claimLuckyWheelReward(coins, xp) },
          onWatchAdForSpin = {
            viewModel.dismissLuckyWheel()
            viewModel.requestRewardedAdWithActivity(activity, AdRewardType.EXTRA_SPIN)
          },
          onDismiss = { viewModel.dismissLuckyWheel() }
        )
      }

      // Feature 4: Reaction Time & Reflex Analytics Dialog
      if (showReactionAnalytics) {
        ReactionAnalyticsDialog(
          userProfile = userProfile,
          onPlayReflex = {
            viewModel.dismissReactionAnalytics()
            viewModel.requestStartGame(GameType.REFLEX_DOTS)
          },
          onDismiss = { viewModel.dismissReactionAnalytics() }
        )
      }

      // Feature 3: Settings Dialog
      if (showSettings) {
        SettingsDialog(
          soundEnabled = userProfile.soundEnabled,
          vibrationEnabled = userProfile.vibrationEnabled,
          onToggleSound = { viewModel.toggleSound(it) },
          onToggleVibration = { viewModel.toggleVibration(it) },
          onOpenPrivacyPolicy = {
            viewModel.dismissSettings()
            viewModel.openPrivacyPolicy()
          },
          onDismiss = { viewModel.dismissSettings() }
        )
      }

      // Privacy Policy Dialog (Play Store compliance)
      if (showPrivacyPolicy) {
        PrivacyPolicyDialog(
          onDismiss = { viewModel.dismissPrivacyPolicy() }
        )
      }

      // 2-Game Transition / Interstitial Ad Dialog
      if (showTransitionAdSimulation) {
        TransitionAdDialog(
          onDismiss = { viewModel.dismissTransitionAdSimulation() }
        )
      }

      // In-game Revive / Second Chance Rewarded Ad Dialog (ca-app-pub-4020568333948380/7841422376)
      showReviveDialog?.let { reason ->
        val currentScore = activeSession?.currentScore ?: 0
        val combo = activeSession?.comboStreak ?: 0
        ReviveContinueDialog(
          reason = reason,
          currentScore = currentScore,
          comboStreak = combo,
          onWatchAdRevive = {
            viewModel.triggerReviveRewardedAd(activity)
          },
          onSkipAndEnd = {
            viewModel.skipReviveAndEndGame()
          }
        )
      }

      // Ad Simulation Dialog (Google AdMob format for rewarded video ads)
      showAdSimulation?.let { adType ->
        AdSimulationDialog(
          rewardType = adType,
          onRewardGranted = { viewModel.onAdRewardGranted(adType) },
          onDismiss = { viewModel.dismissAdSimulation() }
        )
      }
    }
  }
}
