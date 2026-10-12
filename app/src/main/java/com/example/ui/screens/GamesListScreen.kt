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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.GameCategory
import com.example.data.model.GameType
import com.example.ui.components.AppBackgroundEffect
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.CardFrosted
import com.example.ui.theme.CardWhite
import com.example.ui.theme.LightBgGradientBottom
import com.example.ui.theme.LightBgGradientTop
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextDarkSecondary
import com.example.util.LocalizationManager

@Composable
fun GamesListScreen(
  onSelectGame: (GameType) -> Unit,
  modifier: Modifier = Modifier
) {
  val currentLang by LocalizationManager.currentLanguage.collectAsStateWithLifecycle()
  var selectedCategory by remember { mutableStateOf<GameCategory?>(null) }
  val allGames = remember { GameType.values().toList() }

  val filteredGames = remember(selectedCategory) {
    if (selectedCategory == null) allGames else allGames.filter { it.category == selectedCategory }
  }

  AppBackgroundEffect(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 18.dp)
    ) {
      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = LocalizationManager.string("games_title"),
        color = Color.White,
        fontSize = 24.sp,
        fontWeight = FontWeight.ExtraBold
      )

      Text(
        text = LocalizationManager.string("games_subtitle"),
        color = SkyBlueAccent,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Category Filter Chips
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        item {
          FilterChip(
            selected = selectedCategory == null,
            onClick = { selectedCategory = null },
            label = { Text(LocalizationManager.string("all_filter"), fontWeight = FontWeight.Bold) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = BluePrimary,
              selectedLabelColor = Color.White,
              containerColor = CardFrosted,
              labelColor = TextDark
            )
          )
        }
        items(GameCategory.values()) { category ->
          val catLabel = when (category) {
            GameCategory.REFLEX -> LocalizationManager.string("cat_reflex")
            GameCategory.LOGIC -> LocalizationManager.string("cat_logic")
            GameCategory.MEMORY -> LocalizationManager.string("cat_memory")
            GameCategory.ATTENTION -> LocalizationManager.string("cat_attention")
          }
          FilterChip(
            selected = selectedCategory == category,
            onClick = { selectedCategory = if (selectedCategory == category) null else category },
            label = { Text(catLabel, fontWeight = FontWeight.Bold) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = BluePrimary,
              selectedLabelColor = Color.White,
              containerColor = CardFrosted,
              labelColor = TextDark
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // List of games
      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(filteredGames) { game ->
          GameRowCard(
            game = game,
            onClick = { onSelectGame(game) }
          )
        }
        item {
          Spacer(modifier = Modifier.height(16.dp))
        }
      }
    }
  }
}

@Composable
fun GameRowCard(
  game: GameType,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = game.category.accentColor.copy(alpha = 0.35f))
      .clip(RoundedCornerShape(20.dp))
      .background(CardFrosted)
      .border(2.dp, game.category.accentColor, RoundedCornerShape(20.dp))
      .clickable { onClick() }
      .padding(14.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(game.category.accentColor.copy(alpha = 0.15f))
            .border(2.dp, game.category.accentColor, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = when (game.category) {
              GameCategory.REFLEX -> "⚡"
              GameCategory.LOGIC -> "🧠"
              GameCategory.MEMORY -> "👁️"
              GameCategory.ATTENTION -> "🎯"
            },
            fontSize = 24.sp
          )
        }

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = game.title,
            color = TextDark,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            softWrap = false,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
          )

          Spacer(modifier = Modifier.height(3.dp))

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            // Saniye Süresi Rozeti - Her zaman net ve görünür
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(game.category.accentColor.copy(alpha = 0.15f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "⏱️ ${game.defaultDurationSeconds} sn",
                color = game.category.accentColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                softWrap = false
              )
            }

            Text(
              text = game.category.title,
              color = TextDarkSecondary,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium,
              maxLines = 1,
              softWrap = false
            )
          }
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      // OYNA Butonu - Asla alt alta yazılmaz, net ve belirgin
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(Brush.horizontalGradient(listOf(BluePrimary, SkyBlueAccent)))
          .padding(horizontal = 14.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Text(
            text = "OYNA",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1,
            softWrap = false
          )
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Oyna",
            tint = Color.White,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}
