package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.CardWhite
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.OrangeLight
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextDarkSecondary

@Composable
fun StreakCelebrationDialog(
  streak: Int,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .shadow(16.dp, RoundedCornerShape(26.dp), spotColor = Color(0x33000000))
        .clip(RoundedCornerShape(26.dp))
        .background(CardWhite)
        .border(1.5.dp, NeonOrange, RoundedCornerShape(26.dp))
        .padding(24.dp)
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Text(text = "🔥", fontSize = 56.sp)

        Text(
          text = "$streak Seri!",
          color = NeonOrange,
          fontSize = 28.sp,
          fontWeight = FontWeight.ExtraBold
        )

        Text(
          text = "Harika gidiyorsun! Ardı ardına galibiyet serisi yakaladın.",
          color = TextDarkSecondary,
          fontSize = 14.sp
        )

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(OrangeLight)
            .border(1.dp, NeonOrange.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .padding(14.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "⚡ +500 XP BONUS KAZANILDI",
            color = Color(0xFFC2410C),
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(containerColor = SkyBlueAccent),
          shape = RoundedCornerShape(18.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
        ) {
          Text(
            text = "Tamam",
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}
