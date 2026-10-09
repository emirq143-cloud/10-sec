package com.example.ui.screens.games

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CircularTimer
import com.example.ui.components.GameTopBar
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CardWhite
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.TextDark
import com.example.ui.theme.VibrantGreen
import com.example.ui.theme.VibrantRed
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random

val TARGET_PERCENTAGES = listOf(50, 30, 75, 40, 65, 80, 25, 60)

@Composable
fun EstimationBarGame(
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
  var targetPercent by remember { mutableIntStateOf(50) }
  var isStopped by remember { mutableStateOf(false) }
  var frozenPercent by remember { mutableFloatStateOf(0f) }
  var feedbackText by remember { mutableStateOf("") }
  var feedbackIsSuccess by remember { mutableStateOf(true) }

  val transition = rememberInfiniteTransition(label = "slider")
  val speed = if (isRiskMode) 900 else 1200
  val animatedValue by transition.animateFloat(
    initialValue = 0f,
    targetValue = 100f,
    animationSpec = infiniteRepeatable(
      animation = tween(speed, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "barPosition"
  )

  val displayPercent = if (isStopped) frozenPercent else animatedValue

  fun stopBar() {
    if (isStopped) return
    isStopped = true
    frozenPercent = animatedValue
    val diff = abs(frozenPercent - targetPercent)

    if (diff <= 8f) {
      feedbackText = "🎯 MÜKEMMEL! (%${frozenPercent.toInt()}) +140"
      feedbackIsSuccess = true
      onRoundSuccess(140, 2.5f)
    } else if (diff <= 15f) {
      feedbackText = "👍 İYİ! (%${frozenPercent.toInt()}) +80"
      feedbackIsSuccess = true
      onRoundSuccess(80, 1.5f)
    } else {
      feedbackText = "✗ ÇOK UZAK (%${frozenPercent.toInt()})"
      feedbackIsSuccess = false
      onRoundMistake(1.8f, "Hedef %$targetPercent idi, sen %${frozenPercent.toInt()} yaptın")
    }
  }

  // Next round auto-advance after showing feedback
  LaunchedEffect(isStopped) {
    if (isStopped) {
      delay(900)
      round++
      targetPercent = TARGET_PERCENTAGES.random()
      isStopped = false
    }
  }

  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    GameTopBar(
      title = "Hassas Tahmin",
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
          text = "Tur $round",
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

    Spacer(modifier = Modifier.height(14.dp))

    // Hedef Talimat Kartı
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(Color.Black.copy(alpha = 0.6f))
        .border(2.dp, NeonGold, RoundedCornerShape(16.dp))
        .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
      Text(
        text = "🎯 ÇUBUĞU %$targetPercent SEVİYESİNDE DURDUR!",
        color = NeonGold,
        fontSize = 14.sp,
        fontWeight = FontWeight.Black
      )
    }

    Spacer(modifier = Modifier.height(30.dp))

    // Dikey İlerleme Çubuğu ve Gösterge
    Row(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 40.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      // Yüzde ölçek çizgileri
      Column(
        modifier = Modifier.height(280.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.End
      ) {
        Text(text = "%100", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(text = "%75", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(text = "%50", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(text = "%25", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(text = "%0", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }

      Spacer(modifier = Modifier.width(16.dp))

      // Ana Çubuk
      Box(
        modifier = Modifier
          .width(52.dp)
          .height(280.dp)
          .shadow(16.dp, RoundedCornerShape(26.dp), spotColor = SkyBlueAccent)
          .clip(RoundedCornerShape(26.dp))
          .background(CardWhite)
          .border(2.5.dp, BluePrimary, RoundedCornerShape(26.dp))
      ) {
        // Hedef Bölge Çizgisi
        val targetFraction = (100 - targetPercent) / 100f
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .align(Alignment.TopCenter)
            .padding(top = (280.dp * targetFraction) - 3.dp)
            .background(NeonOrange)
        )

        // Dolgu Alanı (Aşağıdan Yukarıya)
        val fillHeight = (displayPercent / 100f) * 280
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(fillHeight.dp)
            .align(Alignment.BottomCenter)
            .background(
              if (isStopped) {
                if (abs(frozenPercent - targetPercent) <= 8f) VibrantGreen else VibrantRed
              } else {
                BluePrimary
              }
            )
        )
      }

      Spacer(modifier = Modifier.width(16.dp))

      // Hedef Etiketi
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(NeonOrange)
          .border(1.5.dp, Color.White, RoundedCornerShape(12.dp))
          .padding(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Text(
          text = "%$targetPercent HEDEF",
          color = Color.White,
          fontSize = 11.sp,
          fontWeight = FontWeight.Black
        )
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Büyük Durdurma Butonu
    Button(
      onClick = { stopBar() },
      enabled = !isStopped,
      colors = ButtonDefaults.buttonColors(
        containerColor = NeonGold,
        disabledContainerColor = Color(0xFF94A3B8)
      ),
      shape = RoundedCornerShape(24.dp),
      modifier = Modifier
        .fillMaxWidth(0.80f)
        .height(60.dp)
        .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = NeonGold.copy(alpha = 0.6f))
    ) {
      Text(
        text = if (isStopped) com.example.util.LocalizationManager.string("stopped") else "${com.example.util.LocalizationManager.string("press_stop")}! 🛑",
        color = Color.Black,
        fontSize = 17.sp,
        fontWeight = FontWeight.Black
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

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
