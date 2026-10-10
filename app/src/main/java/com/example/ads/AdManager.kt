package com.example.ads

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.ui.components.AdRewardType
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback

object AdManager {
  private const val TAG = "AdManager"

  // User's Real Google AdMob Ad Unit IDs
  const val REWARDED_AD_UNIT_ID = "ca-app-pub-4020568333948380/7841422376" // Ek süre
  const val REWARDED_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-4020568333948380/1209846673" // Ödüllü geçiş

  private var isInitialized = false
  private var rewardedAd: RewardedAd? = null
  private var rewardedInterstitialAd: RewardedInterstitialAd? = null
  private var interstitialAd: InterstitialAd? = null

  private var isRewardedLoading = false
  private var isRewardedInterstitialLoading = false
  private var isInterstitialLoading = false

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
    loadInterstitialAd(context)
  }

  /**
   * Load Rewarded Ad (Ek süre / Ödüllü Reklam)
   */
  fun loadRewardedAd(context: Context) {
    runOnMainThread {
      if (rewardedAd != null || isRewardedLoading) return@runOnMainThread
      isRewardedLoading = true

      try {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
          context,
          REWARDED_AD_UNIT_ID,
          adRequest,
          object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
              rewardedAd = ad
              isRewardedLoading = false
              Log.d(TAG, "Rewarded ad successfully loaded ($REWARDED_AD_UNIT_ID)")
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
              rewardedAd = null
              isRewardedLoading = false
              Log.w(TAG, "Rewarded ad failed to load: ${error.message} (code: ${error.code})")
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
  fun loadRewardedInterstitialAd(context: Context) {
    runOnMainThread {
      if (rewardedInterstitialAd != null || isRewardedInterstitialLoading) return@runOnMainThread
      isRewardedInterstitialLoading = true

      try {
        val adRequest = AdRequest.Builder().build()
        RewardedInterstitialAd.load(
          context,
          REWARDED_INTERSTITIAL_AD_UNIT_ID,
          adRequest,
          object : RewardedInterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedInterstitialAd) {
              rewardedInterstitialAd = ad
              isRewardedInterstitialLoading = false
              Log.d(TAG, "Rewarded Interstitial ad loaded ($REWARDED_INTERSTITIAL_AD_UNIT_ID)")
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
              rewardedInterstitialAd = null
              isRewardedInterstitialLoading = false
              Log.w(TAG, "Rewarded Interstitial ad failed to load: ${error.message}")
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
   * In case unit is configured as standard Interstitial
   */
  fun loadInterstitialAd(context: Context) {
    runOnMainThread {
      if (interstitialAd != null || isInterstitialLoading) return@runOnMainThread
      isInterstitialLoading = true

      try {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
          context,
          REWARDED_INTERSTITIAL_AD_UNIT_ID,
          adRequest,
          object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
              interstitialAd = ad
              isInterstitialLoading = false
              Log.d(TAG, "Interstitial ad loaded ($REWARDED_INTERSTITIAL_AD_UNIT_ID)")
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
              interstitialAd = null
              isInterstitialLoading = false
              Log.w(TAG, "Interstitial ad failed to load: ${error.message}")
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
   * If real ad is loaded, shows Google AdMob rewarded ad.
   * If not loaded yet (e.g. AdMob propagation or offline), triggers onFallback to display simulation.
   */
  fun showRewardedAd(
    activity: Activity?,
    onRewardEarned: () -> Unit,
    onFallback: () -> Unit
  ) {
    val ad = rewardedAd
    if (activity != null && !activity.isFinishing && ad != null) {
      try {
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
          override fun onAdDismissedFullScreenContent() {
            rewardedAd = null
            loadRewardedAd(activity)
          }

          override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            rewardedAd = null
            loadRewardedAd(activity)
            onFallback()
          }
        }

        ad.show(activity) { rewardItem ->
          Log.d(TAG, "User earned reward: ${rewardItem.type} amount=${rewardItem.amount}")
          onRewardEarned()
        }
      } catch (e: Throwable) {
        Log.w(TAG, "showRewardedAd exception: ${e.message}")
        rewardedAd = null
        loadRewardedAd(activity)
        onFallback()
      }
    } else {
      Log.d(TAG, "Rewarded ad not ready yet, using test fallback")
      activity?.let { loadRewardedAd(it) }
      onFallback()
    }
  }

  /**
   * Show 2-game interval Interstitial / Rewarded Interstitial ad
   */
  fun showTransitionAd(
    activity: Activity?,
    onCompleted: () -> Unit
  ) {
    if (activity == null || activity.isFinishing) {
      onCompleted()
      return
    }

    // Try Rewarded Interstitial first
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
        Log.w(TAG, "showTransitionAd rwIntAd exception: ${e.message}")
        rewardedInterstitialAd = null
      }
    }

    // Try regular Interstitial if loaded
    val intAd = interstitialAd
    if (intAd != null) {
      try {
        intAd.fullScreenContentCallback = object : FullScreenContentCallback() {
          override fun onAdDismissedFullScreenContent() {
            interstitialAd = null
            loadInterstitialAd(activity)
            onCompleted()
          }

          override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            interstitialAd = null
            loadInterstitialAd(activity)
            onCompleted()
          }
        }
        intAd.show(activity)
        return
      } catch (e: Throwable) {
        Log.w(TAG, "showTransitionAd intAd exception: ${e.message}")
        interstitialAd = null
      }
    }

    // If neither is ready or show failed, reload and proceed immediately without blocking user
    loadRewardedInterstitialAd(activity)
    loadInterstitialAd(activity)
    onCompleted()
  }

  fun isRewardedAdReady(): Boolean = rewardedAd != null
  fun isTransitionAdReady(): Boolean = rewardedInterstitialAd != null || interstitialAd != null
}
