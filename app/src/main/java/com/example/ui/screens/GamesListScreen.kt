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

@Composable
fun GamesListScreen(
  onSelectGame: (GameType) -> Unit,
  modifier: Modifier = Modifier
) {
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
        text = "🎮 Mini Oyunlar",
        color = Color.White,
        fontSize = 24.sp,
        fontWeight = FontWeight.ExtraBold
      )

      Text(
        text = "Refleks, zeka, hafıza ve odaklanma antrenmanı",
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
            label = { Text("Tümü", fontWeight = FontWeight.Bold) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = BluePrimary,
              selectedLabelColor = Color.White,
              containerColor = CardFrosted,
              labelColor = TextDark
            )
          )
        }
        items(GameCategory.values()) { category ->
          FilterChip(
            selected = selectedCategory == category,
            onClick = { selectedCategory = if (selectedCategory == category) null else category },
            label = { Text(category.title, fontWeight = FontWeight.Bold) },
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
      .padding(16.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(50.dp)
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
            fontSize = 26.sp
          )
        }

        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = game.title,
              color = TextDark,
              fontSize = 17.sp,
              fontWeight = FontWeight.ExtraBold
            )

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(game.category.accentColor.copy(alpha = 0.15f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "${game.defaultDurationSeconds}s",
                color = game.category.accentColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Text(
            text = game.subtitle,
            color = TextDarkSecondary,
            fontSize = 12.sp,
            maxLines = 1
          )
        }
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(Brush.horizontalGradient(listOf(BluePrimary, SkyBlueAccent)))
          .padding(horizontal = 12.dp, vertical = 8.dp),
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
            fontWeight = FontWeight.Black
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
