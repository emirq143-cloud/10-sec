package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.NeonGold
import com.example.ui.theme.VibrantGreen
import com.example.ui.theme.VibrantPink

enum class GameCategory(
  val title: String,
  val iconName: String,
  val accentColor: Color
) {
  REFLEX("Refleks", "flash_on", NeonGold),
  LOGIC("Mantık", "psychology", BluePrimary),
  MEMORY("Hafıza", "visibility", VibrantPink),
  ATTENTION("Dikkat", "center_focus_strong", VibrantGreen)
}

enum class GameType(
  val id: String,
  val title: String,
  val subtitle: String,
  val category: GameCategory,
  val defaultDurationSeconds: Int = 10,
  val riskDurationSeconds: Int = 5,
  val baseScore: Int = 200,
  val baseExp: Int = 100
) {
  REFLEX_DOTS(
    id = "reflex_dots",
    title = "Hızlı Dokun",
    subtitle = "10 saniyede yeşil noktalara dokun, kırmızıdan kaçın!",
    category = GameCategory.REFLEX,
    defaultDurationSeconds = 10,
    riskDurationSeconds = 6,
    baseScore = 250,
    baseExp = 120
  ),
  STROOP_COLOR(
    id = "stroop_color",
    title = "Renk Seçimi",
    subtitle = "Sıradaki rengi hızla seç! Yanıltıcı renklere kanma!",
    category = GameCategory.REFLEX,
    defaultDurationSeconds = 8,
    riskDurationSeconds = 5,
    baseScore = 240,
    baseExp = 110
  ),
  MEMORY_SYMBOLS(
    id = "memory_symbols",
    title = "Hafıza",
    subtitle = "Sembolleri incele, sonra hangisinin değiştiğini bul!",
    category = GameCategory.MEMORY,
    defaultDurationSeconds = 8,
    riskDurationSeconds = 5,
    baseScore = 300,
    baseExp = 140
  ),
  QUICK_MATH(
    id = "quick_math",
    title = "Hızlı Matematik",
    subtitle = "Süre dolmadan matematiksel denklemi hemen çöz!",
    category = GameCategory.LOGIC,
    defaultDurationSeconds = 8,
    riskDurationSeconds = 4,
    baseScore = 280,
    baseExp = 130
  ),
  FIND_DIFFERENCE(
    id = "find_difference",
    title = "Farkı Bul",
    subtitle = "İki desen arasındaki gizli farkı anında tespit et!",
    category = GameCategory.ATTENTION,
    defaultDurationSeconds = 10,
    riskDurationSeconds = 6,
    baseScore = 320,
    baseExp = 150
  ),
  LOGIC_PATTERN(
    id = "logic_pattern",
    title = "Mantık & Örüntü",
    subtitle = "Geometrik şifreyi çöz ve sıradaki sayıyı bul!",
    category = GameCategory.LOGIC,
    defaultDurationSeconds = 8,
    riskDurationSeconds = 5,
    baseScore = 270,
    baseExp = 125
  ),
  ESTIMATION_BAR(
    id = "estimation_bar",
    title = "Tahmin",
    subtitle = "Hızlı hareket eden çubuğu tam %50 noktasında durdur!",
    category = GameCategory.ATTENTION,
    defaultDurationSeconds = 8,
    riskDurationSeconds = 4,
    baseScore = 300,
    baseExp = 135
  ),
  WORKING_MEMORY(
    id = "working_memory",
    title = "Çalışan Hafıza",
    subtitle = "Sayı dizisini aklında tut ve tam ters sırayla gir!",
    category = GameCategory.MEMORY,
    defaultDurationSeconds = 9,
    riskDurationSeconds = 5,
    baseScore = 310,
    baseExp = 145
  )
}
