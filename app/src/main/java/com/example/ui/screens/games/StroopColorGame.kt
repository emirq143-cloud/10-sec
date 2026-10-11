package com.example.ui.screens.games

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CircularTimer
import com.example.ui.components.GameTopBar
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CardWhite
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.TextDark
import com.example.ui.theme.VibrantGreen
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantRed
import kotlin.random.Random

data class ColorItem(
  val displayName: String,
  val color: Color
)

val COLOR_POOL = listOf(
  ColorItem("Kırmızı", Color(0xFFEF4444)),
  ColorItem("Mavi", Color(0xFF3B82F6)),
  ColorItem("Yeşil", Color(0xFF10B981)),
  ColorItem("Sarı", Color(0xFFFBBF24)),
  ColorItem("Mor", Color(0xFFA855F7)),
  ColorItem("Turuncu", Color(0xFFF97316))
)

enum class StroopTargetRule {
  WORD_TEXT,  // Tap the color matching the text word
  INK_COLOR   // Tap the color matching the ink display color
}

data class StroopChallenge(
  val wordItem: ColorItem,
  val inkItem: ColorItem,
  val rule: StroopTargetRule,
  val targetColor: ColorItem,
  val options: List<ColorItem>
)

fun generateRandomStroop(): StroopChallenge {
  val pool = COLOR_POOL.shuffled()
  val wordItem = pool[0]
  val inkItem = pool[1] // Conflicting color!
  val rule = if (Random.nextBoolean()) StroopTargetRule.WORD_TEXT else StroopTargetRule.INK_COLOR
  val targetColor = if (rule == StroopTargetRule.WORD_TEXT) wordItem else inkItem

  // 4 selectable color options
  val otherOptions = COLOR_POOL.filter { it != targetColor }.shuffled().take(3)
  val options = (otherOptions + targetColor).shuffled()

  return StroopChallenge(wordItem, inkItem, rule, targetColor, options)
}

@Composable
fun StroopColorGame(
  remainingSeconds: Float,
  durationSeconds: Float,
  streak: Int,
  isRiskMode: Boolean,
  onBack: () -> Unit,
  onRoundSuccess: (scoreBonus: Int, timeBonus: Float) -> Unit = { _, _ -> },
  onRoundMistake: (timePenalty: Float, reason: String) -> Unit = { _, _ -> },
  onSuccess: (scoreBonus: Int, accuracy: Int) -> Unit = { _, _ -> },
  onFail: (reason: String) -> Unit = {}
) {
  var totalCompleted by remember { mutableIntStateOf(0) }
  var challenge by remember { mutableStateOf(generateRandomStroop()) }
  var feedbackText by remember { mutableStateOf("") }
  var feedbackIsSuccess by remember { mutableStateOf(true) }

  fun onOptionTapped(chosen: ColorItem) {
    if (chosen == challenge.targetColor) {
      totalCompleted++
      feedbackText = "✓ HARİKA! (+90)"
      feedbackIsSuccess = true
      onRoundSuccess(90, 1.8f)
      challenge = generateRandomStroop()
    } else {
      feedbackText = "✗ YANLIŞ RENK!"
      feedbackIsSuccess = false
      onRoundMistake(2.0f, "Hatalı renk seçtin! Doğru: ${challenge.targetColor.displayName}")
      if (!isRiskMode) {
        challenge = generateRandomStroop()
      }
    }
  }

  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    GameTopBar(
      title = "Renk Seçimi",
      streak = streak,
      isRiskMode = isRiskMode,
      onBack = onBack
    )

    // Skor ve Geri Bildirim
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(Color.Black.copy(alpha = 0.5f))
          .border(1.dp, SkyBlueAccent, RoundedCornerShape(12.dp))
          .padding(horizontal = 12.dp, vertical = 5.dp)
      ) {
        Text(
          text = "Tamamlanan: $totalCompleted",
          color = SkyBlueAccent,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )
      }

      if (feedbackText.isNotEmpty()) {
        Text(
          text = feedbackText,
          color = if (feedbackIsSuccess) VibrantGreen else VibrantRed,
          fontSize = 12.sp,
          fontWeight = FontWeight.Black,
          maxLines = 1,
          overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Kural İpucu Banner'ı
    val ruleBadge = if (challenge.rule == StroopTargetRule.WORD_TEXT) {
      "📖 KURAL: KELİMENİN ANLAMI"
    } else {
      "🎨 KURAL: YAZININ RENGİ"
    }

    val ruleInstruction = if (challenge.rule == StroopTargetRule.WORD_TEXT) {
      "Yazıda okuduğun rengi seç!"
    } else {
      "Kelimenin boyandığı rengi seç!"
    }

    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.padding(horizontal = 20.dp)
    ) {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(16.dp))
          .background(Color.Black.copy(alpha = 0.65f))
          .border(1.5.dp, NeonGold, RoundedCornerShape(16.dp))
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        Text(
          text = ruleBadge,
          color = NeonGold,
          fontSize = 13.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 0.5.sp
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = ruleInstruction,
        color = Color.White.copy(alpha = 0.9f),
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Stroop Çelişki Kartı
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = challenge.inkItem.color.copy(alpha = 0.5f))
        .clip(RoundedCornerShape(24.dp))
        .background(CardWhite)
        .border(3.dp, challenge.inkItem.color, RoundedCornerShape(24.dp))
        .padding(vertical = 20.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = challenge.wordItem.displayName.uppercase(java.util.Locale("tr", "TR")),
        color = challenge.inkItem.color,
        fontSize = 38.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 2.sp,
        maxLines = 1
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    Text(
      text = "AŞAĞIDAN DOĞRU RENGİ SEÇ",
      color = SkyBlueAccent,
      fontSize = 12.sp,
      fontWeight = FontWeight.Black,
      letterSpacing = 0.8.sp
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Renk Balonları (2x2 Grid)
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 28.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        ColorBalloon(item = challenge.options[0], modifier = Modifier.weight(1f)) {
          onOptionTapped(challenge.options[0])
        }
        ColorBalloon(item = challenge.options[1], modifier = Modifier.weight(1f)) {
          onOptionTapped(challenge.options[1])
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        ColorBalloon(item = challenge.options[2], modifier = Modifier.weight(1f)) {
          onOptionTapped(challenge.options[2])
        }
        ColorBalloon(item = challenge.options[3], modifier = Modifier.weight(1f)) {
          onOptionTapped(challenge.options[3])
        }
      }
    }

    Spacer(modifier = Modifier.weight(1f))

    // Alt Süre
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp),
      contentAlignment = Alignment.Center
    ) {
      CircularTimer(
        remainingSeconds = remainingSeconds,
        totalDurationSeconds = durationSeconds,
        size = 74.dp
      )
    }
  }
}

@Composable
private fun ColorBalloon(
  item: ColorItem,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .height(64.dp)
      .shadow(8.dp, RoundedCornerShape(18.dp), spotColor = item.color.copy(alpha = 0.5f))
      .clip(RoundedCornerShape(18.dp))
      .background(item.color)
      .border(2.5.dp, Color.White, RoundedCornerShape(18.dp))
      .clickable { onClick() }
      .padding(horizontal = 8.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Box(
        modifier = Modifier
          .size(14.dp)
          .clip(CircleShape)
          .background(Color.White)
      )
      Text(
        text = item.displayName,
        color = Color.White,
        fontSize = 15.sp,
        fontWeight = FontWeight.Black,
        maxLines = 1
      )
    }
  }
}
