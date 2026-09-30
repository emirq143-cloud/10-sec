package com.example.ui.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * High-performance sound effects and haptic vibration feedback manager.
 * Respects player preferences and provides arcade-feel game audio.
 */
object SoundHapticManager {
  private var toneGenerator: ToneGenerator? = null

  init {
    try {
      toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 85)
    } catch (_: Exception) {
      toneGenerator = null
    }
  }

  fun playTap(soundEnabled: Boolean) {
    if (!soundEnabled) return
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 35)
    } catch (_: Exception) {}
  }

  fun playSuccess(soundEnabled: Boolean) {
    if (!soundEnabled) return
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 120)
    } catch (_: Exception) {}
  }

  fun playFail(soundEnabled: Boolean) {
    if (!soundEnabled) return
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 180)
    } catch (_: Exception) {}
  }

  fun playTick(soundEnabled: Boolean) {
    if (!soundEnabled) return
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 25)
    } catch (_: Exception) {}
  }

  fun playWheelTick(soundEnabled: Boolean) {
    if (!soundEnabled) return
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_DTMF_D, 20)
    } catch (_: Exception) {}
  }

  fun playFanfare(soundEnabled: Boolean) {
    if (!soundEnabled) return
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 250)
    } catch (_: Exception) {}
  }

  private fun getVibrator(context: Context): Vibrator? {
    return try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
      } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
      }
    } catch (_: Exception) {
      null
    }
  }

  fun vibrateClick(context: Context, vibrationEnabled: Boolean) {
    if (!vibrationEnabled) return
    val vibrator = getVibrator(context) ?: return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(20)
      }
    } catch (_: Exception) {}
  }

  fun vibrateSuccess(context: Context, vibrationEnabled: Boolean) {
    if (!vibrationEnabled) return
    val vibrator = getVibrator(context) ?: return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 30, 40, 50), -1))
      } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(100)
      }
    } catch (_: Exception) {}
  }

  fun vibrateFail(context: Context, vibrationEnabled: Boolean) {
    if (!vibrationEnabled) return
    val vibrator = getVibrator(context) ?: return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(120)
      }
    } catch (_: Exception) {}
  }
}
