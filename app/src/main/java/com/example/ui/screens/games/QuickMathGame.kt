package com.example.ui.screens.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.VibrantGreen
import com.example.ui.theme.VibrantRed
import kotlin.random.Random

data class MathProblem(
  val prompt: String,
  val answer: Int
)

fun generateRandomMathProblem(level: Int): MathProblem {
  return when {
    level <= 2 -> {
      // Level 1: Simple Addition or Subtraction
      if (Random.nextBoolean()) {
        val a = Random.nextInt(11, 49)
        val b = Random.nextInt(9, 39)
        MathProblem("$a + $b = ?", a + b)
      } else {
        val a = Random.nextInt(25, 89)
        val b = Random.nextInt(10, a - 5)
        MathProblem("$a − $b = ?", a - b)
      }
    }
    level <= 5 -> {
      // Level 2: Multiplication or Division
      val op = Random.nextInt(3)
      when (op) {
        0 -> {
          val a = Random.nextInt(6, 12)
          val b = Random.nextInt(4, 9)
          MathProblem("$a × $b = ?", a * b)
        }
        1 -> {
          val divisor = Random.nextInt(3, 9)
          val quotient = Random.nextInt(4, 12)
          val dividend = divisor * quotient
          MathProblem("$dividend ÷ $divisor = ?", quotient)
        }
        else -> {
          val a = Random.nextInt(20, 60)
          val b = Random.nextInt(15, 45)
          MathProblem("$a + $b = ?", a + b)
        }
      }
    }
    else -> {
      // Level 3: Mixed Operations
      val op = Random.nextInt(3)
      when (op) {
        0 -> {
          val a = Random.nextInt(3, 9)
          val b = Random.nextInt(3, 8)
          val c = Random.nextInt(5, 25)
          MathProblem("$a × $b + $c = ?", (a * b) + c)
        }
        1 -> {
          val a = Random.nextInt(40, 80)
          val b = Random.nextInt(3, 7)
          val c = Random.nextInt(3, 6)
          MathProblem("$a − $b × $c = ?", a - (b * c))
        }
        else -> {
          val a = Random.nextInt(4, 9)
          val b = Random.nextInt(4, 9)
          val c = Random.nextInt(10, 30)
          MathProblem("$a × $b − $c = ?", (a * b) - c)
        }
      }
    }
  }
}

@Composable
fun QuickMathGame(
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
  var questionNumber by remember { mutableIntStateOf(1) }
  var currentProblem by remember { mutableStateOf(generateRandomMathProblem(1)) }
  var enteredInput by remember { mutableStateOf("") }
  var feedbackText by remember { mutableStateOf("") }
  var feedbackIsSuccess by remember { mutableStateOf(true) }

  fun checkAnswer() {
    if (enteredInput.isEmpty()) return
    val userVal = enteredInput.toIntOrNull()
    if (userVal == currentProblem.answer) {
      feedbackText = "✓ DOĞRU! (+120 Puan)"
      feedbackIsSuccess = true
      onRoundSuccess(120, 2.5f)
      questionNumber++
      currentProblem = generateRandomMathProblem(questionNumber)
      enteredInput = ""
    } else {
      feedbackText = "✗ YANLIŞ! Doğru: ${currentProblem.answer}"
      feedbackIsSuccess = false
      onRoundMistake(2.0f, "Hatalı cevap: $userVal, beklenen: ${currentProblem.answer}")
      enteredInput = ""
      if (!isRiskMode) {
        questionNumber++
        currentProblem = generateRandomMathProblem(questionNumber)
      }
    }
  }

  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    GameTopBar(
      title = "Hızlı Matematik",
      streak = streak,
      isRiskMode = isRiskMode,
      onBack = onBack
    )

    // Soru ve Combo Rozeti
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
          .padding(horizontal = 12.dp, vertical = 4.dp)
      ) {
        Text(
          text = "Soru $questionNumber",
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
          fontWeight = FontWeight.Black
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Soru Kartı
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = SkyBlueAccent.copy(alpha = 0.4f))
        .clip(RoundedCornerShape(24.dp))
        .background(CardWhite)
        .border(2.5.dp, BluePrimary, RoundedCornerShape(24.dp))
        .padding(vertical = 16.dp, horizontal = 20.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = currentProblem.prompt,
          color = TextDark,
          fontSize = 32.sp,
          fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (enteredInput.isEmpty()) Color(0xFFF1F5F9) else Color(0xFFEFF6FF))
            .border(
              1.5.dp,
              if (enteredInput.isEmpty()) Color(0xFFCBD5E1) else BluePrimary,
              RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 20.dp, vertical = 6.dp)
        ) {
          Text(
            text = if (enteredInput.isEmpty()) "Cevabı Girin..." else enteredInput,
            color = if (enteredInput.isEmpty()) TextDarkMuted else BluePrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Keypad (0-9, Sil, Tamam)
    Box(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 28.dp),
      contentAlignment = Alignment.Center
    ) {
      val keypadItems = listOf(
        "1", "2", "3",
        "4", "5", "6",
        "7", "8", "9",
        "⌫", "0", "OK"
      )

      LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(keypadItems.size) { index ->
          val item = keypadItems[index]
          val isSpecial = item == "⌫" || item == "OK"
          val isOk = item == "OK"

          Box(
            modifier = Modifier
              .size(64.dp)
              .shadow(
                6.dp,
                CircleShape,
                spotColor = if (isOk) VibrantGreen.copy(alpha = 0.5f) else Color(0x33000000)
              )
              .clip(CircleShape)
              .background(
                when {
                  isOk -> VibrantGreen
                  item == "⌫" -> Color(0xFFEF4444)
                  else -> CardWhite
                }
              )
              .border(
                width = 2.dp,
                color = when {
                  isOk -> Color.White
                  item == "⌫" -> Color.White
                  else -> SkyBlueAccent
                },
                shape = CircleShape
              )
              .clickable {
                when (item) {
                  "⌫" -> {
                    if (enteredInput.isNotEmpty()) {
                      enteredInput = enteredInput.dropLast(1)
                    }
                  }
                  "OK" -> {
                    checkAnswer()
                  }
                  else -> {
                    if (enteredInput.length < 5) {
                      enteredInput += item
                    }
                  }
                }
              },
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = item,
              color = if (isSpecial) Color.White else TextDark,
              fontSize = if (isSpecial) 18.sp else 24.sp,
              fontWeight = FontWeight.Black
            )
          }
        }
      }
    }

    // Alt Süre Göstergesi
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
