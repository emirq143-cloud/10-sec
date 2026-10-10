package com.example.util

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppLanguage(val code: String, val displayName: String, val flag: String) {
  TR("tr", "Türkçe", "🇹🇷"),
  EN("en", "English", "🇺🇸")
}

object LocalizationManager {
  private val _currentLanguage = MutableStateFlow(AppLanguage.TR)
  val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

  fun setLanguage(context: Context, lang: AppLanguage) {
    _currentLanguage.value = lang
  }

  private val turkishStrings: Map<String, String> = mapOf(
    // Common & Navigation
    "settings" to "Ayarlar",
    "sound_fx" to "Ses Efektleri",
    "haptic_vibration" to "Titreşim",
    "language" to "Dil",
    "privacy_policy" to "Gizlilik Politikası",
    "play_btn" to "Oyna",
    "level_label" to "Seviye",
    "score_label" to "Skor",
    "streak_label" to "Seri",
    "ranking_btn" to "Sıralama",
    "all_filter" to "Tümü",
    "continue_game" to "Devam Et",
    "play_again" to "Tekrar Oyna",
    "skip_and_finish" to "Atla ve Bitir",
    "details" to "Detaylar",
    "spin_now" to "Çevir",
    "claimed" to "Alındı",
    "claim_reward" to "Ödülü Al",

    // Categories
    "cat_reflex" to "Refleks",
    "cat_logic" to "Mantık",
    "cat_memory" to "Hafıza",
    "cat_attention" to "Dikkat",

    // Home & Dashboard
    "games_title" to "Mini Oyunlar",
    "games_subtitle" to "10 Saniyelik Hızlı Mücadeleler",
    "daily_wheel_title" to "Günün Şans Çarkı",
    "daily_wheel_ready" to "Çevirmeye Hazır!",
    "daily_wheel_done" to "Bugün Çevrildi",
    "reaction_speed_title" to "Tepki Hızı Analizi",
    "reaction_analysis_btn" to "Analizi Gör",
    "daily_missions" to "Günlük Görevler",
    "quick_play" to "Hızlı Oyun",
    "quick_play_title" to "Hızlı Başla",
    "quick_play_sub" to "Rastgele Oyun Modu",

    // Risk Mode Dialog
    "safe_mode" to "Güvenli Mod",
    "safe_mode_desc" to "Standart puanlama, ceza yok",
    "risk_mode" to "Risk Modu",
    "risk_mode_desc" to "Hata yaparsan süre düşer, ödül 2x!",
    "risk_mode_reward_active" to "🔥 2X Risk Modu Bonusu Aktif!",

    // Game Results & Ads
    "game_won_title" to "Tebrikler! Kazandın!",
    "game_lost_title" to "Süre Doldu!",
    "game_won_desc" to "Harika bir refleks ve hız performansı!",
    "xp_gained" to "Kazanılan XP",
    "watch_ad_2x" to "2x Skor ve Ödül Kazan",
    "watch_ad_2x_sub" to "+%d ekstra beyin skoru",
    "watch_ad_second_chance" to "İkinci Şans - Devam Et",

    // Revive / Continue
    "revive_title" to "İkinci Şans!",
    "revive_btn" to "Reklam İzle ve Devam Et",
    "revive_desc" to "+5 saniye ek süre ile seriyi koru",

    // Games Instructions
    "inspect_symbols" to "Sembolleri İncele",
    "tap_changed_symbol" to "Değişen Sembole Dokun",
    "tap_green" to "Yeşile Dokun",
    "avoid_red" to "Kırmızıdan Kaçın",
    "rule_stroop_word" to "Yazılan Kelimenin Anlamı",
    "rule_stroop_ink" to "Yazının Mürekkep Rengi",
    "rule_prefix" to "Kural:",
    "stroop_tap_balloon" to "Doğru Balona Dokun",
    "stopped" to "Durduruldu",
    "press_stop" to "DUR'a Bas",
    "find_diff_instruction" to "Farklı Olanı Bul"
  )

  fun string(key: String, vararg args: Any): String {
    val pattern = turkishStrings[key] ?: key
    return if (args.isNotEmpty()) {
      try {
        String.format(pattern, *args)
      } catch (_: Exception) {
        pattern
      }
    } else {
      pattern
    }
  }
}
