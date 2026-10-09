package com.example.ui.screens.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CircularTimer
import com.example.ui.components.GameTopBar
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CyanBright
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.TextDark
import com.example.ui.theme.VibrantGreen
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantRed
import kotlinx.coroutines.delay
import kotlin.math.hypot
import kotlin.random.Random

enum class SceneTheme(
  val title: String,
  val targetX: Float,
  val targetY: Float,
  val targetRadius: Float,
  val differenceHint: String
) {
  SPACE("🚀 Uzay & Gezegen", 0.72f, 0.32f, 0.22f, "Gezegen halkası ve rengi"),
  CITY("🌃 Gece Şehri & Kule", 0.45f, 0.28f, 0.20f, "Kule tepesindeki sinyal ışığı"),
  OCEAN("🏝️ Korsan Adası & Deniz", 0.25f, 0.78f, 0.20f, "Kumsaldaki deniz canlısı"),
  CAMP("🏕️ Dağ Kampı & Çadır", 0.63f, 0.68f, 0.20f, "Kamp ateşinin alev rengi"),
  CASTLE("🏰 Kraliyet Şatosu", 0.50f, 0.30f, 0.20f, "Orta kuledeki krallık bayrağı"),
  RACETRACK("🏎️ Hızlı Yarış Pisti", 0.70f, 0.65f, 0.20f, "Yarış otomobilinin arka rüzgarlığı"),
  DESERT("🏜️ Mısır Piramitleri", 0.52f, 0.48f, 0.22f, "Büyük piramidin altın zirve taşı"),
  LAB("🔬 Siber Laboratuvar", 0.76f, 0.60f, 0.20f, "Deney tüpünün parlak kimyasal sıvısı")
}

@Composable
fun FindDifferenceGame(
  remainingSeconds: Float,
  durationSeconds: Float,
  streak: Int,
  isRiskMode: Boolean,
  onBack: () -> Unit,
  onRoundSuccess: (scoreBonus: Int, timeBonus: Float) -> Unit = { _, _ -> },
  onRoundMistake: (timePenalty: Float, reason: String) -> Unit = { _, _ -> },
  onSuccess: (scoreBonus: Int, accuracy: Int) -> Unit = { _, _ -> },
  onFail: (reason: String) -> Unit = {}
) {
  var round by remember { mutableIntStateOf(1) }
  val themes = remember { SceneTheme.values() }
  var currentTheme by remember { mutableStateOf(themes[0]) }
  var feedbackText by remember { mutableStateOf("") }
  var feedbackIsSuccess by remember { mutableStateOf(true) }
  var lastTapOffset by remember { mutableStateOf<Offset?>(null) }
  var correctCount by remember { mutableIntStateOf(0) }
  var isProcessingTap by remember { mutableStateOf(false) }

  fun handleBottomPanelTap(normX: Float, normY: Float, pixelOffset: Offset) {
    if (isProcessingTap) return
    isProcessingTap = true
    lastTapOffset = pixelOffset

    val dist = hypot((normX - currentTheme.targetX).toDouble(), (normY - currentTheme.targetY).toDouble())
    if (dist <= currentTheme.targetRadius) {
      // Correct! Farkı buldu
      correctCount++
      feedbackText = "✓ FARKI BULDUN! (+120 Puan)"
      feedbackIsSuccess = true
      // No time added (0f timeBonus)
      onRoundSuccess(120, 0f)

      round++
      currentTheme = themes[(round - 1) % themes.size]
      isProcessingTap = false
    } else {
      // Wrong! Doğrudan yanlış ve diğer soruya geç
      feedbackText = "✗ YANLIŞ! Doğrudan diğer soruya geçiliyor..."
      feedbackIsSuccess = false
      // No time subtracted (0f timePenalty)
      onRoundMistake(0f, "Yanlış yere dokundun! Fark: ${currentTheme.differenceHint}")

      if (!isRiskMode) {
        round++
        currentTheme = themes[(round - 1) % themes.size]
      }
      isProcessingTap = false
    }
  }

  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    GameTopBar(
      title = "Görsel Farkı Bul",
      streak = streak,
      isRiskMode = isRiskMode,
      onBack = onBack
    )

    // Tur ve Tema Başlığı
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(Color.Black.copy(alpha = 0.55f))
          .border(1.2.dp, SkyBlueAccent, RoundedCornerShape(12.dp))
          .padding(horizontal = 12.dp, vertical = 5.dp)
      ) {
        Text(
          text = "${currentTheme.title} (Soru $round)",
          color = SkyBlueAccent,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )
      }

      if (feedbackText.isNotEmpty()) {
        Text(
          text = feedbackText,
          color = if (feedbackIsSuccess) VibrantGreen else VibrantRed,
          fontSize = 11.sp,
          fontWeight = FontWeight.Black
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Üst Panel: Orijinal Resim (Referans)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(152.dp)
        .padding(horizontal = 20.dp)
        .shadow(8.dp, RoundedCornerShape(18.dp), spotColor = Color(0x33000000))
        .clip(RoundedCornerShape(18.dp))
        .background(Color(0xFF0F172A))
        .border(2.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
    ) {
      ScenicThemeCanvas(theme = currentTheme, hasDifference = false)
      Box(
        modifier = Modifier
          .align(Alignment.TopStart)
          .padding(8.dp)
          .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(6.dp))
          .padding(horizontal = 8.dp, vertical = 3.dp)
      ) {
        Text(text = "Orijinal Resim", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Talimat
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(12.dp))
        .background(NeonOrange.copy(alpha = 0.22f))
        .border(1.5.dp, NeonOrange, RoundedCornerShape(12.dp))
        .padding(horizontal = 14.dp, vertical = 3.dp)
    ) {
      Text(
        text = com.example.util.LocalizationManager.string("find_diff_instruction"),
        color = NeonOrange,
        fontSize = 11.sp,
        fontWeight = FontWeight.Black
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Alt Panel: Farklı Olan Resim (Dokunulabilir)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(152.dp)
        .padding(horizontal = 20.dp)
        .shadow(12.dp, RoundedCornerShape(18.dp), spotColor = NeonOrange.copy(alpha = 0.4f))
        .clip(RoundedCornerShape(18.dp))
        .background(Color(0xFF0F172A))
        .border(2.5.dp, NeonOrange, RoundedCornerShape(18.dp))
        .pointerInput(currentTheme, round) {
          detectTapGestures { offset ->
            val normX = offset.x / size.width
            val normY = offset.y / size.height
            handleBottomPanelTap(normX, normY, offset)
          }
        }
    ) {
      ScenicThemeCanvas(theme = currentTheme, hasDifference = true)
      Box(
        modifier = Modifier
          .align(Alignment.TopStart)
          .padding(8.dp)
          .background(NeonOrange, RoundedCornerShape(6.dp))
          .padding(horizontal = 8.dp, vertical = 3.dp)
      ) {
        Text(text = "🔍 FARK BURADA (DOKUN)", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
      }

      // Tap indicator highlight
      lastTapOffset?.let { offset ->
        Canvas(modifier = Modifier.fillMaxSize()) {
          drawCircle(
            color = if (feedbackIsSuccess) VibrantGreen.copy(alpha = 0.8f) else VibrantRed.copy(alpha = 0.8f),
            radius = 16.dp.toPx(),
            center = offset
          )
        }
      }
    }

    Spacer(modifier = Modifier.weight(1f))

    // Alt Süre Çemberi
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 14.dp),
      contentAlignment = Alignment.Center
    ) {
      CircularTimer(
        remainingSeconds = remainingSeconds,
        totalDurationSeconds = durationSeconds,
        size = 72.dp
      )
    }
  }
}

@Composable
fun ScenicThemeCanvas(theme: SceneTheme, hasDifference: Boolean) {
  Canvas(modifier = Modifier.fillMaxSize()) {
    val w = size.width
    val h = size.height

    when (theme) {
      SceneTheme.SPACE -> {
        // Space scene
        drawRect(color = Color(0xFF030712), size = Size(w, h))

        // Stars
        drawCircle(Color.White, radius = 2.dp.toPx(), center = Offset(w * 0.12f, h * 0.20f))
        drawCircle(Color.White, radius = 3.dp.toPx(), center = Offset(w * 0.42f, h * 0.15f))
        drawCircle(Color.White, radius = 2.dp.toPx(), center = Offset(w * 0.88f, h * 0.40f))

        // Earth Planet
        drawCircle(Color(0xFF2563EB), radius = 24.dp.toPx(), center = Offset(w * 0.22f, h * 0.40f))
        drawCircle(Color(0xFF10B981), radius = 10.dp.toPx(), center = Offset(w * 0.20f, h * 0.38f))

        // Rocket
        val rocketPath = Path().apply {
          moveTo(w * 0.48f, h * 0.70f)
          lineTo(w * 0.53f, h * 0.55f)
          lineTo(w * 0.58f, h * 0.70f)
          close()
        }
        drawPath(rocketPath, color = Color(0xFFE2E8F0))

        // Difference: Saturn Ring color/beam at (0.72, 0.32)
        val saturnColor = if (hasDifference) NeonOrange else NeonGold
        drawCircle(saturnColor, radius = 20.dp.toPx(), center = Offset(w * 0.72f, h * 0.32f))
        drawOval(
          color = if (hasDifference) Color.White else Color(0xFFD97706),
          topLeft = Offset(w * 0.60f, h * 0.30f),
          size = Size(w * 0.24f, 10.dp.toPx())
        )
      }

      SceneTheme.CITY -> {
        // Night City skyline
        drawRect(color = Color(0xFF0F172A), size = Size(w, h))

        // Moon
        drawCircle(Color(0xFFFDE68A), radius = 16.dp.toPx(), center = Offset(w * 0.85f, h * 0.20f))

        // Skyscrapers
        drawRect(Color(0xFF1E293B), topLeft = Offset(w * 0.08f, h * 0.40f), size = Size(w * 0.22f, h * 0.60f))
        drawRect(Color(0xFF334155), topLeft = Offset(w * 0.35f, h * 0.28f), size = Size(w * 0.26f, h * 0.72f))
        drawRect(Color(0xFF1E293B), topLeft = Offset(w * 0.66f, h * 0.45f), size = Size(w * 0.26f, h * 0.55f))

        // Windows
        drawRect(Color(0xFFFBBF24), topLeft = Offset(w * 0.13f, h * 0.48f), size = Size(8.dp.toPx(), 8.dp.toPx()))
        drawRect(Color(0xFFFBBF24), topLeft = Offset(w * 0.18f, h * 0.60f), size = Size(8.dp.toPx(), 8.dp.toPx()))
        drawRect(Color(0xFFFBBF24), topLeft = Offset(w * 0.42f, h * 0.45f), size = Size(10.dp.toPx(), 10.dp.toPx()))

        // Difference: Radio Tower Light at (0.45, 0.28)
        val towerLightColor = if (hasDifference) Color(0xFFEF4444) else CyanBright
        drawCircle(towerLightColor, radius = 7.dp.toPx(), center = Offset(w * 0.45f, h * 0.26f))
        drawLine(
          color = Color(0xFF94A3B8),
          start = Offset(w * 0.45f, h * 0.26f),
          end = Offset(w * 0.45f, h * 0.28f),
          strokeWidth = 3.dp.toPx()
        )
      }

      SceneTheme.OCEAN -> {
        // Ocean & Island
        drawRect(color = Color(0xFF38BDF8), size = Size(w, h * 0.55f)) // Sky
        drawRect(color = Color(0xFF0284C7), topLeft = Offset(0f, h * 0.55f), size = Size(w, h * 0.45f)) // Water

        // Sun
        drawCircle(Color(0xFFF59E0B), radius = 18.dp.toPx(), center = Offset(w * 0.85f, h * 0.22f))

        // Island Sand
        drawOval(Color(0xFFFDE68A), topLeft = Offset(w * 0.05f, h * 0.65f), size = Size(w * 0.45f, h * 0.30f))

        // Palm Tree Trunk
        val trunk = Path().apply {
          moveTo(w * 0.20f, h * 0.70f)
          lineTo(w * 0.24f, h * 0.45f)
          lineTo(w * 0.28f, h * 0.70f)
          close()
        }
        drawPath(trunk, color = Color(0xFF78350F))
        drawCircle(Color(0xFF16A34A), radius = 18.dp.toPx(), center = Offset(w * 0.24f, h * 0.42f))

        // Ship
        val ship = Path().apply {
          moveTo(w * 0.65f, h * 0.65f)
          lineTo(w * 0.85f, h * 0.65f)
          lineTo(w * 0.78f, h * 0.72f)
          lineTo(w * 0.68f, h * 0.72f)
          close()
        }
        drawPath(ship, color = Color(0xFF451A03))

        // Difference: Starfish/Crab on island sand at (0.25, 0.78)
        val creatureColor = if (hasDifference) Color(0xFFEF4444) else Color(0xFFF59E0B)
        drawCircle(creatureColor, radius = 7.dp.toPx(), center = Offset(w * 0.25f, h * 0.78f))
      }

      SceneTheme.CAMP -> {
        // Mountain & Campfire
        drawRect(color = Color(0xFF1E293B), size = Size(w, h * 0.65f)) // Sky
        drawRect(color = Color(0xFF0F172A), topLeft = Offset(0f, h * 0.65f), size = Size(w, h * 0.35f)) // Ground

        // Mountains
        val mtn = Path().apply {
          moveTo(0f, h * 0.65f)
          lineTo(w * 0.30f, h * 0.30f)
          lineTo(w * 0.60f, h * 0.65f)
          close()
        }
        drawPath(mtn, color = Color(0xFF334155))

        // Pine Tree
        drawRect(Color(0xFF14532D), topLeft = Offset(w * 0.18f, h * 0.48f), size = Size(16.dp.toPx(), 36.dp.toPx()))

        // Tent
        val tent = Path().apply {
          moveTo(w * 0.35f, h * 0.75f)
          lineTo(w * 0.45f, h * 0.55f)
          lineTo(w * 0.55f, h * 0.75f)
          close()
        }
        drawPath(tent, color = Color(0xFF2563EB))

        // Difference: Campfire sparks/color at (0.63, 0.68)
        val flameColor = if (hasDifference) Color(0xFF10B981) else Color(0xFFF97316)
        val campfire = Path().apply {
          moveTo(w * 0.60f, h * 0.74f)
          lineTo(w * 0.63f, h * 0.66f)
          lineTo(w * 0.66f, h * 0.74f)
          close()
        }
        drawPath(campfire, color = flameColor)
        drawCircle(if (hasDifference) NeonGold else Color(0xFFEF4444), radius = 5.dp.toPx(), center = Offset(w * 0.63f, h * 0.68f))
      }

      SceneTheme.CASTLE -> {
        // Royal Castle & Dragon
        drawRect(color = Color(0xFF312E81), size = Size(w, h * 0.65f)) // Sky
        drawRect(color = Color(0xFF1E1B4B), topLeft = Offset(0f, h * 0.65f), size = Size(w, h * 0.35f)) // Ground

        // Castle Wall
        drawRect(Color(0xFF475569), topLeft = Offset(w * 0.20f, h * 0.45f), size = Size(w * 0.60f, h * 0.35f))

        // Left Tower
        drawRect(Color(0xFF334155), topLeft = Offset(w * 0.16f, h * 0.35f), size = Size(w * 0.14f, h * 0.45f))
        // Right Tower
        drawRect(Color(0xFF334155), topLeft = Offset(w * 0.70f, h * 0.35f), size = Size(w * 0.14f, h * 0.45f))
        // Center Tower
        drawRect(Color(0xFF1E293B), topLeft = Offset(w * 0.44f, h * 0.30f), size = Size(w * 0.12f, h * 0.50f))

        // Difference: Center tower flag at (0.50, 0.30)
        val flagColor = if (hasDifference) Color(0xFFEC4899) else NeonGold
        val flagPath = Path().apply {
          moveTo(w * 0.50f, h * 0.24f)
          lineTo(w * 0.57f, h * 0.28f)
          lineTo(w * 0.50f, h * 0.32f)
          close()
        }
        drawPath(flagPath, color = flagColor)
        drawLine(Color.White, start = Offset(w * 0.50f, h * 0.22f), end = Offset(w * 0.50f, h * 0.34f), strokeWidth = 3f)
      }

      SceneTheme.RACETRACK -> {
        // Racetrack
        drawRect(color = Color(0xFF15803D), size = Size(w, h)) // Green grass
        drawOval(Color(0xFF334155), topLeft = Offset(w * 0.08f, h * 0.25f), size = Size(w * 0.84f, h * 0.60f)) // Asphalt track
        drawOval(Color(0xFF15803D), topLeft = Offset(w * 0.22f, h * 0.38f), size = Size(w * 0.56f, h * 0.34f)) // Infield

        // Race car body
        drawRoundRect(
          color = Color(0xFFDC2626),
          topLeft = Offset(w * 0.58f, h * 0.68f),
          size = Size(w * 0.20f, h * 0.12f),
          cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
        )
        // Wheels
        drawCircle(Color.Black, radius = 5.dp.toPx(), center = Offset(w * 0.62f, h * 0.80f))
        drawCircle(Color.Black, radius = 5.dp.toPx(), center = Offset(w * 0.74f, h * 0.80f))

        // Difference: Racing Spoiler at (0.70, 0.65)
        val spoilerColor = if (hasDifference) NeonGold else Color(0xFF1E293B)
        drawRect(
          color = spoilerColor,
          topLeft = Offset(w * 0.70f, h * 0.63f),
          size = Size(w * 0.08f, 6.dp.toPx())
        )
      }

      SceneTheme.DESERT -> {
        // Egyptian Desert & Pyramids
        drawRect(color = Color(0xFFFBBF24), size = Size(w, h * 0.50f)) // Orange/Yellow Sky
        drawRect(color = Color(0xFFD97706), topLeft = Offset(0f, h * 0.50f), size = Size(w, h * 0.50f)) // Sand

        // Sun
        drawCircle(Color(0xFFEF4444), radius = 22.dp.toPx(), center = Offset(w * 0.82f, h * 0.18f))

        // Left Pyramid
        val pyr1 = Path().apply {
          moveTo(w * 0.10f, h * 0.60f)
          lineTo(w * 0.32f, h * 0.35f)
          lineTo(w * 0.54f, h * 0.60f)
          close()
        }
        drawPath(pyr1, color = Color(0xFFB45309))

        // Main Pyramid
        val pyr2 = Path().apply {
          moveTo(w * 0.34f, h * 0.65f)
          lineTo(w * 0.56f, h * 0.32f)
          lineTo(w * 0.78f, h * 0.65f)
          close()
        }
        drawPath(pyr2, color = Color(0xFF92400E))

        // Difference: Golden Capstone on Main Pyramid at (0.52, 0.48)
        val capColor = if (hasDifference) CyanBright else NeonGold
        val capPath = Path().apply {
          moveTo(w * 0.52f, h * 0.38f)
          lineTo(w * 0.56f, h * 0.32f)
          lineTo(w * 0.60f, h * 0.38f)
          close()
        }
        drawPath(capPath, color = capColor)
      }

      SceneTheme.LAB -> {
        // High-tech Sci-Fi Lab
        drawRect(color = Color(0xFF020617), size = Size(w, h))

        // Grid lines
        drawLine(Color(0xFF1E293B), start = Offset(0f, h * 0.60f), end = Offset(w, h * 0.60f), strokeWidth = 2f)
        drawLine(Color(0xFF1E293B), start = Offset(w * 0.40f, 0f), end = Offset(w * 0.40f, h), strokeWidth = 2f)

        // Lab Beaker & Flask
        val flask = Path().apply {
          moveTo(w * 0.70f, h * 0.72f)
          lineTo(w * 0.74f, h * 0.54f)
          lineTo(w * 0.78f, h * 0.54f)
          lineTo(w * 0.82f, h * 0.72f)
          close()
        }
        drawPath(flask, color = Color(0xFF64748B))

        // Difference: Beaker chemical liquid at (0.76, 0.60)
        val liquidColor = if (hasDifference) Color(0xFF10B981) else VibrantPink
        drawCircle(liquidColor, radius = 9.dp.toPx(), center = Offset(w * 0.76f, h * 0.64f))

        // Robot Head
        drawRoundRect(
          color = Color(0xFF94A3B8),
          topLeft = Offset(w * 0.18f, h * 0.45f),
          size = Size(w * 0.16f, h * 0.22f),
          cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
        )
        // Robot Eyes
        drawCircle(CyanBright, radius = 4.dp.toPx(), center = Offset(w * 0.22f, h * 0.52f))
        drawCircle(CyanBright, radius = 4.dp.toPx(), center = Offset(w * 0.30f, h * 0.52f))
      }
    }
  }
}
