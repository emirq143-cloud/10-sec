package com.example.ui.screens.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CircularTimer
import com.example.ui.components.GameTopBar
import com.example.ui.theme.CardWhite
import com.example.ui.theme.CyanBright
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.TextWhiteTranslucent

@Composable
fun FindDifferenceGame(
  remainingSeconds: Float,
  durationSeconds: Float,
  streak: Int,
  isRiskMode: Boolean,
  onBack: () -> Unit,
  onSuccess: (scoreBonus: Int, accuracy: Int) -> Unit,
  onFail: (reason: String) -> Unit
) {
  var isDifferenceFound by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    GameTopBar(
      title = "Farkı Bul",
      streak = streak,
      isRiskMode = isRiskMode,
      onBack = onBack
    )

    Text(
      text = "İki görsel neredeyse aynı. Alttaki gizli farka dokun!",
      color = TextWhiteTranslucent,
      fontSize = 13.sp
    )

    Spacer(modifier = Modifier.height(14.dp))

    // Top Panel: Original (Framed in crisp white border)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(170.dp)
        .padding(horizontal = 24.dp)
        .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color(0x33000000))
        .clip(RoundedCornerShape(20.dp))
        .background(Color(0xFF131E36))
        .border(2.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
    ) {
      ScenicArtCanvas(hasDifference = false)
      Box(
        modifier = Modifier
          .align(Alignment.TopStart)
          .padding(8.dp)
          .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
          .padding(horizontal = 8.dp, vertical = 3.dp)
      ) {
        Text(text = "Orijinal", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Clear indicator
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(14.dp))
        .background(Color.Black.copy(alpha = 0.6f))
        .border(1.5.dp, NeonOrange, RoundedCornerShape(14.dp))
        .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
      Text(
        text = "👇 ALTTALİ GÖRSELDEKİ FARKA DOKUN! 👇",
        color = NeonOrange,
        fontSize = 12.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.sp
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Bottom Panel: Modified with interactive hotspot!
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(170.dp)
        .padding(horizontal = 24.dp)
        .shadow(14.dp, RoundedCornerShape(20.dp), spotColor = NeonOrange.copy(alpha = 0.5f))
        .clip(RoundedCornerShape(20.dp))
        .background(Color(0xFF131E36))
        .border(3.dp, NeonOrange, RoundedCornerShape(20.dp))
        .clickable {
          if (!isDifferenceFound) {
            isDifferenceFound = true
            onSuccess(120, 100)
          }
        }
    ) {
      ScenicArtCanvas(hasDifference = true)
      Box(
        modifier = Modifier
          .align(Alignment.TopStart)
          .padding(8.dp)
          .background(NeonOrange, RoundedCornerShape(6.dp))
          .padding(horizontal = 8.dp, vertical = 3.dp)
      ) {
        Text(text = "🔍 FARK BURADA! (DOKUN)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
      }
    }

    Spacer(modifier = Modifier.weight(1f))

    // Bottom Timer
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 20.dp),
      contentAlignment = Alignment.Center
    ) {
      CircularTimer(
        remainingSeconds = remainingSeconds,
        totalDurationSeconds = durationSeconds,
        size = 78.dp
      )
    }
  }
}

@Composable
fun ScenicArtCanvas(hasDifference: Boolean) {
  Canvas(modifier = Modifier.fillMaxSize()) {
    val w = size.width
    val h = size.height

    drawRect(
      color = Color(0xFF1E284A),
      size = Size(w, h * 0.65f)
    )

    // Sun / Moon
    drawCircle(
      color = if (hasDifference) NeonOrange else NeonGold,
      radius = 24.dp.toPx(),
      center = Offset(w * 0.75f, h * 0.28f)
    )

    val mountainPath = Path().apply {
      moveTo(0f, h * 0.65f)
      lineTo(w * 0.25f, h * 0.38f)
      lineTo(w * 0.5f, h * 0.55f)
      lineTo(w * 0.75f, h * 0.32f)
      lineTo(w, h * 0.65f)
      close()
    }
    drawPath(mountainPath, color = Color(0xFF0F172A))

    drawRect(
      color = Color(0xFF0B1224),
      topLeft = Offset(0f, h * 0.65f),
      size = Size(w, h * 0.35f)
    )

    drawRect(
      color = Color(0xFF1E293B),
      topLeft = Offset(w * 0.42f, h * 0.40f),
      size = Size(28.dp.toPx(), h * 0.25f)
    )
    val towerRoof = Path().apply {
      moveTo(w * 0.42f - 4.dp.toPx(), h * 0.40f)
      lineTo(w * 0.42f + 14.dp.toPx(), h * 0.28f)
      lineTo(w * 0.42f + 32.dp.toPx(), h * 0.40f)
      close()
    }
    drawPath(towerRoof, color = Color(0xFF334155))

    val boatX = if (hasDifference) w * 0.25f else w * 0.20f
    val boatPath = Path().apply {
      moveTo(boatX, h * 0.82f)
      lineTo(boatX + 44.dp.toPx(), h * 0.82f)
      lineTo(boatX + 36.dp.toPx(), h * 0.88f)
      lineTo(boatX + 8.dp.toPx(), h * 0.88f)
      close()
    }
    drawPath(boatPath, color = if (hasDifference) CyanBright else Color.White)

    drawCircle(color = Color.White.copy(alpha = 0.8f), radius = 3.dp.toPx(), center = Offset(w * 0.22f, h * 0.25f))
    drawCircle(color = Color.White.copy(alpha = 0.8f), radius = 3.dp.toPx(), center = Offset(w * 0.28f, h * 0.20f))
    if (hasDifference) {
      drawCircle(color = NeonGold, radius = 4.dp.toPx(), center = Offset(w * 0.35f, h * 0.18f))
    }
  }
}
