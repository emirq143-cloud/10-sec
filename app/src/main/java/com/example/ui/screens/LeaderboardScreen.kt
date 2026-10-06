package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

val TURKISH_CITIES = listOf(
  "İstanbul", "Ankara", "İzmir", "Bursa", "Antalya",
  "Adana", "Konya", "Gaziantep", "Şanlıurfa", "Kocaeli",
  "Mersin", "Diyarbakır", "Hatay", "Manisa", "Kayseri",
  "Samsun", "Balıkesir", "Kahramanmaraş", "Van", "Aydın",
  "Tekirdağ", "Denizli", "Sakarya", "Muğla", "Eskişehir",
  "Trabzon", "Malatya", "Ordu", "Erzurum", "Sivas"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
  userProfile: UserProfile,
  entries: List<LeaderboardEntry>,
  selectedTab: Int,
  onSelectTab: (Int) -> Unit,
  onUpdateName: (String) -> Unit = {},
  currentUser: FirebaseUser? = null,
  selectedCity: String = "İstanbul",
  onSelectCity: (String) -> Unit = {},
  isSubmittingScore: Boolean = false,
  syncStatusMessage: String? = null,
  onDismissSyncMessage: () -> Unit = {},
  onSignInWithGoogle: (Context, (Boolean, String?) -> Unit) -> Unit = { _, _ -> },
  onSignOut: (Context) -> Unit = {},
  onSubmitScoreToCloud: (((Boolean, String) -> Unit)?) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("tr-TR"))

  var showEditProfileDialog by remember { mutableStateOf(false) }
  var editedName by remember { mutableStateOf(userProfile.name) }
  var editedCity by remember { mutableStateOf(selectedCity) }
  var cityFilter by remember { mutableStateOf("Tüm Türkiye") }

  // Filter entries based on selected city filter chip
  val displayedEntries = remember(entries, cityFilter) {
    if (cityFilter == "Tüm Türkiye") {
      entries
    } else {
      entries.filter { it.city.equals(cityFilter, ignoreCase = true) || it.isUser }
    }
  }

  val top3 = remember(displayedEntries) { displayedEntries.take(3) }
  val remainingEntries = remember(displayedEntries) {
    if (displayedEntries.size > 3) displayedEntries.drop(3) else emptyList()
  }

  val userRankEntry = remember(displayedEntries) {
    displayedEntries.find { it.isUser }
  }

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
              text = "🏆 Türkiye Beyin Ligi",
              color = Color.White,
              fontSize = 22.sp,
              fontWeight = FontWeight.ExtraBold
            )
            Text(text = "🇹🇷", fontSize = 20.sp)
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
              text = if (currentUser != null) "Firestore Canlı Bulut Sıralaması" else "Yerel Mod (Giriş Yapılmadı)",
              color = if (currentUser != null) Color(0xFF34D399) else Color(0xFFFDE68A),
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        IconButton(
          onClick = {
            editedName = userProfile.name
            editedCity = selectedCity
            showEditProfileDialog = true
          },
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(CardFrosted)
            .border(1.5.dp, SkyBlueAccent, CircleShape)
            .testTag("edit_city_name_button")
        ) {
          Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Bilgileri Düzenle",
            tint = BluePrimary,
            modifier = Modifier.size(20.dp)
          )
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
                  text = "Türkiye Sıralamasında Yerini Al!",
                  color = Color.White,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "Beyin skorunu kaydetmek ve Türkiye genelinde yarışmak için giriş yap.",
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
                  text = "📍 $selectedCity • Canlı Bulut Senkronize",
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

      // Turkish City Filter Horizontal Chips
      Column(modifier = Modifier.padding(horizontal = 18.dp)) {
        Text(
          text = "📍 İl Filtresi & Bölgesel Sıralama",
          color = Color(0xFFCBD5E1),
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          modifier = Modifier.padding(bottom = 6.dp)
        )

        val cityChips = listOf("Tüm Türkiye", "İstanbul", "Ankara", "İzmir", "Bursa", "Antalya", "Adana", "Konya", "Trabzon", "Eskişehir", "Gaziantep", "Samsun")

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          contentPadding = PaddingValues(vertical = 2.dp)
        ) {
          items(cityChips) { city ->
            val isSelected = cityFilter == city
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (isSelected) BluePrimary else Color(0x661E293B))
                .border(
                  width = 1.dp,
                  color = if (isSelected) Color(0xFF38BDF8) else Color(0x33CBD5E1),
                  shape = RoundedCornerShape(20.dp)
                )
                .clickable { cityFilter = city }
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .testTag("city_filter_${city.lowercase()}")
            ) {
              Text(
                text = city,
                color = if (isSelected) Color.White else Color(0xFFE2E8F0),
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

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
                  text = "👑 ZİRVEDEKİ BEYİNLER",
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
                      formatter = formatter
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
                    formatter = formatter
                  )

                  // 3rd Place (Right)
                  if (top3.size >= 3) {
                    PodiumColumn(
                      entry = top3[2],
                      rank = 3,
                      medal = "🥉",
                      pedestalHeight = 54.dp,
                      badgeColor = Color(0xFFFDBA74),
                      formatter = formatter
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
                    Text(
                      text = "📍 ${entry.city}",
                      color = TextDarkMuted,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Medium
                    )
                    Text(
                      text = "• ${entry.league.displayName}",
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
            Text(text = "🇹🇷", fontSize = 26.sp)
            Column {
              Text(
                text = "Senin Sıralaman (📍 $selectedCity)",
                color = TextDarkSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
              Text(
                text = if (userRankEntry != null) "#${userRankEntry.rank} Sırada"
                else "Türkiye'de #${userProfile.nationalRank}",
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
                text = "${formatter.format(userProfile.overallBrainScore * 10 + userProfile.totalScore)}",
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

    // Edit Name & City Dialog
    if (showEditProfileDialog) {
      AlertDialog(
        onDismissRequest = { showEditProfileDialog = false },
        title = { Text("Oyuncu Profilini Güncelle", color = TextDark, fontWeight = FontWeight.Bold) },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
              text = "Liderlik tablosunda Türkiye'ye görünecek adını ve şehrini seç:",
              color = TextDarkSecondary,
              fontSize = 13.sp
            )

            OutlinedTextField(
              value = editedName,
              onValueChange = { editedName = it },
              singleLine = true,
              label = { Text("Oyuncu Adı") },
              modifier = Modifier.fillMaxWidth()
            )

            Text(
              text = "Şehrin:",
              color = TextDark,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )

            // City selection dropdown/chips
            var expandedCityDropdown by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
              expanded = expandedCityDropdown,
              onExpandedChange = { expandedCityDropdown = !expandedCityDropdown }
            ) {
              OutlinedTextField(
                value = editedCity,
                onValueChange = {},
                readOnly = true,
                label = { Text("Şehir") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCityDropdown) },
                modifier = Modifier
                  .menuAnchor()
                  .fillMaxWidth()
              )
              ExposedDropdownMenu(
                expanded = expandedCityDropdown,
                onDismissRequest = { expandedCityDropdown = false }
              ) {
                TURKISH_CITIES.forEach { city ->
                  DropdownMenuItem(
                    text = { Text(city) },
                    onClick = {
                      editedCity = city
                      expandedCityDropdown = false
                    }
                  )
                }
              }
            }
          }
        },
        confirmButton = {
          Button(onClick = {
            onUpdateName(editedName)
            onSelectCity(editedCity)
            showEditProfileDialog = false
            // Auto submit to cloud if signed in
            if (currentUser != null) {
              onSubmitScoreToCloud(null)
            }
          }) {
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
  formatter: NumberFormat
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

    Text(
      text = "📍 ${entry.city}",
      color = Color(0xFF94A3B8),
      fontSize = 10.sp,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )

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
