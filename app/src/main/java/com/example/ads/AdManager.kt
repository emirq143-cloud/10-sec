package com.example.ads

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.BuildConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback

object AdManager {
  private const val TAG = "AdManager"

  // User's Real Google AdMob Ad Unit IDs
  const val REWARDED_AD_UNIT_ID = "ca-app-pub-4020568333948380/7841422376" // Ek süre (Rewarded)
  const val REWARDED_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-4020568333948380/1209846673" // Ödüllü geçiş (Rewarded Interstitial)
  const val NATIVE_AD_UNIT_ID = "ca-app-pub-4020568333948380/1635848931" // Yerel gelişmiş reklam

  // Official Google AdMob Test Unit IDs (used as test fallback if live unit has No Fill during testing)
  private const val TEST_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
  private const val TEST_REWARDED_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/5354046379"

  private var isInitialized = false
  private var rewardedAd: RewardedAd? = null
  private var rewardedInterstitialAd: RewardedInterstitialAd? = null

  private var isRewardedLoading = false
  private var isRewardedInterstitialLoading = false

  private val mainHandler = Handler(Looper.getMainLooper())

  private fun runOnMainThread(action: () -> Unit) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
      try {
        action()
      } catch (e: Throwable) {
        Log.w(TAG, "Main thread action notice: ${e.message}")
      }
    } else {
      mainHandler.post {
        try {
          action()
        } catch (e: Throwable) {
          Log.w(TAG, "Main thread post notice: ${e.message}")
        }
      }
    }
  }

  /**
   * Initialize Google Mobile Ads SDK safely
   */
  fun initialize(context: Context) {
    if (isInitialized) return
    val appContext = context.applicationContext
    runOnMainThread {
      try {
        MobileAds.initialize(appContext) { status ->
          isInitialized = true
          Log.d(TAG, "AdMob SDK Initialized: $status")
          preloadAds(appContext)
        }
      } catch (e: Throwable) {
        Log.w(TAG, "AdMob initialization notice: ${e.message}")
      }
    }
  }

  fun preloadAds(context: Context) {
    loadRewardedAd(context)
    loadRewardedInterstitialAd(context)
  }

  /**
   * Load Rewarded Ad (Ek süre / Ödüllü Reklam)
   */
  fun loadRewardedAd(context: Context, useTestUnit: Boolean = false) {
    runOnMainThread {
      if (rewardedAd != null || isRewardedLoading) return@runOnMainThread
      isRewardedLoading = true

      val adUnitId = if (useTestUnit) TEST_REWARDED_AD_UNIT_ID else REWARDED_AD_UNIT_ID

      try {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
          context,
          adUnitId,
          adRequest,
          object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
              rewardedAd = ad
              isRewardedLoading = false
              Log.d(TAG, "Rewarded ad successfully loaded ($adUnitId)")
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
              rewardedAd = null
              isRewardedLoading = false
              Log.w(TAG, "Rewarded ad failed to load: ${error.message} (code: ${error.code}) for unit: $adUnitId")

              // If live ad has No Fill (code 3) during testing/debug, fallback to official Google test unit
              if (!useTestUnit && BuildConfig.DEBUG && error.code == 3) {
                Log.d(TAG, "Live unit has No Fill in debug, trying Google official test unit...")
                loadRewardedAd(context, useTestUnit = true)
                return
              }

              // Retry loading after 15 seconds so an ad is ready for next attempt
              mainHandler.postDelayed({
                loadRewardedAd(context)
              }, 15000)
            }
          }
        )
      } catch (e: Throwable) {
        rewardedAd = null
        isRewardedLoading = false
        Log.w(TAG, "loadRewardedAd exception: ${e.message}")
      }
    }
  }

  /**
   * Load Rewarded Interstitial Ad (Ödüllü Geçiş Reklamı)
   */
  fun loadRewardedInterstitialAd(context: Context, useTestUnit: Boolean = false) {
    runOnMainThread {
      if (rewardedInterstitialAd != null || isRewardedInterstitialLoading) return@runOnMainThread
      isRewardedInterstitialLoading = true

      val adUnitId = if (useTestUnit) TEST_REWARDED_INTERSTITIAL_AD_UNIT_ID else REWARDED_INTERSTITIAL_AD_UNIT_ID

      try {
        val adRequest = AdRequest.Builder().build()
        RewardedInterstitialAd.load(
          context,
          adUnitId,
          adRequest,
          object : RewardedInterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedInterstitialAd) {
              rewardedInterstitialAd = ad
              isRewardedInterstitialLoading = false
              Log.d(TAG, "Rewarded Interstitial ad loaded ($adUnitId)")
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
              rewardedInterstitialAd = null
              isRewardedInterstitialLoading = false
              Log.w(TAG, "Rewarded Interstitial ad failed to load: ${error.message} (code: ${error.code}) for unit: $adUnitId")

              // If live ad has No Fill in debug, fallback to official Google test unit
              if (!useTestUnit && BuildConfig.DEBUG && error.code == 3) {
                Log.d(TAG, "Live Rewarded Interstitial has No Fill in debug, trying test unit...")
                loadRewardedInterstitialAd(context, useTestUnit = true)
                return
              }

              // Retry after 15 seconds
              mainHandler.postDelayed({
                loadRewardedInterstitialAd(context)
              }, 15000)
            }
          }
        )
      } catch (e: Throwable) {
        rewardedInterstitialAd = null
        isRewardedInterstitialLoading = false
        Log.w(TAG, "loadRewardedInterstitialAd exception: ${e.message}")
      }
    }
  }

  /**
   * Show Rewarded Ad (Ek süre / İkinci Şans / Çarkıfelek)
   * Uses real Google AdMob Rewarded or Rewarded Interstitial ad.
   */
  fun showRewardedAd(
    activity: Activity?,
    onRewardEarned: () -> Unit,
    onAdClosed: () -> Unit = {},
    onAdNotReady: () -> Unit = {}
  ) {
    if (activity == null || activity.isFinishing) {
      onAdNotReady()
      return
    }

    // 1. Try primary Rewarded Ad
    val ad = rewardedAd
    if (ad != null) {
      try {
        var earnedReward = false
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
          override fun onAdDismissedFullScreenContent() {
            rewardedAd = null
            loadRewardedAd(activity)
            if (earnedReward) {
              onAdClosed()
            }
          }

          override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            Log.w(TAG, "Rewarded ad failed to show: ${adError.message}")
            rewardedAd = null
            loadRewardedAd(activity)
            onAdNotReady()
          }
        }

        ad.show(activity) { rewardItem ->
          Log.d(TAG, "User earned reward: ${rewardItem.type} amount=${rewardItem.amount}")
          earnedReward = true
          onRewardEarned()
        }
        return
      } catch (e: Throwable) {
        Log.w(TAG, "showRewardedAd exception: ${e.message}")
        rewardedAd = null
        loadRewardedAd(activity)
      }
    }

    // 2. Fallback: Try Rewarded Interstitial if available
    val rwInt = rewardedInterstitialAd
    if (rwInt != null) {
      try {
        var earnedReward = false
        rwInt.fullScreenContentCallback = object : FullScreenContentCallback() {
          override fun onAdDismissedFullScreenContent() {
            rewardedInterstitialAd = null
            loadRewardedInterstitialAd(activity)
            if (earnedReward) {
              onAdClosed()
            }
          }

          override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            Log.w(TAG, "Rewarded Interstitial failed to show: ${adError.message}")
            rewardedInterstitialAd = null
            loadRewardedInterstitialAd(activity)
            onAdNotReady()
          }
        }

        rwInt.show(activity) { rewardItem ->
          Log.d(TAG, "User earned reward via Rewarded Interstitial: ${rewardItem.type}")
          earnedReward = true
          onRewardEarned()
        }
        return
      } catch (e: Throwable) {
        Log.w(TAG, "showRewardedAd rwInt exception: ${e.message}")
        rewardedInterstitialAd = null
        loadRewardedInterstitialAd(activity)
      }
    }

    // 3. Ad is not ready yet -> start preloading immediately and inform caller
    loadRewardedAd(activity)
    loadRewardedInterstitialAd(activity)
    onAdNotReady()
  }

  /**
   * Show 4-game interval Rewarded Interstitial transition ad
   */
  fun showTransitionAd(
    activity: Activity?,
    onCompleted: () -> Unit
  ) {
    if (activity == null || activity.isFinishing) {
      onCompleted()
      return
    }

    val rwIntAd = rewardedInterstitialAd
    if (rwIntAd != null) {
      try {
        rwIntAd.fullScreenContentCallback = object : FullScreenContentCallback() {
          override fun onAdDismissedFullScreenContent() {
            rewardedInterstitialAd = null
            loadRewardedInterstitialAd(activity)
            onCompleted()
          }

          override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            rewardedInterstitialAd = null
            loadRewardedInterstitialAd(activity)
            onCompleted()
          }
        }
        rwIntAd.show(activity) { rewardItem ->
          Log.d(TAG, "Transition reward earned: ${rewardItem.type}")
        }
        return
      } catch (e: Throwable) {
        Log.w(TAG, "showTransitionAd exception: ${e.message}")
        rewardedInterstitialAd = null
      }
    }

    // If not ready, preload for next time and continue without blocking
    loadRewardedInterstitialAd(activity)
    onCompleted()
  }

  fun isRewardedAdReady(): Boolean = rewardedAd != null || rewardedInterstitialAd != null
  fun isTransitionAdReady(): Boolean = rewardedInterstitialAd != null
}
