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
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object AdManager {
  private const val TAG = "AdManager"

  // User's Real Google AdMob Ad Unit IDs
  const val REWARDED_AD_UNIT_ID = "ca-app-pub-4020568333948380/7841422376" // Ek süre (Rewarded)
  const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-4020568333948380/1209846673" // Oyun Sonu Geçiş (Standard Interstitial)
  const val NATIVE_AD_UNIT_ID = "ca-app-pub-4020568333948380/1635848931" // Yerel gelişmiş reklam

  // Official Google AdMob Test Unit IDs (used as test fallback if live unit has No Fill during testing)
  private const val TEST_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
  private const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

  private var isInitialized = false
  private var rewardedAd: RewardedAd? = null
  private var interstitialAd: InterstitialAd? = null

  private var isRewardedLoading = false
  private var isInterstitialLoading = false

  private var rewardedRetryCount = 0
  private var interstitialRetryCount = 0

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
        // Protect developer account: Always mark emulator traffic as safe test ads
        val testConfig = RequestConfiguration.Builder()
          .setTestDeviceIds(listOf(AdRequest.DEVICE_ID_EMULATOR))
          .build()
        MobileAds.setRequestConfiguration(testConfig)

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
    val appContext = context.applicationContext
    loadRewardedAd(appContext)
    loadInterstitialAd(appContext)
  }

  /**
   * Load Rewarded Ad (Ek süre / Ödüllü Reklam)
   */
  fun loadRewardedAd(context: Context, useTestUnit: Boolean = false) {
    val appContext = context.applicationContext
    runOnMainThread {
      if (rewardedAd != null || isRewardedLoading) return@runOnMainThread
      isRewardedLoading = true

      val adUnitId = if (useTestUnit) TEST_REWARDED_AD_UNIT_ID else REWARDED_AD_UNIT_ID

      try {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
          appContext,
          adUnitId,
          adRequest,
          object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
              rewardedAd = ad
              isRewardedLoading = false
              rewardedRetryCount = 0
              Log.d(TAG, "Rewarded ad successfully loaded ($adUnitId)")
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
              rewardedAd = null
              isRewardedLoading = false
              Log.w(TAG, "Rewarded ad failed to load: ${error.message} (code: ${error.code}) for unit: $adUnitId")

              // If live ad has No Fill (code 3) during testing/debug, fallback to official Google test unit
              if (!useTestUnit && BuildConfig.DEBUG && error.code == 3) {
                Log.d(TAG, "Live unit has No Fill in debug, trying Google official test unit...")
                loadRewardedAd(appContext, useTestUnit = true)
                return
              }

              // Exponential backoff: 15s, 30s, 60s (max 3 retries)
              if (rewardedRetryCount < 3) {
                val delayMs = 15000L * (1 shl rewardedRetryCount)
                rewardedRetryCount++
                mainHandler.postDelayed({
                  loadRewardedAd(appContext)
                }, delayMs)
              }
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
   * Load Standard Interstitial Ad (Oyun Sonu Geçiş Reklamı)
   */
  fun loadInterstitialAd(context: Context, useTestUnit: Boolean = false) {
    val appContext = context.applicationContext
    runOnMainThread {
      if (interstitialAd != null || isInterstitialLoading) return@runOnMainThread
      isInterstitialLoading = true

      val adUnitId = if (useTestUnit) TEST_INTERSTITIAL_AD_UNIT_ID else INTERSTITIAL_AD_UNIT_ID

      try {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
          appContext,
          adUnitId,
          adRequest,
          object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
              interstitialAd = ad
              isInterstitialLoading = false
              interstitialRetryCount = 0
              Log.d(TAG, "Interstitial ad loaded successfully ($adUnitId)")
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
              interstitialAd = null
              isInterstitialLoading = false
              Log.w(TAG, "Interstitial ad failed to load: ${error.message} (code: ${error.code}) for unit: $adUnitId")

              // If live ad has No Fill in debug, fallback to official Google test unit
              if (!useTestUnit && BuildConfig.DEBUG && error.code == 3) {
                Log.d(TAG, "Live Interstitial has No Fill in debug, trying test unit...")
                loadInterstitialAd(appContext, useTestUnit = true)
                return
              }

              // Exponential backoff: 15s, 30s, 60s (max 3 retries)
              if (interstitialRetryCount < 3) {
                val delayMs = 15000L * (1 shl interstitialRetryCount)
                interstitialRetryCount++
                mainHandler.postDelayed({
                  loadInterstitialAd(appContext)
                }, delayMs)
              }
            }
          }
        )
      } catch (e: Throwable) {
        interstitialAd = null
        isInterstitialLoading = false
        Log.w(TAG, "loadInterstitialAd exception: ${e.message}")
      }
    }
  }

  /**
   * Show Rewarded Ad (Ek süre / İkinci Şans / Çarkıfelek)
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

    val ad = rewardedAd
    if (ad != null) {
      try {
        var earnedReward = false
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
          override fun onAdDismissedFullScreenContent() {
            rewardedAd = null
            loadRewardedAd(activity.applicationContext)
            if (earnedReward) {
              onAdClosed()
            }
          }

          override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            Log.w(TAG, "Rewarded ad failed to show: ${adError.message}")
            rewardedAd = null
            loadRewardedAd(activity.applicationContext)
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
        loadRewardedAd(activity.applicationContext)
      }
    }

    // Ad is not ready yet -> start preloading immediately and inform caller
    rewardedRetryCount = 0
    loadRewardedAd(activity.applicationContext)
    onAdNotReady()
  }

  /**
   * Show Standard 2-game interval Interstitial ad
   */
  fun showTransitionAd(
    activity: Activity?,
    onCompleted: () -> Unit
  ) {
    if (activity == null || activity.isFinishing) {
      onCompleted()
      return
    }

    val ad = interstitialAd
    if (ad != null) {
      try {
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
          override fun onAdDismissedFullScreenContent() {
            interstitialAd = null
            loadInterstitialAd(activity.applicationContext)
            onCompleted()
          }

          override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            interstitialAd = null
            loadInterstitialAd(activity.applicationContext)
            onCompleted()
          }
        }
        ad.show(activity)
        return
      } catch (e: Throwable) {
        Log.w(TAG, "showTransitionAd exception: ${e.message}")
        interstitialAd = null
      }
    }

    // If not ready, preload for next time and continue without blocking
    loadInterstitialAd(activity.applicationContext)
    onCompleted()
  }

  fun isRewardedAdReady(): Boolean = rewardedAd != null
  fun isTransitionAdReady(): Boolean = interstitialAd != null
}
