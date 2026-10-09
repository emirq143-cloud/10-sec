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

import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.window.Dialog
import com.example.util.NicknameValidationResult
import com.example.util.ProfanityFilter

@Composable
fun ProfileScreen(
  userProfile: UserProfile,
  avatars: List<AvatarItem>,
  onSelectAvatar: (String) -> Unit,
  onPurchaseAvatar: (String) -> Unit = {},
  onOpenSettings: () -> Unit = {},
  onOpenReactionAnalytics: () -> Unit = {},
  onUpdateName: (String) -> Unit = {},
  modifier: Modifier = Modifier
) {
  var avatarFilter by remember { mutableStateOf("Tümü") }
  var avatarToPurchase by remember { mutableStateOf<AvatarItem?>(null) }
  var showEditNameDialog by remember { mutableStateOf(false) }
  var editedNickname by remember { mutableStateOf(userProfile.name) }

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
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text(
                    text = userProfile.name,
                    color = TextDark,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                  )
                  IconButton(
                    onClick = {
                      editedNickname = userProfile.name
                      showEditNameDialog = true
                    },
                    modifier = Modifier
                      .size(28.dp)
                      .testTag("edit_nickname_button")
                  ) {
                    Icon(
                      imageVector = Icons.Default.Edit,
                      contentDescription = "Takma Adı Düzenle",
                      tint = BluePrimary,
                      modifier = Modifier.size(16.dp)
                    )
                  }
                }
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
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text(
                text = "Karakterler",
                color = TextDark,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
              )
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color(0xFFFEF3C7))
                  .border(1.dp, NeonGold, RoundedCornerShape(10.dp))
                  .padding(horizontal = 8.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "🪙 ${userProfile.coins}",
                  color = Color(0xFFB45309),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.ExtraBold
                )
              }
            }

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
              val progress = if (avatar.priceCoins > 0) (userProfile.coins.toFloat() / avatar.priceCoins).coerceIn(0f, 1f) else 1f

              Box(
                modifier = Modifier
                  .width(114.dp)
                  .height(132.dp)
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
                  .clickable {
                    if (avatar.isUnlocked) {
                      onSelectAvatar(avatar.id)
                    } else {
                      avatarToPurchase = avatar
                    }
                  }
                  .padding(8.dp),
                contentAlignment = Alignment.Center
              ) {
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.SpaceBetween,
                  modifier = Modifier.fillMaxSize()
                ) {
                  // Rarity Tag
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .background(avatar.rarity.color.copy(alpha = 0.15f))
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = avatar.rarity.label,
                      color = avatar.rarity.color,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Black
                    )
                  }

                  Text(text = avatar.emoji, fontSize = 32.sp)

                  Text(
                    text = avatar.name,
                    color = if (avatar.isUnlocked) TextDark else TextDarkMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1
                  )

                  if (isSelected) {
                    Text(
                      text = "AKTİF",
                      color = Color(0xFFB45309),
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Black
                    )
                  } else if (avatar.isUnlocked) {
                    Text(
                      text = "SEÇ ➔",
                      color = BluePrimary,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold
                    )
                  } else {
                    Column(
                      horizontalAlignment = Alignment.CenterHorizontally,
                      verticalArrangement = Arrangement.spacedBy(2.dp),
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                      ) {
                        Text(text = "🪙", fontSize = 10.sp)
                        Text(
                          text = "${avatar.priceCoins}",
                          color = Color(0xFFB45309),
                          fontSize = 10.sp,
                          fontWeight = FontWeight.ExtraBold
                        )
                      }
                      // Progress Bar towards unlocking
                      Box(
                        modifier = Modifier
                          .fillMaxWidth()
                          .height(4.dp)
                          .clip(RoundedCornerShape(2.dp))
                          .background(Color(0xFFE2E8F0))
                      ) {
                        Box(
                          modifier = Modifier
                            .fillMaxWidth(fraction = progress)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (progress >= 1f) VibrantGreen else NeonGold)
                        )
                      }
                    }
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
                      modifier = Modifier.size(11.dp)
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

    avatarToPurchase?.let { targetAvatar ->
      AvatarPurchaseModal(
        avatar = targetAvatar,
        userCoins = userProfile.coins,
        onConfirmPurchase = {
          onPurchaseAvatar(targetAvatar.id)
          avatarToPurchase = null
        },
        onDismiss = { avatarToPurchase = null }
      )
    }

    if (showEditNameDialog) {
      val validation = remember(editedNickname) { ProfanityFilter.validateNickname(editedNickname) }

      AlertDialog(
        onDismissRequest = { showEditNameDialog = false },
        title = { Text("Takma Adını Değiştir", color = TextDark, fontWeight = FontWeight.Bold) },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
              text = "Liderlik tablosunda ve profilinde görünecek takma adını belirle (Küfür ve hakaret içeremez):",
              color = TextDarkSecondary,
              fontSize = 13.sp
            )
            OutlinedTextField(
              value = editedNickname,
              onValueChange = {
                if (it.length <= 16) editedNickname = it
              },
              singleLine = true,
              isError = validation is NicknameValidationResult.Invalid,
              label = { Text("Takma Ad") },
              modifier = Modifier.fillMaxWidth().testTag("edit_nickname_textfield")
            )
            if (validation is NicknameValidationResult.Invalid) {
              Text(
                text = "⚠️ " + validation.errorMessage,
                color = Color(0xFFEF4444),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        },
        confirmButton = {
          Button(
            onClick = {
              if (validation is NicknameValidationResult.Valid) {
                onUpdateName(editedNickname.trim())
                showEditNameDialog = false
              }
            },
            enabled = validation is NicknameValidationResult.Valid,
            modifier = Modifier.testTag("save_nickname_button")
          ) {
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

@Composable
fun AvatarPurchaseModal(
  avatar: AvatarItem,
  userCoins: Int,
  onConfirmPurchase: () -> Unit,
  onDismiss: () -> Unit
) {
  val canAfford = userCoins >= avatar.priceCoins

  Dialog(onDismissRequest = onDismiss) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(26.dp))
        .background(CardWhite)
        .border(2.5.dp, if (canAfford) NeonGold else Color(0xFFCBD5E1), RoundedCornerShape(26.dp))
        .padding(22.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Emoji in glowing avatar ring
        Box(
          modifier = Modifier
            .size(76.dp)
            .shadow(12.dp, CircleShape, spotColor = if (canAfford) NeonGold else Color(0x33000000))
            .clip(CircleShape)
            .background(if (canAfford) Color(0xFFFEF3C7) else Color(0xFFF1F5F9))
            .border(2.5.dp, if (canAfford) NeonGold else Color(0xFF94A3B8), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(text = avatar.emoji, fontSize = 42.sp)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(avatar.rarity.color.copy(alpha = 0.15f))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = avatar.rarity.label,
              color = avatar.rarity.color,
              fontSize = 11.sp,
              fontWeight = FontWeight.Black
            )
          }
          Text(
            text = avatar.name,
            color = TextDark,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
          )
          Text(
            text = avatar.description,
            color = TextDarkSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
          )
        }

        // Price & Balance Card
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
            .padding(14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(text = "Fiyat", color = TextDarkSecondary, fontSize = 11.sp)
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "🪙", fontSize = 14.sp)
                Text(text = "${avatar.priceCoins} Altın", color = Color(0xFFB45309), fontSize = 15.sp, fontWeight = FontWeight.Black)
              }
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(text = "Senin Bakiyen", color = TextDarkSecondary, fontSize = 11.sp)
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "🪙", fontSize = 14.sp)
                Text(
                  text = "$userCoins Altın",
                  color = if (canAfford) VibrantGreen else Color(0xFFEF4444),
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Black
                )
              }
            }
          }
        }

        if (!canAfford) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFFFEF2F2))
              .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(12.dp))
              .padding(10.dp)
          ) {
            Text(
              text = "⚠️ Yetersiz Altın! ${avatar.priceCoins - userCoins} altın daha gerekiyor. Mini oyunlar oynayarak veya çarkı çevirerek altın kazanabilirsin.",
              color = Color(0xFFDC2626),
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        // Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
          ) {
            Text(text = "Vazgeç", color = TextDarkSecondary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }

          if (canAfford) {
            Button(
              onClick = onConfirmPurchase,
              colors = ButtonDefaults.buttonColors(containerColor = NeonGold),
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier
                .weight(1.5f)
                .height(48.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = NeonGold.copy(alpha = 0.6f))
            ) {
              Text(text = "Satın Al & Kuşan ✨", color = Color(0xFF78350F), fontWeight = FontWeight.Black, fontSize = 13.sp)
            }
          }
        }
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
