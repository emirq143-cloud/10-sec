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
import androidx.compose.ui.text.style.TextOverflow
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
      .padding(horizontal = 14.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    IconButton(
      onClick = onBack,
      modifier = Modifier
        .size(40.dp)
        .clip(CircleShape)
        .background(Color.Black.copy(alpha = 0.4f))
    ) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = "Geri",
        tint = Color.White,
        modifier = Modifier.size(20.dp)
      )
    }

    Row(
      modifier = Modifier.weight(1f),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Text(
        text = title,
        color = Color.White,
        fontSize = 15.sp,
        fontWeight = FontWeight.ExtraBold,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.weight(1f, fill = false)
      )

      if (isRiskMode) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(NeonOrange)
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "3x RİSK",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1,
            softWrap = false
          )
        }
      }
    }

    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(14.dp))
        .background(Color.Black.copy(alpha = 0.45f))
        .padding(horizontal = 10.dp, vertical = 5.dp),
      contentAlignment = Alignment.Center
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
      ) {
        Text(text = "🔥", fontSize = 13.sp)
        Text(
          text = "$streak Seri",
          color = NeonGold,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          softWrap = false
        )
      }
    }
  }
}
