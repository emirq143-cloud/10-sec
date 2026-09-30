package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonOrange

@Composable
fun GameTopBar(
  title: String,
  streak: Int,
  isRiskMode: Boolean,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    IconButton(
      onClick = onBack,
      modifier = Modifier
        .size(42.dp)
        .clip(CircleShape)
        .background(Color.Black.copy(alpha = 0.35f))
    ) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = "Geri",
        tint = Color.White
      )
    }

    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Text(
        text = title,
        color = Color.White,
        fontSize = 19.sp,
        fontWeight = FontWeight.ExtraBold
      )

      if (isRiskMode) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(NeonOrange)
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = "3x RİSK",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black
          )
        }
      }
    }

    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(Color.Black.copy(alpha = 0.35f))
        .padding(horizontal = 12.dp, vertical = 6.dp),
      contentAlignment = Alignment.Center
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Text(text = "🔥", fontSize = 14.sp)
        Text(
          text = "$streak Seri",
          color = NeonGold,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}
