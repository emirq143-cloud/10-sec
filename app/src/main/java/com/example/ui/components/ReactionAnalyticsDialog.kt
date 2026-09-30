package com.example.ui.components

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
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.UserProfile
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CardWhite
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextDarkSecondary
import com.example.ui.theme.VibrantGreen

@Composable
fun ReactionAnalyticsDialog(
  userProfile: UserProfile,
  onPlayReflex: () -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(26.dp))
        .background(CardWhite)
        .border(2.dp, SkyBlueAccent, RoundedCornerShape(26.dp))
        .padding(22.dp)
    ) {
      Column(
        modifier = Modifier.fillMaxWidth(),
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
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0xFFEFF6FF)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Speed,
                contentDescription = null,
                tint = BluePrimary,
                modifier = Modifier.size(24.dp)
              )
            }
            Column {
              Text(
                text = "Refleks & Tepki Analizi",
                color = TextDark,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold
              )
              Text(
                text = "Milisaniye (ms) hassasiyetinde ölçüm",
                color = TextDarkMuted,
                fontSize = 11.sp
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Kapat", tint = TextDarkSecondary)
          }
        }

        // Main Stat Gauge Card
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
              Brush.horizontalGradient(
                listOf(Color(0xFF0284C7), Color(0xFF0369A1))
              )
            )
            .padding(18.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(
                text = "Ortalama Tepki Süren",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
              )
              Row(verticalAlignment = Alignment.Bottom) {
                Text(
                  text = "${userProfile.avgReactionTimeMs}",
                  color = Color.White,
                  fontSize = 38.sp,
                  fontWeight = FontWeight.Black
                )
                Text(
                  text = " ms",
                  color = SkyBlueAccent,
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(bottom = 6.dp)
                )
              }
            }

            Column(horizontalAlignment = Alignment.End) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color.White.copy(alpha = 0.2f))
                  .padding(horizontal = 10.dp, vertical = 4.dp)
              ) {
                Text(
                  text = "En Hızlı: ${userProfile.bestReactionTimeMs} ms",
                  color = Color.White,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "⚡ %${userProfile.reactionPercentile} Daha Hızlı",
                color = NeonGold,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold
              )
            }
          }
        }

        // Percentile Badge
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFECFDF5))
            .border(1.5.dp, VibrantGreen, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(text = "🎯", fontSize = 18.sp)
            Text(
              text = "Türkiye'deki oyuncuların %${userProfile.reactionPercentile}'inden daha hızlı tepki veriyorsun!",
              color = Color(0xFF065F46),
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              lineHeight = 16.sp
            )
          }
        }

        // Trend Bar Chart
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = "Son Oyunlar Tepki Trendi",
            color = TextDark,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )

          Canvas(
            modifier = Modifier
              .fillMaxWidth()
              .height(65.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFFF8FAFC))
              .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
              .padding(horizontal = 16.dp, vertical = 8.dp)
          ) {
            val list = userProfile.reactionHistory
            if (list.isEmpty()) return@Canvas
            val barWidth = 24.dp.toPx()
            val spacing = (size.width - (list.size * barWidth)) / (list.size + 1)
            val maxMs = 380f
            val minMs = 180f

            list.forEachIndexed { i, ms ->
              val normalized = ((maxMs - ms) / (maxMs - minMs)).coerceIn(0.2f, 1f)
              val barH = size.height * normalized
              val x = spacing + i * (barWidth + spacing)
              val y = size.height - barH

              drawRoundRect(
                color = if (i == list.lastIndex) BluePrimary else SkyBlueAccent.copy(alpha = 0.6f),
                topLeft = Offset(x, y),
                size = Size(barWidth, barH),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
              )
            }
          }
        }

        // Action Button to Play Reflex
        Button(
          onClick = {
            onDismiss()
            onPlayReflex()
          },
          colors = ButtonDefaults.buttonColors(containerColor = NeonOrange),
          shape = RoundedCornerShape(18.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
        ) {
          Text(
            text = "Refleksini Test Et & Hızlan! ⚡",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold
          )
        }
      }
    }
  }
}
