package com.example.ui.components

import android.graphics.Paint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CardWhite
import com.example.ui.theme.CyanBright
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextDarkSecondary
import com.example.ui.theme.VibrantGreen
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantPurple
import com.example.ui.util.SoundHapticManager
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class WheelReward(
  val label: String,
  val icon: String,
  val color: Color,
  val coins: Int,
  val xp: Int
)

@Composable
fun LuckyWheelDialog(
  soundEnabled: Boolean,
  vibrationEnabled: Boolean,
  hasSpunToday: Boolean = false,
  onRewardClaimed: (coins: Int, xp: Int) -> Unit,
  onWatchAdForSpin: () -> Unit = {},
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val rotation = remember { Animatable(0f) }
  var isSpinning by remember { mutableStateOf(false) }
  var wonReward by remember { mutableStateOf<WheelReward?>(null) }

  val rewards = remember {
    listOf(
      WheelReward("100 Altın", "🪙", Color(0xFFF59E0B), 100, 0),
      WheelReward("150 XP", "⚡", Color(0xFF3B82F6), 0, 150),
      WheelReward("250 Altın", "🪙", Color(0xFF10B981), 250, 0),
      WheelReward("Gizemli Sandık", "🎁", Color(0xFF8B5CF6), 180, 120),
      WheelReward("500 Altın!", "👑", Color(0xFFEF4444), 500, 50),
      WheelReward("300 XP", "⚡", Color(0xFF06B6D4), 0, 300),
      WheelReward("75 Altın", "🪙", Color(0xFFF97316), 75, 0),
      WheelReward("Elmas Kutu", "💎", Color(0xFFEC4899), 350, 200)
    )
  }

  fun spinWheel() {
    if (isSpinning) return
    isSpinning = true
    wonReward = null

    scope.launch {
      SoundHapticManager.vibrateClick(context, vibrationEnabled)
      val winningIndex = Random.nextInt(rewards.size)
      val sectorAngle = 360f / rewards.size
      val targetAngle = (360f * 5) + (360f - (winningIndex * sectorAngle) - (sectorAngle / 2))

      rotation.animateTo(
        targetValue = targetAngle,
        animationSpec = tween(
          durationMillis = 3800,
          easing = CubicBezierEasing(0.15f, 0.85f, 0.25f, 1f)
        )
      )

      val reward = rewards[winningIndex]
      wonReward = reward
      isSpinning = false
      SoundHapticManager.playFanfare(soundEnabled)
      SoundHapticManager.vibrateSuccess(context, vibrationEnabled)
      onRewardClaimed(reward.coins, reward.xp)
    }
  }

  Dialog(onDismissRequest = { if (!isSpinning) onDismiss() }) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(26.dp))
        .background(CardWhite)
        .border(2.5.dp, NeonGold, RoundedCornerShape(26.dp))
        .padding(20.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(text = "🎰", fontSize = 24.sp)
            Column {
              Text(
                text = "Günlük Şans Çarkı",
                color = TextDark,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
              )
              Text(
                text = "Her gün ücretsiz bir çevirme hakkı!",
                color = TextDarkSecondary,
                fontSize = 11.sp
              )
            }
          }

          if (!isSpinning) {
            IconButton(onClick = onDismiss) {
              Icon(Icons.Default.Close, contentDescription = "Kapat", tint = TextDarkSecondary)
            }
          }
        }

        // Wheel Canvas Container with Top Arrow Pointer
        Box(
          modifier = Modifier
            .size(260.dp),
          contentAlignment = Alignment.Center
        ) {
          // Spinning Wheel
          Canvas(modifier = Modifier.size(250.dp)) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2
            val sliceAngle = 360f / rewards.size

            rotate(rotation.value) {
              rewards.forEachIndexed { i, item ->
                val startAngle = i * sliceAngle
                drawArc(
                  color = item.color,
                  startAngle = startAngle,
                  sweepAngle = sliceAngle,
                  useCenter = true,
                  size = Size(radius * 2, radius * 2),
                  topLeft = Offset(0f, 0f)
                )

                // Divider line
                val rad = Math.toRadians(startAngle.toDouble())
                drawLine(
                  color = Color.White.copy(alpha = 0.6f),
                  start = center,
                  end = Offset(
                    center.x + (radius * cos(rad)).toFloat(),
                    center.y + (radius * sin(rad)).toFloat()
                  ),
                  strokeWidth = 2f
                )

                // Text/Emoji on slice
                val textAngle = Math.toRadians((startAngle + sliceAngle / 2).toDouble())
                val textRadius = radius * 0.68f
                val tx = center.x + (textRadius * cos(textAngle)).toFloat()
                val ty = center.y + (textRadius * sin(textAngle)).toFloat()

                drawContext.canvas.nativeCanvas.apply {
                  val paint = Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 28f
                    isFakeBoldText = true
                    textAlign = Paint.Align.CENTER
                  }
                  drawText(item.icon, tx, ty + 10f, paint)
                }
              }

              // Center Hub
              drawCircle(
                color = Color.White,
                radius = 28f,
                center = center
              )
              drawCircle(
                color = BluePrimary,
                radius = 20f,
                center = center
              )
            }
          }

          // Fixed Pointer Indicator at Top
          Canvas(
            modifier = Modifier
              .align(Alignment.TopCenter)
              .size(28.dp)
          ) {
            val path = Path().apply {
              moveTo(size.width / 2, size.height)
              lineTo(0f, 0f)
              lineTo(size.width, 0f)
              close()
            }
            drawPath(path, color = Color(0xFFEF4444))
          }
        }

        // Won Reward Banner
        if (wonReward != null) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(Color(0xFFFEF3C7))
              .border(2.dp, NeonGold, RoundedCornerShape(16.dp))
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "🎉 TEBRİKLER!",
                color = Color(0xFFB45309),
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
              )
              Text(
                text = "${wonReward!!.icon} ${wonReward!!.label} Kazandın!",
                color = TextDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        // Spin Button or Daily limit state
        if (hasSpunToday) {
          Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFF1F5F9))
                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(14.dp))
                .padding(vertical = 10.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "⏰ Günlük Hak Kullanıldı (Yarın 00:00'da Yenilenir)",
                color = TextDarkSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Button(
              onClick = onWatchAdForSpin,
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
              shape = RoundedCornerShape(20.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = NeonGold.copy(alpha = 0.5f))
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Text(text = "🎬", fontSize = 16.sp)
                Text(
                  text = "Reklam İzle & Ekstra Hak Al (+1)",
                  color = Color.Black,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Black
                )
              }
            }
          }
        } else {
          Button(
            onClick = { spinWheel() },
            enabled = !isSpinning,
            colors = ButtonDefaults.buttonColors(
              containerColor = NeonGold,
              disabledContainerColor = Color(0xFFE2E8F0)
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .shadow(if (!isSpinning) 10.dp else 0.dp, RoundedCornerShape(20.dp), spotColor = NeonGold.copy(alpha = 0.6f))
          ) {
            Text(
              text = if (isSpinning) "ÇARK DÖNÜYOR..." else "GÜNLÜK HAKKINI KULLAN: ÇEVİR! 🎯",
              color = if (isSpinning) Color(0xFF94A3B8) else Color(0xFF78350F),
              fontSize = 15.sp,
              fontWeight = FontWeight.Black
            )
          }
        }
      }
    }
  }
}
