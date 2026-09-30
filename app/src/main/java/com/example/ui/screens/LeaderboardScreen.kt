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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.model.LeaderboardEntry
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
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextDarkSecondary
import com.example.ui.theme.VibrantGreen
import java.text.NumberFormat
import java.util.Locale

@Composable
fun LeaderboardScreen(
  userProfile: UserProfile,
  entries: List<LeaderboardEntry>,
  selectedTab: Int,
  onSelectTab: (Int) -> Unit,
  onUpdateName: (String) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val tabs = listOf("Dünya", "Türkiye", "Arkadaşlar")
  val formatter = NumberFormat.getNumberInstance(Locale("tr", "TR"))
  var showEditNameDialog by remember { mutableStateOf(false) }
  var editedName by remember { mutableStateOf(userProfile.name) }

  AppBackgroundEffect(modifier = modifier) {
    Column(modifier = Modifier.fillMaxSize()) {
      Spacer(modifier = Modifier.height(16.dp))

      // Top Title Bar with Live Cloud Sync Status
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text(
            text = "🏆 Liderlik Tablosu",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
          )
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Box(
              modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(Color(0xFF34D399))
            )
            Text(
              text = "Canlı Bulut Sıralaması Aktif",
              color = Color(0xFF34D399),
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        IconButton(
          onClick = {
            editedName = userProfile.name
            showEditNameDialog = true
          },
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(CardFrosted)
            .border(1.5.dp, SkyBlueAccent, CircleShape)
        ) {
          Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "İsmi Değiştir",
            tint = BluePrimary,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Segmented Tab Controls
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp)
          .shadow(4.dp, RoundedCornerShape(18.dp), spotColor = Color(0x22000000))
          .clip(RoundedCornerShape(18.dp))
          .background(Color(0x991E293B))
          .border(1.dp, SkyBlueAccent.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
          .padding(4.dp)
      ) {
        Row(modifier = Modifier.fillMaxWidth()) {
          tabs.forEachIndexed { index, title ->
            val isSelected = selectedTab == index
            Box(
              modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .shadow(if (isSelected) 6.dp else 0.dp, RoundedCornerShape(14.dp))
                .clip(RoundedCornerShape(14.dp))
                .background(if (isSelected) BluePrimary else Color.Transparent)
                .clickable { onSelectTab(index) },
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = title,
                color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Competitors List
      LazyColumn(
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(entries) { entry ->
          val isUser = entry.isUser

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .shadow(
                elevation = if (isUser) 10.dp else 4.dp,
                shape = RoundedCornerShape(18.dp),
                spotColor = if (isUser) NeonGold.copy(alpha = 0.5f) else Color(0x1A000000)
              )
              .clip(RoundedCornerShape(18.dp))
              .background(if (isUser) Color(0xFFFFFBEB) else CardFrosted)
              .border(
                width = if (isUser) 2.5.dp else 1.5.dp,
                color = if (isUser) NeonGold else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(18.dp)
              )
              .padding(horizontal = 14.dp, vertical = 12.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                // Rank Circle
                Box(
                  modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                      when (entry.rank) {
                        1 -> Color(0xFFFFD700)
                        2 -> Color(0xFFE2E8F0)
                        3 -> Color(0xFFFDBA74)
                        else -> Color(0xFFF1F5F9)
                      }
                    )
                    .border(
                      width = 1.dp,
                      color = when (entry.rank) {
                        1 -> Color(0xFFB45309)
                        2 -> Color(0xFF94A3B8)
                        3 -> Color(0xFFEA580C)
                        else -> Color(0xFFCBD5E1)
                      },
                      shape = CircleShape
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = when (entry.rank) {
                      1 -> "🥇"
                      2 -> "🥈"
                      3 -> "🥉"
                      else -> "#${entry.rank}"
                    },
                    fontSize = if (entry.rank in 1..3) 16.sp else 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                  )
                }

                Text(text = entry.avatarEmoji, fontSize = 24.sp)

                Column {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Text(
                      text = entry.name,
                      color = TextDark,
                      fontSize = 15.sp,
                      fontWeight = if (isUser) FontWeight.ExtraBold else FontWeight.Bold
                    )
                    if (isUser) {
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(6.dp))
                          .background(NeonGold)
                          .padding(horizontal = 6.dp, vertical = 2.dp)
                      ) {
                        Text(
                          text = "SEN",
                          color = Color.White,
                          fontSize = 10.sp,
                          fontWeight = FontWeight.Black
                        )
                      }
                    }
                  }
                  Text(
                    text = entry.league.displayName,
                    color = entry.league.color,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                  )
                }
              }

              // Score
              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = "${formatter.format(entry.score)} P",
                  color = if (isUser) BluePrimary else TextDark,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.ExtraBold
                )
                Text(
                  text = "Skor",
                  color = TextDarkMuted,
                  fontSize = 10.sp
                )
              }
            }
          }
        }

        item {
          Spacer(modifier = Modifier.height(10.dp))
        }
      }

      // Bottom Sticky Player Rank Banner (Turkey Flag theme)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .shadow(10.dp, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
          .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
          .background(CardWhite)
          .border(1.dp, CardBorderLight, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
          .padding(horizontal = 20.dp, vertical = 14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text(text = "🇹🇷", fontSize = 24.sp)
            Column {
              Text(
                text = "Senin Sıralaman",
                color = TextDarkSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
              Text(
                text = if (userProfile.nationalRank == 1) "👑 Türkiye 1.sisin!"
                else "Türkiye'de #${userProfile.nationalRank}",
                color = TextDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
              )
            }
          }

          Text(
            text = "${formatter.format(userProfile.totalScore)} Puan",
            color = BluePrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold
          )
        }
      }
    }

    // Edit Name Dialog
    if (showEditNameDialog) {
      AlertDialog(
        onDismissRequest = { showEditNameDialog = false },
        title = { Text("Oyuncu Adını Belirle", color = TextDark, fontWeight = FontWeight.Bold) },
        text = {
          Column {
            Text("Liderlik tablosunda arkadaşlarına görünecek adın:", color = TextDarkSecondary, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = editedName,
              onValueChange = { editedName = it },
              singleLine = true,
              label = { Text("Adın") }
            )
          }
        },
        confirmButton = {
          Button(onClick = {
            onUpdateName(editedName)
            showEditNameDialog = false
          }) {
            Text("Kaydet")
          }
        },
        dismissButton = {
          TextButton(onClick = { showEditNameDialog = false }) {
            Text("İptal")
          }
        }
      )
    }
  }
}
