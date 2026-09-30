package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.NeonGold
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextDarkMuted

@Composable
fun AdBannerCard(
  onRemoveAdsClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(58.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(Color.White.copy(alpha = 0.92f))
      .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(16.dp))
      .padding(horizontal = 12.dp, vertical = 6.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color(0xFFEFF6FF)),
          contentAlignment = Alignment.Center
        ) {
          Text(text = "📢", fontSize = 18.sp)
        }

        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFF59E0B))
                .padding(horizontal = 5.dp, vertical = 1.dp)
            ) {
              Text(
                text = "REKLAM",
                color = Color.Black,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black
              )
            }
            Text(
              text = "Google AdMob Banner",
              color = TextDark,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }
          Text(
            text = "Play Store onayında gerçek reklamlar yayınlanır",
            color = TextDarkMuted,
            fontSize = 10.sp
          )
        }
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xFFEFF6FF))
          .border(1.dp, BluePrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
          .clickable { onRemoveAdsClick() }
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text(
          text = "VIP ✨",
          color = BluePrimary,
          fontSize = 11.sp,
          fontWeight = FontWeight.ExtraBold
        )
      }
    }
  }
}
