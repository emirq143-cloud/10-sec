package com.example.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.GamePlaySession
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.CardWhite
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextDarkSecondary

@Composable
fun GameResultScreen(
  session: GamePlaySession,
  onContinue: () -> Unit,
  onPlayAgain: () -> Unit,
  onWatchAdDouble: () -> Unit = {},
  onWatchAdSecondChance: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    // Twilight mountain background
    Image(
      painter = painterResource(id = R.drawable.twilight_mountain_bg_1790506194088),
      contentDescription = null,
      modifier = Modifier.fillMaxSize(),
      contentScale = ContentScale.Crop
    )

    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.45f))
    )

    Column(
      modifier = Modifier
        .fillMaxWidth(0.90f)
        .shadow(20.dp, RoundedCornerShape(28.dp), spotColor = Color(0x66000000))
        .clip(RoundedCornerShape(28.dp))
        .background(CardWhite)
        .border(1.dp, CardBorderLight, RoundedCornerShape(28.dp))
        .padding(26.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Trophy Emblem
      Box(
        modifier = Modifier
          .size(88.dp)
          .shadow(16.dp, CircleShape, spotColor = if (session.isVictory) NeonGold.copy(alpha = 0.5f) else Color(0x33EF4444))
          .background(
            if (session.isVictory)
              Brush.radialGradient(listOf(Color(0xFFFDE68A), Color(0xFFF59E0B)))
            else
              Brush.radialGradient(listOf(Color(0xFFFECACA), Color(0xFFEF4444))),
            CircleShape
          ),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = if (session.isVictory) "🏆" else "⏰",
          fontSize = 42.sp
        )
      }

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = if (session.isVictory) "Harika!" else "Süre Doldu!",
          color = TextDark,
          fontSize = 28.sp,
          fontWeight = FontWeight.Black
        )
        Text(
          text = if (session.isVictory) "Bu turu başarıyla kazandın!" else session.feedbackText,
          color = TextDarkSecondary,
          fontSize = 14.sp,
          textAlign = TextAlign.Center
        )
      }

      // Stats Cards (Skor & XP)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFEFF6FF))
            .border(1.dp, SkyBlueAccent.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(14.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Skorun", color = TextDarkSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "+${session.currentScore}",
              color = BluePrimary,
              fontSize = 22.sp,
              fontWeight = FontWeight.Black
            )
          }
        }

        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFEF3C7))
            .border(1.dp, NeonGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(14.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "XP Kazanımı", color = Color(0xFFB45309), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "+${session.xpGained} XP",
              color = Color(0xFFD97706),
              fontSize = 22.sp,
              fontWeight = FontWeight.Black
            )
          }
        }
      }

      if (session.isRiskMode) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFEDD5))
            .border(1.dp, NeonOrange.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "🔥 3X RİSK ÖDÜLÜ AKTİF EDİLDİ!",
            color = NeonOrange,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold
          )
        }
      }

      // Monetization / Rewarded Ad Button (Direct match to user request for Play Store Ads)
      if (session.isVictory) {
        Button(
          onClick = onWatchAdDouble,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
          shape = RoundedCornerShape(20.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = NeonGold.copy(alpha = 0.5f))
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(text = "🎬", fontSize = 18.sp)
            Column(horizontalAlignment = Alignment.Start) {
              Text(
                text = "Reklam İzle & 2X Skor Kazan!",
                color = Color.Black,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black
              )
              Text(
                text = "+${session.currentScore} Ek Puan & +50 Altın",
                color = Color(0xFF78350F),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      } else {
        Button(
          onClick = onWatchAdSecondChance,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
          shape = RoundedCornerShape(20.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color(0x6610B981))
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(text = "❤️", fontSize = 18.sp)
            Text(
              text = "Reklam İzle & İkinci Şans (+5s) Al!",
              color = Color.White,
              fontSize = 13.sp,
              fontWeight = FontWeight.Black
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      // Action Buttons
      Button(
        onClick = onContinue,
        colors = ButtonDefaults.buttonColors(containerColor = SkyBlueAccent),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = SkyBlueAccent.copy(alpha = 0.4f))
      ) {
        Text(
          text = "Devam Et",
          color = Color.Black,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold
        )
      }

      OutlinedButton(
        onClick = onPlayAgain,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDark),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
      ) {
        Text(
          text = "Tekrar Oyna",
          fontSize = 15.sp,
          fontWeight = FontWeight.SemiBold
        )
      }
    }
  }
}
