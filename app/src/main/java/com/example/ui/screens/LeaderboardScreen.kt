package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeaderboardEntry
import com.example.data.model.UserProfile
import com.example.ui.components.AppBackgroundEffect
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.CardFrosted
import com.example.ui.theme.CardWhite
import com.example.ui.theme.NeonGold
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextDarkSecondary
import com.example.util.NicknameValidationResult
import com.example.util.ProfanityFilter
import com.google.firebase.auth.FirebaseUser
import java.text.NumberFormat
import java.util.Locale

fun getCountryFlag(countryCode: String): String {
  return when (countryCode.uppercase()) {
    "TR" -> "🇹🇷"
    "US" -> "🇺🇸"
    "JP" -> "🇯🇵"
    "DE" -> "🇩🇪"
    "BR" -> "🇧🇷"
    "IT" -> "🇮🇹"
    "FR" -> "🇫🇷"
    "GB", "UK" -> "🇬🇧"
    "ES" -> "🇪🇸"
    "KR" -> "🇰🇷"
    "CA" -> "🇨🇦"
    else -> "🌍"
  }
}

@Composable
fun LeaderboardScreen(
  userProfile: UserProfile,
  entries: List<LeaderboardEntry>,
  selectedTab: Int, // 0: Türkiye, 1: Dünya
  onSelectTab: (Int) -> Unit,
  onUpdateName: (String) -> Unit = {},
  currentUser: FirebaseUser? = null,
  isSubmittingScore: Boolean = false,
  syncStatusMessage: String? = null,
  onDismissSyncMessage: () -> Unit = {},
  onSignInWithGoogle: (Context, (Boolean, String?) -> Unit) -> Unit = { _, _ -> },
  onSignOut: (Context) -> Unit = {},
  onSubmitScoreToCloud: (((Boolean, String) -> Unit)?) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val formatter = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("tr-TR")) }

  var showEditProfileDialog by remember { mutableStateOf(false) }
  var editedName by remember { mutableStateOf(userProfile.name) }

  val top3 = remember(entries) { entries.take(3) }
  val remainingEntries = remember(entries) {
    if (entries.size > 3) entries.drop(3) else emptyList()
  }

  val userRankEntry = remember(entries) {
    entries.find { it.isUser }
  }

  val isTurkeyTab = selectedTab == 0

  AppBackgroundEffect(modifier = modifier) {
    Column(modifier = Modifier.fillMaxSize()) {
      Spacer(modifier = Modifier.height(14.dp))

      // Top Title Bar & Firestore Sync Status
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = "🏆 Liderlik Tablosu",
              color = Color.White,
              fontSize = 22.sp,
              fontWeight = FontWeight.ExtraBold
            )
            Text(text = if (isTurkeyTab) "🇹🇷" else "🌍", fontSize = 20.sp)
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(top = 2.dp)
          ) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (currentUser != null) Color(0xFF34D399) else Color(0xFFFBBF24))
            )
            Text(
              text = if (currentUser != null) "Canlı Bulut Sıralaması" else "Yerel Sıralama",
              color = if (currentUser != null) Color(0xFF34D399) else Color(0xFFFDE68A),
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        IconButton(
          onClick = {
            editedName = userProfile.name
            showEditProfileDialog = true
          },
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(CardFrosted)
            .border(1.5.dp, SkyBlueAccent, CircleShape)
            .testTag("edit_player_name_button")
        ) {
          Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Takma Adı Düzenle",
            tint = BluePrimary,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Segmented Tabs: Türkiye & Dünya (clean and simple)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 18.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(Color(0xFF0F172A).copy(alpha = 0.9f))
          .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
          .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // Tab 0: Türkiye
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isTurkeyTab) BluePrimary else Color.Transparent)
            .clickable { onSelectTab(0) }
            .padding(vertical = 10.dp)
            .testTag("leaderboard_tab_turkey"),
          contentAlignment = Alignment.Center
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(text = "🇹🇷", fontSize = 16.sp)
            Text(
              text = "Türkiye",
              color = if (isTurkeyTab) Color.White else Color(0xFF94A3B8),
              fontSize = 14.sp,
              fontWeight = if (isTurkeyTab) FontWeight.Bold else FontWeight.Medium
            )
          }
        }

        // Tab 1: Dünya
        val isWorldTab = selectedTab == 1
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isWorldTab) BluePrimary else Color.Transparent)
            .clickable { onSelectTab(1) }
            .padding(vertical = 10.dp)
            .testTag("leaderboard_tab_world"),
          contentAlignment = Alignment.Center
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(text = "🌍", fontSize = 16.sp)
            Text(
              text = "Dünya",
              color = if (isWorldTab) Color.White else Color(0xFF94A3B8),
              fontSize = 14.sp,
              fontWeight = if (isWorldTab) FontWeight.Bold else FontWeight.Medium
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Google Sign-In or Cloud Sync Banner
      if (currentUser == null) {
        // Unauthenticated Banner: Invite user to sign in with Google
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(
              Brush.linearGradient(
                listOf(Color(0xFF1E293B), Color(0xFF0F172A))
              )
            )
            .border(1.5.dp, Color(0xFF38BDF8).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(14.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text(text = "🧠", fontSize = 22.sp)
              Column {
                Text(
                  text = "Liderlik Tablosunda Yerini Al!",
                  color = Color.White,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "Beyin skorunu kaydetmek ve sıralamada yarışmak için giriş yap.",
                  color = Color(0xFFCBD5E1),
                  fontSize = 11.sp
                )
              }
            }

            Button(
              onClick = {
                onSignInWithGoogle(context) { success, errorMsg ->
                  if (!success && errorMsg != null) {
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                  }
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("google_sign_in_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF1F2937)
              )
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                // Google "G" Badge
                Box(
                  modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEA4335)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(text = "G", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
                }
                Text(
                  text = "Google ile Giriş Yap",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF1F2937)
                )
              }
            }
          }
        }
      } else {
        // Authenticated Card: Show Cloud Sync Info & Sync Button
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A).copy(alpha = 0.85f))
            .border(1.dp, Color(0xFF34D399).copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp),
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(Color(0xFF1E293B))
                  .border(1.dp, Color(0xFF34D399), CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Text(text = "🧑‍💻", fontSize = 18.sp)
              }

              Column {
                Text(
                  text = currentUser.displayName ?: userProfile.name,
                  color = Color.White,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Text(
                  text = "Canlı Bulut Senkronize",
                  color = Color(0xFF34D399),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium
                )
              }
            }

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              // Sync Score Button
              Button(
                onClick = { onSubmitScoreToCloud(null) },
                enabled = !isSubmittingScore,
                modifier = Modifier
                  .height(36.dp)
                  .testTag("sync_score_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = BluePrimary,
                  contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 10.dp)
              ) {
                if (isSubmittingScore) {
                  CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                  )
                } else {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.CloudUpload,
                      contentDescription = "Skoru Güncelle",
                      modifier = Modifier.size(16.dp)
                    )
                    Text("Skoru Kaydet", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }

              // Sign Out Text Button
              TextButton(
                onClick = { onSignOut(context) },
                modifier = Modifier.testTag("sign_out_button")
              ) {
                Text("Çıkış", color = Color(0xFF94A3B8), fontSize = 11.sp)
              }
            }
          }
        }
      }

      // Sync status notification bar
      if (!syncStatusMessage.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF064E3B))
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = syncStatusMessage,
              color = Color(0xFF6EE7B7),
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
            IconButton(
              onClick = onDismissSyncMessage,
              modifier = Modifier.size(20.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Kapat",
                tint = Color(0xFF6EE7B7),
                modifier = Modifier.size(14.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Scrollable Rankings Area: Podium (Top 3) + Ranks (4+)
      LazyColumn(
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // TOP 3 PODIUM (KÜRSÜ)
        if (top3.isNotEmpty()) {
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(
                  Brush.verticalGradient(
                    listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                  )
                )
                .border(1.dp, SkyBlueAccent.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .padding(vertical = 14.dp, horizontal = 10.dp)
            ) {
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = if (isTurkeyTab) "👑 TÜRKİYE'NİN EN İYİLERİ" else "👑 DÜNYANIN EN İYİLERİ",
                  color = NeonGold,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceEvenly,
                  verticalAlignment = Alignment.Bottom
                ) {
                  // 2nd Place (Left)
                  if (top3.size >= 2) {
                    PodiumColumn(
                      entry = top3[1],
                      rank = 2,
                      medal = "🥈",
                      pedestalHeight = 70.dp,
                      badgeColor = Color(0xFFE2E8F0),
                      formatter = formatter,
                      isWorldTab = !isTurkeyTab
                    )
                  } else {
                    Spacer(modifier = Modifier.width(90.dp))
                  }

                  // 1st Place (Center - Highest)
                  PodiumColumn(
                    entry = top3[0],
                    rank = 1,
                    medal = "🥇",
                    pedestalHeight = 96.dp,
                    badgeColor = Color(0xFFFFD700),
                    formatter = formatter,
                    isWorldTab = !isTurkeyTab
                  )

                  // 3rd Place (Right)
                  if (top3.size >= 3) {
                    PodiumColumn(
                      entry = top3[2],
                      rank = 3,
                      medal = "🥉",
                      pedestalHeight = 54.dp,
                      badgeColor = Color(0xFFFDBA74),
                      formatter = formatter,
                      isWorldTab = !isTurkeyTab
                    )
                  } else {
                    Spacer(modifier = Modifier.width(90.dp))
                  }
                }
              }
            }
          }
        }

        // Leaderboard items (4+)
        items(remainingEntries) { entry ->
          val isUser = entry.isUser

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .shadow(
                elevation = if (isUser) 8.dp else 3.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = if (isUser) NeonGold.copy(alpha = 0.5f) else Color(0x1A000000)
              )
              .clip(RoundedCornerShape(16.dp))
              .background(if (isUser) Color(0xFFFFFBEB) else CardFrosted)
              .border(
                width = if (isUser) 2.dp else 1.dp,
                color = if (isUser) NeonGold else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(16.dp)
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
                // Rank Number Pill
                Box(
                  modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F5F9))
                    .border(1.dp, Color(0xFFCBD5E1), CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "#${entry.rank}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
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
                      fontWeight = if (isUser) FontWeight.ExtraBold else FontWeight.Bold,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
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

                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    if (!isTurkeyTab) {
                      Text(
                        text = "${getCountryFlag(entry.country)} ${entry.country}",
                        color = TextDarkMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                      )
                    }
                    Text(
                      text = entry.league.displayName,
                      color = entry.league.color,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.SemiBold
                    )
                  }
                }
              }

              // Score
              Column(horizontalAlignment = Alignment.End) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                  Text(
                    text = "🧠",
                    fontSize = 13.sp
                  )
                  Text(
                    text = formatter.format(entry.score),
                    color = if (isUser) BluePrimary else TextDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                  )
                }
                Text(
                  text = "Beyin Skoru",
                  color = TextDarkMuted,
                  fontSize = 10.sp
                )
              }
            }
          }
        }

        item {
          Spacer(modifier = Modifier.height(12.dp))
        }
      }

      // Bottom Sticky Player Rank Banner
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .shadow(12.dp, RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
          .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
          .background(CardWhite)
          .border(1.dp, CardBorderLight, RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
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
            Text(text = if (isTurkeyTab) "🇹🇷" else "🌍", fontSize = 26.sp)
            Column {
              Text(
                text = if (isTurkeyTab) "Senin Sıralaman (Türkiye)" else "Senin Sıralaman (Dünya)",
                color = TextDarkSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
              Text(
                text = if (userRankEntry != null) "#${userRankEntry.rank} Sırada"
                else if (isTurkeyTab) "Türkiye'de #${userProfile.nationalRank}"
                else "Dünyada #${userProfile.nationalRank + 240}",
                color = TextDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
              )
            }
          }

          Column(horizontalAlignment = Alignment.End) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Text(text = "🧠", fontSize = 14.sp)
              Text(
                text = formatter.format(userProfile.overallBrainScore * 10 + userProfile.totalScore),
                color = BluePrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold
              )
            }
            Text(
              text = "Beyin Puanı",
              color = TextDarkMuted,
              fontSize = 10.sp
            )
          }
        }
      }
    }

    // Edit Name Dialog with Profanity Filter
    if (showEditProfileDialog) {
      val validation = remember(editedName) { ProfanityFilter.validateNickname(editedName) }

      AlertDialog(
        onDismissRequest = { showEditProfileDialog = false },
        title = {
          Text(
            text = "Takma Adını Değiştir",
            color = TextDark,
            fontWeight = FontWeight.Bold
          )
        },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
              text = "Liderlik tablosunda görünecek takma adını belirle (Küfür ve hakaret içeremez):",
              color = TextDarkSecondary,
              fontSize = 13.sp
            )

            OutlinedTextField(
              value = editedName,
              onValueChange = {
                if (it.length <= 16) editedName = it
              },
              singleLine = true,
              isError = validation is NicknameValidationResult.Invalid,
              label = { Text("Oyuncu Adı / Takma Ad") },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("edit_leaderboard_nickname_field")
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
                onUpdateName(editedName.trim())
                showEditProfileDialog = false
                if (currentUser != null) {
                  onSubmitScoreToCloud(null)
                }
              }
            },
            enabled = validation is NicknameValidationResult.Valid,
            modifier = Modifier.testTag("save_leaderboard_nickname_button")
          ) {
            Text("Kaydet & Güncelle")
          }
        },
        dismissButton = {
          TextButton(onClick = { showEditProfileDialog = false }) {
            Text("İptal")
          }
        }
      )
    }
  }
}

@Composable
fun PodiumColumn(
  entry: LeaderboardEntry,
  rank: Int,
  medal: String,
  pedestalHeight: androidx.compose.ui.unit.Dp,
  badgeColor: Color,
  formatter: NumberFormat,
  isWorldTab: Boolean = false
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.width(96.dp)
  ) {
    // Medal & Avatar
    Text(text = medal, fontSize = 20.sp)
    Text(text = entry.avatarEmoji, fontSize = 28.sp)

    Text(
      text = entry.name,
      color = Color.White,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      textAlign = TextAlign.Center
    )

    if (isWorldTab) {
      Text(
        text = "${getCountryFlag(entry.country)} ${entry.country}",
        color = Color(0xFF94A3B8),
        fontSize = 10.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    } else {
      Text(
        text = entry.league.displayName,
        color = entry.league.color,
        fontSize = 10.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }

    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
      Text(text = "🧠", fontSize = 11.sp)
      Text(
        text = formatter.format(entry.score),
        color = NeonGold,
        fontSize = 12.sp,
        fontWeight = FontWeight.ExtraBold
      )
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Pedestal Block
    Box(
      modifier = Modifier
        .width(80.dp)
        .height(pedestalHeight)
        .shadow(4.dp, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
        .background(
          Brush.verticalGradient(
            when (rank) {
              1 -> listOf(Color(0xFFD97706), Color(0xFFB45309))
              2 -> listOf(Color(0xFF64748B), Color(0xFF475569))
              else -> listOf(Color(0xFFB45309), Color(0xFF78350F))
            }
          )
        )
        .border(
          width = 1.dp,
          color = badgeColor.copy(alpha = 0.6f),
          shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
        ),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "$rank",
        color = Color.White,
        fontSize = 26.sp,
        fontWeight = FontWeight.Black
      )
    }
  }
}
