package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.SkyBlueAccent

@Composable
fun StartScreen(
  onStartGame: () -> Unit,
  onOpenLeaderboard: () -> Unit,
  onOpenProfile: () -> Unit,
  onOpenPrivacyPolicy: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  Box(modifier = modifier.fillMaxSize()) {
    // Hero Background Artwork (Twilight mountain cliff sunrise)
    Image(
      painter = painterResource(id = R.drawable.game_hero_bg_1790504981925),
      contentDescription = "Game Landscape",
      modifier = Modifier.fillMaxSize(),
      contentScale = ContentScale.Crop
    )

    // Vibrant gradient overlay matching Column 1 of screenshot
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            listOf(
              Color(0x881E293B),
              Color(0x221E293B),
              Color(0xCC0F172A)
            )
          )
        )
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp, vertical = 36.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top 10 SEC Logo & Stopwatch
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 40.dp)
      ) {
        Box(
          modifier = Modifier
            .size(76.dp)
            .shadow(20.dp, CircleShape, spotColor = SkyBlueAccent)
            .background(Color.White.copy(alpha = 0.2f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(text = "⏱️", fontSize = 38.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "10 SEC",
          color = Color.White,
          fontSize = 48.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "10 saniyede ne kadar iyisin?",
          color = SkyBlueAccent,
          fontSize = 17.sp,
          fontWeight = FontWeight.SemiBold
        )

        Text(
          text = "Refleks • Zeka • Dikkat • Hafıza",
          color = Color.White.copy(alpha = 0.8f),
          fontSize = 13.sp
        )
      }

      // Middle / Bottom Actions: Big bright "OYUNA BAŞLA" pill button
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        Button(
          onClick = onStartGame,
          colors = ButtonDefaults.buttonColors(containerColor = SkyBlueAccent),
          shape = RoundedCornerShape(26.dp),
          modifier = Modifier
            .fillMaxWidth(0.85f)
            .height(58.dp)
            .shadow(16.dp, RoundedCornerShape(26.dp), spotColor = SkyBlueAccent.copy(alpha = 0.6f))
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = "Başla",
              tint = Color.Black,
              modifier = Modifier.size(28.dp)
            )
            Text(
              text = "OYUNA BAŞLA",
              color = Color.Black,
              fontSize = 18.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 1.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Quick bottom shortcuts: Rekorlar, Ligler, Ayarlar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly
        ) {
          StartNavButton(
            icon = "🏆",
            label = "Liderlik",
            onClick = onOpenLeaderboard
          )
          StartNavButton(
            icon = "🛡️",
            label = "Ligler",
            onClick = onOpenLeaderboard
          )
          StartNavButton(
            icon = "👤",
            label = "Profil",
            onClick = onOpenProfile
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Privacy Policy link (Google Play Store compliance)
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onOpenPrivacyPolicy() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = "🔒 Gizlilik Politikası & Play Store Beyanı",
            color = Color.White.copy(alpha = 0.75f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }
  }
}

@Composable
private fun StartNavButton(
  icon: String,
  label: String,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .padding(8.dp)
  ) {
    Text(text = icon, fontSize = 22.sp)
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = label,
      color = Color.White.copy(alpha = 0.9f),
      fontSize = 12.sp,
      fontWeight = FontWeight.SemiBold
    )
  }
}
