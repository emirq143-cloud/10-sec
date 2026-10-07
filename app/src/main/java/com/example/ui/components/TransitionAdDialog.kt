package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ads.AdManager
import com.example.ui.theme.NeonGold
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.VibrantGreen
import kotlinx.coroutines.delay

@Composable
fun TransitionAdDialog(
  onDismiss: () -> Unit
) {
  var secondsLeft by remember { mutableIntStateOf(3) }
  var canSkip by remember { mutableStateOf(false) }
  val progress = remember { Animatable(0f) }

  LaunchedEffect(Unit) {
    progress.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
    )
  }

  LaunchedEffect(Unit) {
    while (secondsLeft > 0) {
      delay(1000)
      secondsLeft--
    }
    canSkip = true
  }

  Dialog(
    onDismissRequest = {
      if (canSkip) onDismiss()
    },
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF0F172A))
        .padding(20.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(26.dp))
          .background(Color(0xFF1E293B))
          .border(2.dp, SkyBlueAccent.copy(alpha = 0.5f), RoundedCornerShape(26.dp))
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFF59E0B))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "GEÇİŞ REKLAMI",
                color = Color.Black,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black
              )
            }
            Text(
              text = "Google AdMob",
              color = Color.White.copy(alpha = 0.8f),
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
          }

          if (canSkip) {
            IconButton(
              onClick = onDismiss,
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f))
            ) {
              Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.White)
            }
          } else {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black.copy(alpha = 0.4f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(
                text = "${secondsLeft}s sonra atla",
                color = NeonGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        // Progress bar
        LinearProgressIndicator(
          progress = { progress.value },
          modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp)),
          color = VibrantGreen,
          trackColor = Color(0xFF334155)
        )

        // Ad Banner Creative
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
              Brush.verticalGradient(
                listOf(
                  Color(0xFF6366F1),
                  Color(0xFF4338CA),
                  Color(0xFF0F172A)
                )
              )
            )
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            .padding(16.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(50.dp)
                .shadow(12.dp, CircleShape, spotColor = SkyBlueAccent)
                .background(Color.White.copy(alpha = 0.2f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "⚡", fontSize = 26.sp)
            }
            Text(
              text = "Time Rush: Rekorları Kır!",
              color = Color.White,
              fontSize = 17.sp,
              fontWeight = FontWeight.Black
            )
            Text(
              text = "2 Oyun Sonu Otomatik Geçiş Reklamı",
              color = SkyBlueAccent,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
            Text(
              text = "Ad Unit ID: ${AdManager.REWARDED_INTERSTITIAL_AD_UNIT_ID}",
              color = Color.White.copy(alpha = 0.6f),
              fontSize = 10.sp,
              textAlign = TextAlign.Center
            )
          }
        }

        // Explanation text
        Text(
          text = "AdMob Ödüllü Geçiş Reklamı (2 oyunda bir gösterim aktifleştirildi).",
          color = Color.White.copy(alpha = 0.7f),
          fontSize = 11.sp,
          textAlign = TextAlign.Center
        )

        // Dismiss button
        Button(
          onClick = {
            if (canSkip) onDismiss()
          },
          enabled = canSkip,
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF3B82F6),
            disabledContainerColor = Color(0xFF334155)
          ),
          shape = RoundedCornerShape(18.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
        ) {
          Text(
            text = if (canSkip) "Reklamı Kapat ve Oyuna Dön ⏩" else "Lütfen Bekleyin (${secondsLeft}s)...",
            color = if (canSkip) Color.White else Color(0xFF94A3B8),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}
