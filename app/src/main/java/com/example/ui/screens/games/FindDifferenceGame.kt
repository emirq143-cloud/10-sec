package com.example.ui.screens.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.CyanBright
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.VibrantGreen
import com.example.ui.theme.VibrantRed
import kotlin.math.hypot

enum class DifferenceType {
  MOON_COLOR,     // Top right (0.75, 0.28)
  BOAT_POSITION,  // Bottom water (0.35, 0.85)
  TOWER_ROOF,     // Center tower (0.46, 0.35)
  EXTRA_STAR      // Top left (0.25, 0.20)
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
  val diffTypes = remember { DifferenceType.values() }
  var currentDiff by remember { mutableStateOf(diffTypes[(round - 1) % diffTypes.size]) }
  var feedbackText by remember { mutableStateOf("") }
  var feedbackIsSuccess by remember { mutableStateOf(true) }

  fun handleBottomPanelTap(normalizedX: Float, normalizedY: Float) {
    // Check if tap hit the current difference target area
    val (targetX, targetY, radius) = when (currentDiff) {
      DifferenceType.MOON_COLOR -> Triple(0.75f, 0.28f, 0.18f)
      DifferenceType.BOAT_POSITION -> Triple(0.35f, 0.85f, 0.20f)
      DifferenceType.TOWER_ROOF -> Triple(0.46f, 0.35f, 0.18f)
      DifferenceType.EXTRA_STAR -> Triple(0.25f, 0.20f, 0.18f)
    }

    val dist = hypot((normalizedX - targetX).toDouble(), (normalizedY - targetY).toDouble())
    if (dist <= radius) {
      feedbackText = "✓ FARKI BULDUN! (+140)"
      feedbackIsSuccess = true
      onRoundSuccess(140, 3.0f)
      round++
      currentDiff = diffTypes[(round - 1) % diffTypes.size]
    } else {
      feedbackText = "✗ BURADA FARK YOK!"
      feedbackIsSuccess = false
      onRoundMistake(1.5f, "Yanlış yere dokundun!")
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

    // Tur ve Geri Bildirim
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(Color.Black.copy(alpha = 0.5f))
          .border(1.dp, SkyBlueAccent, RoundedCornerShape(12.dp))
          .padding(horizontal = 12.dp, vertical = 4.dp)
      ) {
        Text(
          text = "Görsel $round",
          color = SkyBlueAccent,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )
      }

      if (feedbackText.isNotEmpty()) {
        Text(
          text = feedbackText,
          color = if (feedbackIsSuccess) VibrantGreen else VibrantRed,
          fontSize = 12.sp,
          fontWeight = FontWeight.Black
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Üst Panel: Orijinal
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(160.dp)
        .padding(horizontal = 22.dp)
        .shadow(8.dp, RoundedCornerShape(18.dp), spotColor = Color(0x33000000))
        .clip(RoundedCornerShape(18.dp))
        .background(Color(0xFF131E36))
        .border(2.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
    ) {
      ScenicArtCanvas(hasDifference = false, diffType = currentDiff)
      Box(
        modifier = Modifier
          .align(Alignment.TopStart)
          .padding(8.dp)
          .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
          .padding(horizontal = 8.dp, vertical = 3.dp)
      ) {
        Text(text = "Orijinal Resim", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Talimat
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(14.dp))
        .background(NeonOrange.copy(alpha = 0.2f))
        .border(1.5.dp, NeonOrange, RoundedCornerShape(14.dp))
        .padding(horizontal = 14.dp, vertical = 4.dp)
    ) {
      Text(
        text = "👇 ALTTALİ RESİMDEKİ FARKA DOKUN! 👇",
        color = NeonOrange,
        fontSize = 12.sp,
        fontWeight = FontWeight.Black
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Alt Panel: Farklı Olan Resim (Dokunulabilir)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(160.dp)
        .padding(horizontal = 22.dp)
        .shadow(12.dp, RoundedCornerShape(18.dp), spotColor = NeonOrange.copy(alpha = 0.4f))
        .clip(RoundedCornerShape(18.dp))
        .background(Color(0xFF131E36))
        .border(2.5.dp, NeonOrange, RoundedCornerShape(18.dp))
        .pointerInput(currentDiff) {
          detectTapGestures { offset ->
            val normX = offset.x / size.width
            val normY = offset.y / size.height
            handleBottomPanelTap(normX, normY)
          }
        }
    ) {
      ScenicArtCanvas(hasDifference = true, diffType = currentDiff)
      Box(
        modifier = Modifier
          .align(Alignment.TopStart)
          .padding(8.dp)
          .background(NeonOrange, RoundedCornerShape(6.dp))
          .padding(horizontal = 8.dp, vertical = 3.dp)
      ) {
        Text(text = "🔍 FARK BURADA (DOKUN)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
      }
    }

    Spacer(modifier = Modifier.weight(1f))

    // Alt Süre
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp),
      contentAlignment = Alignment.Center
    ) {
      CircularTimer(
        remainingSeconds = remainingSeconds,
        totalDurationSeconds = durationSeconds,
        size = 74.dp
      )
    }
  }
}

@Composable
fun ScenicArtCanvas(hasDifference: Boolean, diffType: DifferenceType) {
  Canvas(modifier = Modifier.fillMaxSize()) {
    val w = size.width
    val h = size.height

    // Sky
    drawRect(color = Color(0xFF1E284A), size = Size(w, h * 0.65f))

    // Moon / Sun
    val moonColor = if (hasDifference && diffType == DifferenceType.MOON_COLOR) NeonOrange else NeonGold
    drawCircle(
      color = moonColor,
      radius = 22.dp.toPx(),
      center = Offset(w * 0.75f, h * 0.28f)
    )

    // Mountains
    val mountainPath = Path().apply {
      moveTo(0f, h * 0.65f)
      lineTo(w * 0.25f, h * 0.38f)
      lineTo(w * 0.5f, h * 0.55f)
      lineTo(w * 0.75f, h * 0.32f)
      lineTo(w, h * 0.65f)
      close()
    }
    drawPath(mountainPath, color = Color(0xFF0F172A))

    // Water
    drawRect(
      color = Color(0xFF0B1224),
      topLeft = Offset(0f, h * 0.65f),
      size = Size(w, h * 0.35f)
    )

    // Center Tower
    drawRect(
      color = Color(0xFF1E293B),
      topLeft = Offset(w * 0.42f, h * 0.40f),
      size = Size(28.dp.toPx(), h * 0.25f)
    )

    // Tower Roof
    val roofColor = if (hasDifference && diffType == DifferenceType.TOWER_ROOF) NeonOrange else Color(0xFF334155)
    val towerRoof = Path().apply {
      moveTo(w * 0.42f - 4.dp.toPx(), h * 0.40f)
      lineTo(w * 0.42f + 14.dp.toPx(), h * 0.26f)
      lineTo(w * 0.42f + 32.dp.toPx(), h * 0.40f)
      close()
    }
    drawPath(towerRoof, color = roofColor)

    // Boat on water
    val boatX = if (hasDifference && diffType == DifferenceType.BOAT_POSITION) w * 0.35f else w * 0.20f
    val boatColor = if (hasDifference && diffType == DifferenceType.BOAT_POSITION) CyanBright else Color.White
    val boatPath = Path().apply {
      moveTo(boatX, h * 0.82f)
      lineTo(boatX + 44.dp.toPx(), h * 0.82f)
      lineTo(boatX + 36.dp.toPx(), h * 0.88f)
      lineTo(boatX + 8.dp.toPx(), h * 0.88f)
      close()
    }
    drawPath(boatPath, color = boatColor)

    // Stars
    drawCircle(color = Color.White.copy(alpha = 0.8f), radius = 3.dp.toPx(), center = Offset(w * 0.22f, h * 0.25f))
    drawCircle(color = Color.White.copy(alpha = 0.8f), radius = 3.dp.toPx(), center = Offset(w * 0.28f, h * 0.20f))

    if (hasDifference && diffType == DifferenceType.EXTRA_STAR) {
      drawCircle(color = NeonGold, radius = 5.dp.toPx(), center = Offset(w * 0.38f, h * 0.16f))
    }
  }
}
