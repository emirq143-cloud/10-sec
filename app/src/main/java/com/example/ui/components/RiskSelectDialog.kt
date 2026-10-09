package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.GameType
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.CardWhite
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.OrangeLight
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextDarkSecondary
import com.example.util.LocalizationManager

@Composable
fun RiskSelectDialog(
  gameType: GameType,
  onSelectMode: (isRisk: Boolean) -> Unit,
  onDismiss: () -> Unit
) {
  val currentLang by LocalizationManager.currentLanguage.collectAsStateWithLifecycle()

  Dialog(onDismissRequest = onDismiss) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .shadow(16.dp, RoundedCornerShape(26.dp), spotColor = Color(0x33000000))
        .clip(RoundedCornerShape(26.dp))
        .background(CardWhite)
        .border(1.dp, CardBorderLight, RoundedCornerShape(26.dp))
        .padding(24.dp)
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Text(
          text = gameType.title,
          color = TextDark,
          fontSize = 22.sp,
          fontWeight = FontWeight.ExtraBold
        )

        Text(
          text = gameType.subtitle,
          color = TextDarkSecondary,
          fontSize = 13.sp,
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Safe Option Card
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFEFF6FF))
            .border(1.5.dp, SkyBlueAccent, RoundedCornerShape(18.dp))
            .clickable { onSelectMode(false) }
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🛡️", fontSize = 18.sp)
                Spacer(modifier = Modifier.padding(3.dp))
                Text(
                  text = LocalizationManager.string("safe_mode"),
                  color = TextDark,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold
                )
              }
              Text(
                text = LocalizationManager.string("safe_mode_desc"),
                color = TextDarkSecondary,
                fontSize = 12.sp
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(BluePrimary)
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                text = "+${gameType.baseExp} XP",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }
          }
        }

        // Risk Option Card (3x Rewards!)
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(OrangeLight)
            .border(1.5.dp, NeonOrange, RoundedCornerShape(18.dp))
            .clickable { onSelectMode(true) }
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🔥", fontSize = 18.sp)
                Spacer(modifier = Modifier.padding(3.dp))
                Text(
                  text = LocalizationManager.string("risk_mode"),
                  color = Color(0xFFC2410C),
                  fontSize = 16.sp,
                  fontWeight = FontWeight.ExtraBold
                )
              }
              Text(
                text = LocalizationManager.string("risk_mode_desc"),
                color = NeonOrange,
                fontSize = 12.sp
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(NeonOrange)
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                text = "+${gameType.baseExp * 3} XP",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp
              )
            }
          }
        }
      }
    }
  }
}
