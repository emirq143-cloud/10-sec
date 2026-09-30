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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
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
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextWhiteTranslucent
import com.example.ui.theme.VibrantGreen

data class MathProblem(
  val prompt: String,
  val answer: Int
)

@Composable
fun QuickMathGame(
  remainingSeconds: Float,
  durationSeconds: Float,
  streak: Int,
  isRiskMode: Boolean,
  onBack: () -> Unit,
  onSuccess: (scoreBonus: Int, accuracy: Int) -> Unit,
  onFail: (reason: String) -> Unit
) {
  val problems = remember {
    listOf(
      MathProblem("17 × 4 − 9 = ?", 59),
      MathProblem("24 × 3 − 18 = ?", 54),
      MathProblem("35 + 8 × 6 = ?", 83),
      MathProblem("16 × 5 − 27 = ?", 53),
      MathProblem("48 ÷ 4 + 29 = ?", 41)
    )
  }

  var currentProblemIndex by remember { mutableIntStateOf(0) }
  var enteredInput by remember { mutableStateOf("") }
  val problem = problems[currentProblemIndex]

  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    GameTopBar(
      title = "Matematik",
      streak = streak,
      isRiskMode = isRiskMode,
      onBack = onBack
    )

    Text(
      text = "5 saniye içinde çöz!",
      color = TextWhiteTranslucent,
      fontSize = 13.sp
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Crisp Question Card with Distinct Glowing Border
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = SkyBlueAccent.copy(alpha = 0.4f))
        .clip(RoundedCornerShape(24.dp))
        .background(CardWhite)
        .border(2.5.dp, BluePrimary, RoundedCornerShape(24.dp))
        .padding(vertical = 20.dp, horizontal = 20.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = problem.prompt,
          color = TextDark,
          fontSize = 32.sp,
          fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(6.dp))
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (enteredInput.isEmpty()) Color(0xFFF1F5F9) else Color(0xFFEFF6FF))
            .border(1.5.dp, if (enteredInput.isEmpty()) Color(0xFFCBD5E1) else BluePrimary, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
          Text(
            text = if (enteredInput.isEmpty()) "Cevap Bekleniyor..." else "Girdiğin: $enteredInput",
            color = if (enteredInput.isEmpty()) Color(0xFF64748B) else BluePrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Clear instruction tag pointing to keypad
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(Color.Black.copy(alpha = 0.5f))
        .border(1.dp, SkyBlueAccent.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
        .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
      Text(
        text = "👇 CEVABI TUŞLAYIP ONAYLA 👇",
        color = SkyBlueAccent,
        fontSize = 12.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.sp
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Keypad: 1 to 9, Backspace, 0, Check (High-contrast, clearly selectable keys)
    Box(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 30.dp),
      contentAlignment = Alignment.Center
    ) {
      val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "DEL", "0", "OK")

      LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(keys.size) { index ->
          val key = keys[index]
          val isOk = key == "OK"
          val isDel = key == "DEL"

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(58.dp)
              .shadow(
                elevation = if (isOk) 10.dp else 6.dp,
                shape = RoundedCornerShape(18.dp),
                spotColor = if (isOk) VibrantGreen.copy(alpha = 0.5f) else Color(0x33000000)
              )
              .clip(RoundedCornerShape(18.dp))
              .background(
                when {
                  isOk -> VibrantGreen
                  isDel -> Color(0xFFFEE2E2)
                  else -> CardWhite
                }
              )
              .border(
                width = 2.dp,
                color = when {
                  isOk -> Color(0xFF34D399)
                  isDel -> Color(0xFFEF4444)
                  else -> Color(0xFF38BDF8)
                },
                shape = RoundedCornerShape(18.dp)
              )
              .clickable {
                when (key) {
                  "DEL" -> {
                    if (enteredInput.isNotEmpty()) enteredInput = enteredInput.dropLast(1)
                  }
                  "OK" -> {
                    val parsed = enteredInput.toIntOrNull()
                    if (parsed == problem.answer) {
                      onSuccess(100, 100)
                    } else {
                      onFail("Yanlış cevap! Doğru cevap: ${problem.answer}")
                    }
                  }
                  else -> {
                    if (enteredInput.length < 4) {
                      enteredInput += key
                      // Instant auto-check if user typed exact answer
                      if (enteredInput.toIntOrNull() == problem.answer) {
                        onSuccess(100, 100)
                      }
                    }
                  }
                }
              },
            contentAlignment = Alignment.Center
          ) {
            when (key) {
              "DEL" -> Icon(
                imageVector = Icons.Default.Backspace,
                contentDescription = "Sil",
                tint = Color(0xFFDC2626),
                modifier = Modifier.size(24.dp)
              )
              "OK" -> Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = "Onayla",
                  tint = Color.White,
                  modifier = Modifier.size(26.dp)
                )
              }
              else -> Text(
                text = key,
                color = TextDark,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
              )
            }
          }
        }
      }
    }

    // Circular Timer at the bottom over mountain backdrop
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 20.dp),
      contentAlignment = Alignment.Center
    ) {
      CircularTimer(
        remainingSeconds = remainingSeconds,
        totalDurationSeconds = durationSeconds,
        size = 78.dp
      )
    }
  }
}
