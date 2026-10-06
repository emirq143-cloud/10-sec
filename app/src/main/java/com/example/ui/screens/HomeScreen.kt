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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.IconButton
import com.example.data.model.DailyMission
import com.example.data.model.GameCategory
import com.example.data.model.GameType
import com.example.data.model.UserProfile
import com.example.ui.components.AppBackgroundEffect
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.CardFrosted
import com.example.ui.theme.CardWhite
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GreenLight
import com.example.ui.theme.LightBgGradientBottom
import com.example.ui.theme.LightBgGradientTop
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.OrangeLight
import com.example.ui.theme.PinkLight
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextDarkSecondary
import com.example.ui.theme.VibrantGreen
import com.example.ui.theme.VibrantPink

@Composable
fun HomeScreen(
  userProfile: UserProfile,
  dailyMissions: List<DailyMission>,
  onQuickPlay: () -> Unit,
  onSelectGame: (GameType) -> Unit,
  onOpenLeaderboard: () -> Unit,
  onOpenGames: () -> Unit,
  onClaimMission: (String) -> Unit,
  onOpenLuckyWheel: () -> Unit = {},
  onOpenReactionAnalytics: () -> Unit = {},
  onOpenSettings: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  AppBackgroundEffect(modifier = modifier) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 18.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(10.dp))

        // Top Profile & Gold Coin Bar (Framed in sleek frosted glass)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardFrosted)
            .border(1.5.dp, SkyBlueAccent.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(50.dp)
                .shadow(8.dp, CircleShape, spotColor = BluePrimary.copy(alpha = 0.4f))
                .clip(CircleShape)
                .background(Color(0xFFEFF6FF))
                .border(2.5.dp, BluePrimary, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "🧑‍🚀", fontSize = 26.sp)
            }

            Column {
              Text(
                text = userProfile.name,
                color = TextDark,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold
              )
              Text(
                text = "Seviye ${userProfile.level}",
                color = BluePrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )

              Spacer(modifier = Modifier.height(3.dp))

              // XP Progress Bar
              LinearProgressIndicator(
                progress = { (userProfile.currentXp.toFloat() / userProfile.maxXp).coerceIn(0f, 1f) },
                modifier = Modifier
                  .width(105.dp)
                  .height(6.dp)
                  .clip(RoundedCornerShape(3.dp)),
                color = BluePrimary,
                trackColor = Color(0xFFE2E8F0)
              )
            }
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Coins Badge
            Box(
              modifier = Modifier
                .shadow(4.dp, RoundedCornerShape(18.dp), spotColor = NeonGold.copy(alpha = 0.4f))
                .clip(RoundedCornerShape(18.dp))
                .background(GoldLight)
                .border(1.5.dp, NeonGold, RoundedCornerShape(18.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Text(text = "🪙", fontSize = 14.sp)
                Text(
                  text = "${userProfile.coins}",
                  color = Color(0xFFB45309),
                  fontSize = 13.sp,
                  fontWeight = FontWeight.ExtraBold
                )
              }
            }

            // Settings Button
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

      // Feature 4 & 5: Lucky Wheel & Reaction Time Quick Action Cards
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Lucky Wheel Card
          Box(
            modifier = Modifier
              .weight(1f)
              .shadow(10.dp, RoundedCornerShape(20.dp), spotColor = NeonGold.copy(alpha = 0.5f))
              .clip(RoundedCornerShape(20.dp))
              .background(
                Brush.linearGradient(
                  listOf(Color(0xFFFFFBEB), Color(0xFFFEF3C7))
                )
              )
              .border(2.dp, NeonGold, RoundedCornerShape(20.dp))
              .clickable { onOpenLuckyWheel() }
              .padding(14.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(text = "🎯 Günlük Çark", color = Color(0xFF78350F), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                Box(
                  modifier = Modifier
                    .clip(CircleShape)
                    .background(if (!userProfile.hasSpunWheelToday) Color(0xFFDC2626) else Color(0xFF16A34A))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = if (!userProfile.hasSpunWheelToday) "HAZIR" else "✓",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black
                  )
                }
              }
              Text(
                text = if (!userProfile.hasSpunWheelToday) "Hediye altın & XP seni bekliyor!" else "Bugün çevrildi (Reklamla +1)",
                color = Color(0xFF92400E),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = if (!userProfile.hasSpunWheelToday) "ŞİMDİ ÇEVİR ➔" else "DETAY ➔",
                color = Color(0xFFB45309),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
              )
            }
          }

          // Reaction Analytics Card
          Box(
            modifier = Modifier
              .weight(1f)
              .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = SkyBlueAccent.copy(alpha = 0.4f))
              .clip(RoundedCornerShape(20.dp))
              .background(CardFrosted)
              .border(2.dp, Color(0xFF0284C7), RoundedCornerShape(20.dp))
              .clickable { onOpenReactionAnalytics() }
              .padding(14.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(text = "⚡ Tepki Hızın", color = TextDark, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = "⏱️", fontSize = 18.sp)
              }
              Text(text = "${userProfile.avgReactionTimeMs} ms (%${userProfile.reactionPercentile} hızlı)", color = BluePrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "ANALİZİ GÖR ➔",
                color = Color(0xFF0284C7),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
              )
            }
          }
        }
      }

      // Günlük Görevler Card (Frosted White with distinct border)
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
                Text(text = "🎯", fontSize = 18.sp)
                Text(
                  text = "Günlük Görevler",
                  color = TextDark,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold
                )
              }
              Text(text = "🎁", fontSize = 24.sp)
            }

            dailyMissions.forEach { mission ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (mission.isCompleted) GreenLight.copy(alpha = 0.4f) else Color(0xFFF8FAFC))
                  .border(0.5.dp, if (mission.isCompleted) VibrantGreen.copy(alpha = 0.3f) else Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                  .clickable {
                    if (mission.isCompleted && !mission.isClaimed) {
                      onClaimMission(mission.id)
                    }
                  }
                  .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(22.dp)
                      .clip(CircleShape)
                      .background(if (mission.isCompleted) VibrantGreen else Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                  ) {
                    if (mission.isCompleted) {
                      Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                      )
                    }
                  }

                  Text(
                    text = mission.title,
                    color = if (mission.isCompleted) TextDarkSecondary else TextDark,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                  )
                }

                if (mission.isCompleted && !mission.isClaimed) {
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .background(NeonGold)
                      .padding(horizontal = 10.dp, vertical = 4.dp)
                  ) {
                    Text(
                      text = "ÖDÜLÜ AL (+${mission.xpReward} XP)",
                      color = Color.White,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                } else if (mission.isClaimed) {
                  Text(text = "Alındı ✓", color = VibrantGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                } else {
                  Text(
                    text = "${mission.current}/${mission.target}",
                    color = TextDarkMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }
        }
      }

      // Hızlı Oyun (Kendini test et!) Card - High-energy Action Card
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(24.dp), spotColor = NeonOrange.copy(alpha = 0.4f))
            .clip(RoundedCornerShape(24.dp))
            .background(CardFrosted)
            .border(2.dp, NeonOrange, RoundedCornerShape(24.dp))
            .clickable { onQuickPlay() }
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
              Box(
                modifier = Modifier
                  .size(50.dp)
                  .shadow(6.dp, CircleShape, spotColor = NeonOrange.copy(alpha = 0.5f))
                  .background(OrangeLight, CircleShape)
                  .border(2.dp, NeonOrange, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.FlashOn,
                  contentDescription = "Hızlı Oyun",
                  tint = NeonOrange,
                  modifier = Modifier.size(30.dp)
                )
              }

              Column {
                Text(
                  text = "Hızlı Oyun Modu",
                  color = TextDark,
                  fontSize = 18.sp,
                  fontWeight = FontWeight.ExtraBold
                )
                Text(
                  text = "10 saniyede refleksini test et!",
                  color = TextDarkSecondary,
                  fontSize = 12.sp
                )
              }
            }

            // Prominent Action Button
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.horizontalGradient(listOf(NeonOrange, Color(0xFFEA580C))))
                .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
              Text(
                text = "OYNA ⚡",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black
              )
            }
          }
        }
      }

      // 3 Main Categories: Refleks, Mantık, Hafıza
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          CategoryPillCard(
            title = "Refleks",
            icon = "⚡",
            bgColor = OrangeLight,
            textColor = NeonOrange,
            modifier = Modifier.weight(1f),
            onClick = { onSelectGame(GameType.REFLEX_DOTS) }
          )
          CategoryPillCard(
            title = "Mantık",
            icon = "🧠",
            bgColor = Color(0xFFE0F2FE),
            textColor = BluePrimary,
            modifier = Modifier.weight(1f),
            onClick = { onSelectGame(GameType.QUICK_MATH) }
          )
          CategoryPillCard(
            title = "Hafıza",
            icon = "👁️",
            bgColor = PinkLight,
            textColor = VibrantPink,
            modifier = Modifier.weight(1f),
            onClick = { onSelectGame(GameType.MEMORY_SYMBOLS) }
          )
        }
      }

      // Lig & Türkiye Sıralaması Card
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = userProfile.league.color.copy(alpha = 0.3f))
            .clip(RoundedCornerShape(24.dp))
            .background(CardFrosted)
            .border(2.dp, userProfile.league.color, RoundedCornerShape(24.dp))
            .clickable { onOpenLeaderboard() }
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
              Box(
                modifier = Modifier
                  .size(48.dp)
                  .background(userProfile.league.color.copy(alpha = 0.15f), CircleShape)
                  .border(1.5.dp, userProfile.league.color, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Text(text = userProfile.league.iconEmoji, fontSize = 24.sp)
              }

              Column {
                Text(
                  text = userProfile.league.displayName,
                  color = TextDark,
                  fontSize = 17.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = if (userProfile.nationalRank == 1) "👑 Türkiye 1.sisin!"
                  else "Türkiye'de #${userProfile.nationalRank}",
                  color = if (userProfile.nationalRank == 1) NeonGold else BluePrimary,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFEFF6FF))
                .border(1.dp, BluePrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(
                text = "SIRALAMA ➔",
                color = BluePrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}

@Composable
fun CategoryPillCard(
  title: String,
  icon: String,
  bgColor: Color,
  textColor: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = textColor.copy(alpha = 0.35f))
      .clip(RoundedCornerShape(20.dp))
      .background(CardFrosted)
      .border(2.dp, textColor, RoundedCornerShape(20.dp))
      .clickable { onClick() }
      .padding(vertical = 16.dp, horizontal = 10.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(CircleShape)
          .background(bgColor)
          .border(1.5.dp, textColor.copy(alpha = 0.4f), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Text(text = icon, fontSize = 24.sp)
      }
      Text(
        text = title,
        color = textColor,
        fontSize = 14.sp,
        fontWeight = FontWeight.ExtraBold
      )
      Text(
        text = "OYNA ➔",
        color = textColor.copy(alpha = 0.8f),
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}
