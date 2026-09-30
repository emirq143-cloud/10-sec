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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.TextWhiteTranslucent
import com.example.ui.theme.VibrantGreen
import java.util.Locale
import kotlin.math.abs

@Composable
fun EstimationBarGame(
  remainingSeconds: Float,
  durationSeconds: Float,
  streak: Int,
  isRiskMode: Boolean,
  onBack: () -> Unit,
  onSuccess: (scoreBonus: Int, accuracy: Int) -> Unit,
  onFail: (reason: String) -> Unit
) {
  var isStopped by remember { mutableStateOf(false) }
  var frozenPercent by remember { mutableFloatStateOf(0f) }

  val transition = rememberInfiniteTransition(label = "slider")
  val animatedValue by transition.animateFloat(
    initialValue = 0f,
    targetValue = 100f,
    animationSpec = infiniteRepeatable(
      animation = tween(if (isRiskMode) 850 else 1250, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "barPosition"
  )

  val displayPercent = if (isStopped) frozenPercent else animatedValue

  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    GameTopBar(
      title = "Tahmin",
      streak = streak,
      isRiskMode = isRiskMode,
      onBack = onBack
    )

    Text(
      text = "Çubuğu %50 noktasında durdur!",
      color = Color.White,
      fontSize = 15.sp,
      fontWeight = FontWeight.Bold
    )
    Text(
      text = "Ne kadar yakınsan o kadar puan.",
      color = TextWhiteTranslucent,
      fontSize = 12.sp
    )

    Spacer(modifier = Modifier.height(20.dp))

    // Vertical Meter with %50 badge on right (Matching screenshot Column 9)
    Row(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth(),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .width(76.dp)
          .fillMaxHeight(0.85f)
          .clip(RoundedCornerShape(38.dp))
          .background(Color.Black.copy(alpha = 0.4f))
          .border(2.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(38.dp))
          .padding(8.dp),
        contentAlignment = Alignment.Center
      ) {
        // Target 50% line
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(Color.White.copy(alpha = 0.8f))
        )

        val fraction = displayPercent / 100f
        Box(
          modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
          contentAlignment = Alignment.TopCenter
        ) {
          Box(
            modifier = Modifier
              .fillMaxHeight(fraction)
              .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .shadow(10.dp, RoundedCornerShape(8.dp), spotColor = SkyBlueAccent)
                .clip(RoundedCornerShape(8.dp))
                .background(SkyBlueAccent)
            )
          }
        }
      }

      Spacer(modifier = Modifier.width(16.dp))

      // %50 target zone badge
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(14.dp))
          .background(Color(0xFF0284C7))
          .border(2.dp, Color.White, RoundedCornerShape(14.dp))
          .padding(horizontal = 14.dp, vertical = 10.dp)
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "HEDEF",
            color = SkyBlueAccent,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold
          )
          Text(
            text = "%50",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Massive Glowing Stop Action Button
    Button(
      onClick = {
        if (!isStopped) {
          isStopped = true
          frozenPercent = animatedValue
          val diff = abs(frozenPercent - 50f)
          if (diff <= 12f) {
            val accuracy = (100 - (diff * 4f)).toInt().coerceIn(60, 100)
            val bonus = ((15f - diff) * 10).toInt().coerceAtLeast(30)
            onSuccess(bonus, accuracy)
          } else {
            onFail("Çok uzak kaldın! %${frozenPercent.toInt()} noktasında durdu (Hedef %50)")
          }
        }
      },
      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
      shape = RoundedCornerShape(24.dp),
      modifier = Modifier
        .fillMaxWidth(0.72f)
        .height(60.dp)
        .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFFEF4444).copy(alpha = 0.7f))
        .border(3.dp, Color.White, RoundedCornerShape(24.dp))
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(text = "🛑", fontSize = 24.sp)
        Text(
          text = if (isStopped) "DURDURULDU!" else "BURAYA BAS: DURDUR!",
          color = Color.White,
          fontSize = 18.sp,
          fontWeight = FontWeight.Black
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 20.dp),
      contentAlignment = Alignment.Center
    ) {
      CircularTimer(
        remainingSeconds = remainingSeconds,
        totalDurationSeconds = durationSeconds,
        size = 76.dp
      )
    }
  }
}
