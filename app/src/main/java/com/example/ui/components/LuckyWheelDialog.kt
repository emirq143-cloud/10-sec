package com.example.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.TextDark
import com.example.ui.util.SoundHapticManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class WheelReward(
  val label: String,
  val valueText: String,
  val icon: String,
  val sliceColor: Color,
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

  // Rotating light bulb glow effect around the wheel rim
  val infiniteTransition = rememberInfiniteTransition(label = "WheelGlow")
  val bulbPhase by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "BulbPhase"
  )

  val rewards = remember {
    listOf(
      WheelReward("10 Altın", "+10 🪙", "🪙", Color(0xFFD97706), 10, 0),
      WheelReward("35 XP", "+35 XP", "⚡", Color(0xFF2563EB), 0, 35),
      WheelReward("5 Altın", "+5 🪙", "🪙", Color(0xFF059669), 5, 0),
      WheelReward("Gizemli Sandık", "🎁 Sürpriz", "🎁", Color(0xFF7C3AED), 12, 25),
      WheelReward("25 Altın!", "🔥 +25", "👑", Color(0xFFDC2626), 25, 10),
      WheelReward("50 XP", "+50 XP", "⚡", Color(0xFF0284C7), 0, 50),
      WheelReward("8 Altın", "+8 🪙", "🪙", Color(0xFFEA580C), 8, 0),
      WheelReward("Elmas Kutu", "💎 Sandık", "💎", Color(0xFFDB2777), 15, 45)
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
      val extraRotations = 360f * 6 // 6 full turns for high drama
      val targetAngle = extraRotations + (360f - (winningIndex * sectorAngle) - (sectorAngle / 2))

      // Audio tick and haptic ticking simulation during spin
      val tickJob = launch {
        var tickDelay = 55L
        while (isSpinning) {
          SoundHapticManager.playWheelTick(soundEnabled)
          SoundHapticManager.vibrateClick(context, vibrationEnabled)
          delay(tickDelay)
          tickDelay = (tickDelay * 1.09f).toLong().coerceAtMost(380L)
        }
      }

      rotation.animateTo(
        targetValue = targetAngle,
        animationSpec = tween(
          durationMillis = 4200,
          easing = CubicBezierEasing(0.12f, 0.88f, 0.22f, 1f)
        )
      )

      tickJob.cancel()
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
        .clip(RoundedCornerShape(28.dp))
        .background(
          Brush.verticalGradient(
            listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF090D16))
          )
        )
        .border(2.5.dp, NeonGold, RoundedCornerShape(28.dp))
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
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0xFFFEF3C7))
                .border(1.5.dp, NeonGold, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "🎰", fontSize = 20.sp)
            }
            Column {
              Text(
                text = "GÜNLÜK ŞANS ÇARKI",
                color = NeonGold,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
              )
              Text(
                text = "Her gün 1 ücretsiz ödül çevir!",
                color = Color(0xFFCBD5E1),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }

          if (!isSpinning) {
            IconButton(
              onClick = onDismiss,
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0x33FFFFFF))
            ) {
              Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.White, modifier = Modifier.size(18.dp))
            }
          }
        }

        // Wheel Canvas with Metallic Gold Bezel, Glowing Bulbs, and Precision Needle
        Box(
          modifier = Modifier
            .size(280.dp),
          contentAlignment = Alignment.Center
        ) {
          // Canvas rendering the complete wheel
          Canvas(modifier = Modifier.size(276.dp)) {
            val center = Offset(size.width / 2, size.height / 2)
            val outerRadius = size.width / 2
            val rimThickness = 14.dp.toPx()
            val wheelRadius = outerRadius - rimThickness
            val sliceAngle = 360f / rewards.size

            // 1. Outer Metallic Golden Rim Bezel
            drawCircle(
              brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFDF00), Color(0xFFB45309), Color(0xFF78350F)),
                center = center,
                radius = outerRadius
              ),
              radius = outerRadius,
              center = center
            )

            // Inner rim shadow ring
            drawCircle(
              color = Color(0xFF1E293B),
              radius = wheelRadius + 2.dp.toPx(),
              center = center,
              style = Stroke(width = 3.dp.toPx())
            )

            // 2. Spinning Slices
            rotate(rotation.value) {
              rewards.forEachIndexed { i, item ->
                val startAngle = i * sliceAngle
                drawArc(
                  color = item.sliceColor,
                  startAngle = startAngle,
                  sweepAngle = sliceAngle,
                  useCenter = true,
                  size = Size(wheelRadius * 2, wheelRadius * 2),
                  topLeft = Offset(rimThickness, rimThickness)
                )

                // Divider line with gold gleam
                val rad = Math.toRadians(startAngle.toDouble())
                drawLine(
                  color = Color(0xFFFEF3C7),
                  start = center,
                  end = Offset(
                    center.x + (wheelRadius * cos(rad)).toFloat(),
                    center.y + (wheelRadius * sin(rad)).toFloat()
                  ),
                  strokeWidth = 2.5f
                )

                // Text/Emoji on slice
                val midAngle = startAngle + sliceAngle / 2
                val midRad = Math.toRadians(midAngle.toDouble())

                // Draw emoji icon
                val iconRadius = wheelRadius * 0.70f
                val ix = center.x + (iconRadius * cos(midRad)).toFloat()
                val iy = center.y + (iconRadius * sin(midRad)).toFloat()

                drawContext.canvas.nativeCanvas.apply {
                  val iconPaint = Paint().apply {
                    textSize = 42f
                    textAlign = Paint.Align.CENTER
                  }
                  drawText(item.icon, ix, iy + 14f, iconPaint)

                  // Draw prize text label
                  val labelRadius = wheelRadius * 0.42f
                  val lx = center.x + (labelRadius * cos(midRad)).toFloat()
                  val ly = center.y + (labelRadius * sin(midRad)).toFloat()

                  val textPaint = Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 26f
                    typeface = Typeface.DEFAULT_BOLD
                    textAlign = Paint.Align.CENTER
                    setShadowLayer(4f, 0f, 2f, android.graphics.Color.BLACK)
                  }
                  drawText(item.valueText, lx, ly + 8f, textPaint)
                }
              }

              // 3. Center Hub (Metallic Golden Boss with Star)
              drawCircle(
                brush = Brush.radialGradient(
                  colors = listOf(Color(0xFFFFFBEC), Color(0xFFF59E0B), Color(0xFF78350F)),
                  center = center,
                  radius = 34.dp.toPx()
                ),
                radius = 34.dp.toPx(),
                center = center
              )

              drawCircle(
                color = Color.White,
                radius = 26.dp.toPx(),
                center = center,
                style = Stroke(width = 2.dp.toPx())
              )

              drawCircle(
                color = Color(0xFF1E1B4B),
                radius = 22.dp.toPx(),
                center = center
              )

              drawContext.canvas.nativeCanvas.apply {
                val starPaint = Paint().apply {
                  textSize = 36f
                  textAlign = Paint.Align.CENTER
                }
                drawText("⭐", center.x, center.y + 12f, starPaint)
              }
            }

            // 4. Perimeter Light Bulbs around the golden rim
            val totalBulbs = 16
            for (b in 0 until totalBulbs) {
              val bAngle = Math.toRadians((b * (360.0 / totalBulbs)))
              val bRadius = outerRadius - (rimThickness / 2)
              val bx = center.x + (bRadius * cos(bAngle)).toFloat()
              val by = center.y + (bRadius * sin(bAngle)).toFloat()

              val isLit = (b % 2 == 0 && bulbPhase > 0.5f) || (b % 2 != 0 && bulbPhase <= 0.5f)
              val bulbColor = if (isLit) Color(0xFFFFFBEB) else Color(0xFFF59E0B).copy(alpha = 0.5f)

              drawCircle(
                color = bulbColor,
                radius = 4.5.dp.toPx(),
                center = Offset(bx, by)
              )
              if (isLit) {
                drawCircle(
                  color = Color.White,
                  radius = 2.dp.toPx(),
                  center = Offset(bx, by)
                )
              }
            }
          }

          // 5. Golden Top Pointer Needle with Ruby Jewel
          Box(
            modifier = Modifier
              .align(Alignment.TopCenter)
              .padding(top = 2.dp)
              .size(width = 34.dp, height = 38.dp)
          ) {
            Canvas(modifier = Modifier.fillMaxWidth().height(38.dp)) {
              val needlePath = Path().apply {
                moveTo(size.width / 2, size.height) // Tip pointing downward into the wheel
                lineTo(0f, 6.dp.toPx())
                lineTo(size.width, 6.dp.toPx())
                close()
              }
              // Golden needle with red ruby core
              drawPath(
                path = needlePath,
                color = Color(0xFFFFD700)
              )
              drawCircle(
                color = Color(0xFFDC2626),
                radius = 7.dp.toPx(),
                center = Offset(size.width / 2, 9.dp.toPx())
              )
              drawCircle(
                color = Color.White,
                radius = 2.5.dp.toPx(),
                center = Offset(size.width / 2 - 2f, 7.dp.toPx())
              )
            }
          }
        }

        // Won Reward Celebration Card
        AnimatedVisibility(
          visible = wonReward != null,
          enter = fadeIn() + scaleIn()
        ) {
          if (wonReward != null) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(
                  Brush.horizontalGradient(
                    listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A), Color(0xFFFEF3C7))
                  )
                )
                .border(2.dp, NeonGold, RoundedCornerShape(18.dp))
                .padding(vertical = 12.dp, horizontal = 16.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = "🎉 TEBRİKLER!",
                  color = Color(0xFFB45309),
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 1.sp
                )
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                  modifier = Modifier.padding(top = 2.dp)
                ) {
                  Text(text = wonReward!!.icon, fontSize = 22.sp)
                  Text(
                    text = "${wonReward!!.label} Kazandın!",
                    color = Color(0xFF1E1B4B),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                  )
                }
              }
            }
          }
        }

        // Action Buttons: Spin or Watch Ad for extra spin
        if (hasSpunToday) {
          Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0x33FFFFFF))
                .border(1.dp, Color(0x33CBD5E1), RoundedCornerShape(14.dp))
                .padding(vertical = 10.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "⏰ Günlük Hak Kullanıldı (Yarın 00:00'da Yenilenir)",
                color = Color(0xFFE2E8F0),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
              )
            }

            Button(
              onClick = onWatchAdForSpin,
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF59E0B),
                contentColor = Color.Black
              ),
              shape = RoundedCornerShape(18.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .shadow(8.dp, RoundedCornerShape(18.dp), spotColor = NeonGold.copy(alpha = 0.5f))
                .testTag("watch_ad_for_spin_button")
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Text(text = "🎬", fontSize = 16.sp)
                Text(
                  text = "Reklam İzle & Ekstra Hak Al (+1)",
                  fontSize = 14.sp,
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
              disabledContainerColor = Color(0xFF475569)
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .shadow(if (!isSpinning) 12.dp else 0.dp, RoundedCornerShape(20.dp), spotColor = NeonGold.copy(alpha = 0.7f))
              .testTag("spin_wheel_button")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text(text = if (isSpinning) "⏳" else "🎯", fontSize = 18.sp)
              Text(
                text = if (isSpinning) "ŞANS ÇARKI DÖNÜYOR..." else "GÜNLÜK HAKKINI ÇEVİR!",
                color = if (isSpinning) Color.White else Color(0xFF78350F),
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
              )
            }
          }
        }
      }
    }
  }
}
