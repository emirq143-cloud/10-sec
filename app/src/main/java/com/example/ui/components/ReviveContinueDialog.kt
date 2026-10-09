package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonOrange
import kotlinx.coroutines.delay

/**
 * Revive / Continue countdown dialog shown immediately when game time expires or on mistake.
 * Gives user a 5-second countdown to watch a Rewarded Ad (ca-app-pub-4020568333948380/7841422376)
 * and revive with +5 extra seconds to continue playing!
 */
@Composable
fun ReviveContinueDialog(
  reason: String = "Süre Doldu!",
  currentScore: Int,
  comboStreak: Int,
  onWatchAdRevive: () -> Unit,
  onSkipAndEnd: () -> Unit
) {
  var countdownSeconds by remember { mutableIntStateOf(5) }
  val progress = remember { Animatable(1f) }

  // Heart pulse animation
  val infiniteTransition = rememberInfiniteTransition(label = "heartPulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.18f,
    animationSpec = infiniteRepeatable(
      animation = tween(450),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulseScale"
  )

  LaunchedEffect(Unit) {
    progress.animateTo(
      targetValue = 0f,
      animationSpec = tween(durationMillis = 5000, easing = LinearEasing)
    )
  }

  LaunchedEffect(Unit) {
    while (countdownSeconds > 0) {
      delay(1000)
      countdownSeconds--
    }
    // Time expired without clicking revive ad
    onSkipAndEnd()
  }

  Dialog(
    onDismissRequest = { onSkipAndEnd() },
    properties = DialogProperties(
      dismissOnBackPress = true,
      dismissOnClickOutside = false,
      usePlatformDefaultWidth = false
    )
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.85f))
        .padding(24.dp),
      contentAlignment = Alignment.Center
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .shadow(24.dp, RoundedCornerShape(28.dp), spotColor = Color(0xFFEF4444))
          .clip(RoundedCornerShape(28.dp))
          .background(
            Brush.verticalGradient(
              listOf(
                Color(0xFF1E1B4B),
                Color(0xFF0F172A),
                Color(0xFF030712)
              )
            )
          )
          .border(
            width = 2.dp,
            brush = Brush.linearGradient(
              listOf(Color(0xFFEF4444), Color(0xFFF59E0B), Color(0xFF10B981))
            ),
            shape = RoundedCornerShape(28.dp)
          )
          .padding(24.dp)
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          // Circular Countdown Timer with Pulsing Heart
          Box(
            modifier = Modifier.size(110.dp),
            contentAlignment = Alignment.Center
          ) {
            CircularProgressIndicator(
              progress = { progress.value },
              modifier = Modifier.fillMaxSize(),
              color = if (countdownSeconds <= 2) Color(0xFFEF4444) else Color(0xFF10B981),
              trackColor = Color.White.copy(alpha = 0.15f),
              strokeWidth = 7.dp
            )

            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Text(
                text = "❤️",
                fontSize = 32.sp,
                modifier = Modifier.scale(pulseScale)
              )
              Text(
                text = "$countdownSeconds",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
              )
            }
          }

          // Title & Reason
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(
              text = com.example.util.LocalizationManager.string("revive_title"),
              color = Color.White,
              fontSize = 22.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 0.5.sp
            )
            Text(
              text = reason,
              color = Color(0xFFFCA5A5),
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold
            )
          }

          // Stats preserved badge
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(Color.White.copy(alpha = 0.07f))
              .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
              .padding(vertical = 10.dp, horizontal = 16.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceAround,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = com.example.util.LocalizationManager.string("score_label"), color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(text = "$currentScore", color = NeonGold, fontSize = 18.sp, fontWeight = FontWeight.Black)
              }
              Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.White.copy(alpha = 0.2f)))
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = com.example.util.LocalizationManager.string("streak_label"), color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(text = "$comboStreak 🔥", color = NeonOrange, fontSize = 18.sp, fontWeight = FontWeight.Black)
              }
            }
          }

          // Primary CTA: Watch Rewarded Ad to Continue
          Button(
            onClick = onWatchAdRevive,
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF10B981)
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(56.dp)
              .shadow(12.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFF10B981).copy(alpha = 0.7f))
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Text(text = "🎬", fontSize = 22.sp)
              Column(horizontalAlignment = Alignment.Start) {
                Text(
                  text = com.example.util.LocalizationManager.string("revive_btn"),
                  color = Color.White,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Black
                )
                Text(
                  text = com.example.util.LocalizationManager.string("revive_desc"),
                  color = Color(0xFFD1FAE5),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          // Skip / Decline Button
          TextButton(
            onClick = onSkipAndEnd,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "${com.example.util.LocalizationManager.string("skip_and_finish")} ($countdownSeconds s)",
              color = Color.White.copy(alpha = 0.65f),
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              textAlign = TextAlign.Center
            )
          }
        }
      }
    }
  }
}
