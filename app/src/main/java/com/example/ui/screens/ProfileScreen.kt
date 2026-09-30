package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AvatarItem
import com.example.data.model.UserProfile
import com.example.ui.components.AppBackgroundEffect
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.CardFrosted
import com.example.ui.theme.CardWhite
import com.example.ui.theme.LightBgGradientBottom
import com.example.ui.theme.LightBgGradientTop
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.OrangeLight
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextDarkSecondary
import com.example.ui.theme.VibrantGreen
import com.example.ui.theme.VibrantPink

import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.IconButton

@Composable
fun ProfileScreen(
  userProfile: UserProfile,
  avatars: List<AvatarItem>,
  onSelectAvatar: (String) -> Unit,
  onOpenSettings: () -> Unit = {},
  onOpenReactionAnalytics: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  var avatarFilter by remember { mutableStateOf("Tümü") }

  val filteredAvatars = remember(avatarFilter, avatars) {
    when (avatarFilter) {
      "Açılanlar" -> avatars.filter { it.isUnlocked }
      "Kilitli" -> avatars.filter { !it.isUnlocked }
      else -> avatars
    }
  }

  AppBackgroundEffect(modifier = modifier) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 18.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(10.dp))

        // User Overview Card
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = BluePrimary.copy(alpha = 0.35f))
            .clip(RoundedCornerShape(24.dp))
            .background(CardFrosted)
            .border(2.dp, SkyBlueAccent, RoundedCornerShape(24.dp))
            .padding(18.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              val selectedAvatar = avatars.find { it.id == userProfile.selectedAvatarId }
              Box(
                modifier = Modifier
                  .size(62.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFEFF6FF))
                  .border(2.dp, SkyBlueAccent, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Text(text = selectedAvatar?.emoji ?: "🧑‍🚀", fontSize = 32.sp)
              }

              Column {
                Text(
                  text = userProfile.name,
                  color = TextDark,
                  fontSize = 20.sp,
                  fontWeight = FontWeight.ExtraBold
                )
                Text(
                  text = "${userProfile.league.displayName} • #${userProfile.nationalRank}",
                  color = BluePrimary,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.SemiBold
                )
                Text(
                  text = "Seviye ${userProfile.level} • ${userProfile.coins} Altın",
                  color = TextDarkSecondary,
                  fontSize = 12.sp
                )
              }
            }

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(14.dp))
                  .background(OrangeLight)
                  .border(1.dp, NeonOrange.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                  .padding(horizontal = 10.dp, vertical = 8.dp)
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(text = "🔥 ${userProfile.maxStreak}", color = NeonOrange, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                  Text(text = "Seri", color = TextDarkSecondary, fontSize = 10.sp)
                }
              }

              IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFF1F5F9))
                  .border(1.dp, Color(0xFFCBD5E1), CircleShape)
              ) {
                Icon(
                  imageVector = Icons.Default.Settings,
                  contentDescription = "Ayarlar",
                  tint = TextDarkSecondary,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }
        }
      }

      // Beyin Performansı / Beyin Yaşı Analiz Kartı
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color(0x22000000))
            .clip(RoundedCornerShape(24.dp))
            .background(CardFrosted)
            .border(2.dp, Color(0xFFCBD5E1), RoundedCornerShape(24.dp))
            .padding(18.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Text(text = "🧠", fontSize = 20.sp)
                Text(
                  text = "Beyin Performansı",
                  color = TextDark,
                  fontSize = 17.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE0F2FE))
                    .clickable { onOpenReactionAnalytics() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(
                    text = "⚡ ${userProfile.avgReactionTimeMs} ms",
                    color = BluePrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold
                  )
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFEF3C7))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(
                    text = "${userProfile.overallBrainScore}/100",
                    color = Color(0xFFB45309),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold
                  )
                }
              }
            }

            BrainMetricBar(label = "Hafıza", score = userProfile.brainMemoryScore, color = VibrantPink)
            BrainMetricBar(label = "Refleks", score = userProfile.brainReflexScore, color = NeonOrange)
            BrainMetricBar(label = "Dikkat", score = userProfile.brainAttentionScore, color = VibrantGreen)
            BrainMetricBar(label = "Mantık", score = userProfile.brainLogicScore, color = BluePrimary)
          }
        }
      }

      // Karakterler / Avatarlar Bölümü (Matching column 12 in mockup)
      item {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Karakterler",
              color = TextDark,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold
            )

            // Filter Tabs: Tümü, Açılanlar, Kilitli
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              listOf("Tümü", "Açılanlar", "Kilitli").forEach { filter ->
                val isSelected = avatarFilter == filter
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) BluePrimary else Color(0xFFE2E8F0))
                    .clickable { avatarFilter = filter }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                  Text(
                    text = filter,
                    color = if (isSelected) Color.White else TextDarkSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                  )
                }
              }
            }
          }

          // Avatar Row List (Clearly identifiable selectable cards)
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(filteredAvatars.size) { index ->
              val avatar = filteredAvatars[index]
              val isSelected = avatar.id == userProfile.selectedAvatarId

              Box(
                modifier = Modifier
                  .size(98.dp)
                  .shadow(
                    elevation = if (isSelected) 10.dp else 4.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = if (isSelected) NeonGold.copy(alpha = 0.5f) else Color(0x1A000000)
                  )
                  .clip(RoundedCornerShape(20.dp))
                  .background(if (isSelected) Color(0xFFFFFBEB) else CardWhite)
                  .border(
                    width = if (isSelected) 3.dp else if (avatar.isUnlocked) 2.dp else 1.dp,
                    color = if (isSelected) NeonGold else if (avatar.isUnlocked) BluePrimary else Color(0xFFCBD5E1),
                    shape = RoundedCornerShape(20.dp)
                  )
                  .clickable(enabled = avatar.isUnlocked) {
                    onSelectAvatar(avatar.id)
                  }
                  .padding(8.dp),
                contentAlignment = Alignment.Center
              ) {
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center
                ) {
                  Text(text = avatar.emoji, fontSize = 30.sp)
                  Spacer(modifier = Modifier.height(3.dp))
                  Text(
                    text = avatar.name,
                    color = if (avatar.isUnlocked) TextDark else TextDarkMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                  )

                  if (isSelected) {
                    Text(
                      text = "AKTİF",
                      color = Color(0xFFB45309),
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Black
                    )
                  } else if (avatar.isUnlocked) {
                    Text(
                      text = "SEÇ ➔",
                      color = BluePrimary,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold
                    )
                  } else {
                    Text(
                      text = "Lv ${avatar.unlockLevel}",
                      color = NeonOrange,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }

                if (isSelected) {
                  Box(
                    modifier = Modifier
                      .align(Alignment.TopEnd)
                      .size(20.dp)
                      .clip(CircleShape)
                      .background(NeonGold),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.Check,
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(14.dp)
                    )
                  }
                } else if (!avatar.isUnlocked) {
                  Box(
                    modifier = Modifier
                      .align(Alignment.TopEnd)
                      .size(18.dp)
                      .clip(CircleShape)
                      .background(Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.Lock,
                      contentDescription = null,
                      tint = TextDarkMuted,
                      modifier = Modifier.size(12.dp)
                    )
                  }
                }
              }
            }
          }
        }
        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}

@Composable
fun BrainMetricBar(label: String, score: Int, color: Color) {
  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(text = label, color = TextDarkSecondary, fontSize = 13.sp)
      Text(
        text = "$score / 100",
        color = TextDark,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
      )
    }

    LinearProgressIndicator(
      progress = { (score / 100f).coerceIn(0f, 1f) },
      color = color,
      trackColor = Color(0xFFF1F5F9),
      modifier = Modifier
        .fillMaxWidth()
        .height(8.dp)
        .clip(RoundedCornerShape(4.dp))
    )
  }
}
