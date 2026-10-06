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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayCircle
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
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.VibrantGreen
import kotlinx.coroutines.delay

enum class AdRewardType(val title: String, val rewardDescription: String) {
  DOUBLE_REWARD("2X Skor & XP Katlama", "Bu turdaki skorun ve kazandığın XP ikiye katlandı!"),
  SECOND_CHANCE("İkinci Şans & Can", "Ekstra 5 saniye süre ile tura devam ediyorsun!"),
  EXTRA_SPIN("Şans Çarkı Ekstra Hak", "Çarkı bir kez daha çevirme hakkı kazandın!"),
  BONUS_COINS("100 Altın Hediyesi", "Hesabına anında 100 altın tanımlandı!")
}

/**
 * Realistic Google AdMob Test Ad Dialog for rewarded ads.
 * Emulates official Google Mobile Ads SDK behavior for Play Store review testing.
 */
@Composable
fun AdSimulationDialog(
  rewardType: AdRewardType,
  onRewardGranted: () -> Unit,
  onDismiss: () -> Unit
) {
  var secondsLeft by remember { mutableIntStateOf(5) }
  var isCompleted by remember { mutableStateOf(false) }
  val progress = remember { Animatable(0f) }

  LaunchedEffect(Unit) {
    progress.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 5000, easing = LinearEasing)
    )
  }

  LaunchedEffect(Unit) {
    while (secondsLeft > 0) {
      delay(1000)
      secondsLeft--
    }
    isCompleted = true
    onRewardGranted()
  }

  Dialog(
    onDismissRequest = {
      if (isCompleted) onDismiss()
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
        // Ad Header with AdMob tag
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
                text = "REKLAM",
                color = Color.Black,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black
              )
            }
            Text(
              text = "Google AdMob Test Reklamı",
              color = Color.White.copy(alpha = 0.8f),
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
          }

          if (isCompleted) {
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
                text = "Ödül: ${secondsLeft}s",
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

        // Ad Creative Card (Simulating game sponsor / Play Store game promotion)
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
              Brush.verticalGradient(
                listOf(
                  Color(0xFF2563EB),
                  Color(0xFF1D4ED8),
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
                .size(54.dp)
                .shadow(12.dp, CircleShape, spotColor = SkyBlueAccent)
                .background(Color.White.copy(alpha = 0.2f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "🎮", fontSize = 28.sp)
            }
            Text(
              text = "Time Rush: Zeka & Hız",
              color = Color.White,
              fontSize = 18.sp,
              fontWeight = FontWeight.Black
            )
            Text(
              text = "Google Play Store Hazır Sürümü",
              color = SkyBlueAccent,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
            Text(
              text = "Zihnini hızlandır, arkadaşlarınla yarış!",
              color = Color.White.copy(alpha = 0.8f),
              fontSize = 11.sp,
              textAlign = TextAlign.Center
            )
          }
        }

        // Reward Status Announcement
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isCompleted) Color(0xFF065F46) else Color(0xFF1E293B))
            .border(
              1.dp,
              if (isCompleted) VibrantGreen else Color(0xFF475569),
              RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(
              imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.PlayCircle,
              contentDescription = null,
              tint = if (isCompleted) VibrantGreen else NeonGold,
              modifier = Modifier.size(28.dp)
            )
            Column {
              Text(
                text = if (isCompleted) "🎉 Ödül Verildi!" else "🎬 Reklam İzleniyor...",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = rewardType.rewardDescription,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp
              )
            }
          }
        }

        // Action button
        Button(
          onClick = {
            if (isCompleted) onDismiss()
          },
          enabled = isCompleted,
          colors = ButtonDefaults.buttonColors(
            containerColor = VibrantGreen,
            disabledContainerColor = Color(0xFF334155)
          ),
          shape = RoundedCornerShape(18.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
        ) {
          Text(
            text = if (isCompleted) "Ödülü Al ve Devam Et ✨" else "Reklam Bitiyor (${secondsLeft}s)...",
            color = if (isCompleted) Color.White else Color(0xFF94A3B8),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}
