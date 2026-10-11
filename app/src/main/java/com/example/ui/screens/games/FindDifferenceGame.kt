package com.example.ui.screens.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CircularTimer
import com.example.ui.components.GameTopBar
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CyanBright
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.TextDark
import com.example.ui.theme.VibrantGreen
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantRed
import kotlin.math.hypot
import kotlin.random.Random

data class DiffSpot(
  val targetX: Float,
  val targetY: Float,
  val targetRadius: Float,
  val differenceHint: String
)

enum class SceneTheme(
  val title: String,
  val spotA: DiffSpot,
  val spotB: DiffSpot
) {
  SPACE(
    "🚀 Uzay & Gezegen",
    DiffSpot(0.72f, 0.32f, 0.22f, "Gezegen halkası ve rengi"),
    DiffSpot(0.53f, 0.65f, 0.20f, "Roket motor alevi")
  ),
  CITY(
    "🌃 Gece Şehri & Kule",
    DiffSpot(0.45f, 0.28f, 0.20f, "Kule tepesindeki sinyal ışığı"),
    DiffSpot(0.85f, 0.20f, 0.20f, "Gökyüzündeki ayın rengi")
  ),
  OCEAN(
    "🏝️ Korsan Adası & Deniz",
    DiffSpot(0.25f, 0.78f, 0.20f, "Kumsaldaki deniz canlısı"),
    DiffSpot(0.75f, 0.68f, 0.20f, "Yelkenli korsan gemisinin yelkeni")
  ),
  CAMP(
    "🏕️ Dağ Kampı & Çadır",
    DiffSpot(0.63f, 0.68f, 0.20f, "Kamp ateşinin alev rengi"),
    DiffSpot(0.45f, 0.65f, 0.20f, "Kamp çadırının rengi")
  ),
  CASTLE(
    "🏰 Kraliyet Şatosu",
    DiffSpot(0.50f, 0.30f, 0.20f, "Orta kuledeki krallık bayrağı"),
    DiffSpot(0.18f, 0.38f, 0.20f, "Sol gözetleme kulesi çatısı")
  ),
  RACETRACK(
    "🏎️ Hızlı Yarış Pisti",
    DiffSpot(0.70f, 0.65f, 0.20f, "Yarış otomobilinin arka rüzgarlığı"),
    DiffSpot(0.35f, 0.45f, 0.20f, "Pist ortasındaki yarış bayrağı")
  ),
  DESERT(
    "🏜️ Mısır Piramitleri",
    DiffSpot(0.52f, 0.48f, 0.22f, "Büyük piramidin altın zirve taşı"),
    DiffSpot(0.82f, 0.18f, 0.20f, "Çöl güneşinin ışıltısı")
  ),
  LAB(
    "🔬 Siber Laboratuvar",
    DiffSpot(0.76f, 0.60f, 0.20f, "Deney tüpünün parlak kimyasal sıvısı"),
    DiffSpot(0.26f, 0.52f, 0.20f, "Robotun sibernetik göz rengi")
  ),
  JUNGLE(
    "🌴 Tropik Yağmur Ormanı",
    DiffSpot(0.35f, 0.38f, 0.20f, "Ağaç dalındaki egzotik papağan"),
    DiffSpot(0.75f, 0.72f, 0.20f, "Nehir kenarındaki nilüfer çiçeği")
  ),
  ARCTIC(
    "❄️ Kutup & Buzul Dağı",
    DiffSpot(0.30f, 0.72f, 0.20f, "Buz kütlesi üzerindeki penguen"),
    DiffSpot(0.70f, 0.22f, 0.20f, "Kutup ışıklarının (Aurora) ışıltısı")
  ),
  BALLOON(
    "🎈 Gökyüzü & Sıcak Balon",
    DiffSpot(0.40f, 0.35f, 0.22f, "Büyük balonun desen şeridi"),
    DiffSpot(0.78f, 0.48f, 0.20f, "Bulutun arkasındaki göçmen kuşlar")
  ),
  UNDERWATER(
    "🐠 Derin Deniz & Mercan",
    DiffSpot(0.25f, 0.40f, 0.20f, "Mercanların arasındaki denizanası"),
    DiffSpot(0.72f, 0.74f, 0.20f, "Deniz tabanındaki hazine sandığı")
  ),
  VOLCANO(
    "🌋 Yanardağ & Ateş Dağı",
    DiffSpot(0.50f, 0.32f, 0.22f, "Kraterden fışkıran lav dumanı"),
    DiffSpot(0.25f, 0.68f, 0.20f, "Yamaçtan akan parlak magma")
  ),
  ARCADE(
    "🕹️ Retro Oyun Salonu",
    DiffSpot(0.40f, 0.48f, 0.22f, "Atari ekranındaki piksel karakter"),
    DiffSpot(0.75f, 0.35f, 0.20f, "Neon rekor skoru tabelası")
  ),
  FARM(
    "🌻 Köy Çiftliği & Değirmen",
    DiffSpot(0.70f, 0.40f, 0.22f, "Yel değirmeninin dönen kanatları"),
    DiffSpot(0.28f, 0.75f, 0.20f, "Çiftlik çitindeki sarı ayçiçeği")
  ),
  AIRPORT(
    "✈️ Havaalanı & Jet Uçak",
    DiffSpot(0.48f, 0.52f, 0.22f, "Pistteki jet uçağın kuyruk amblemi"),
    DiffSpot(0.80f, 0.28f, 0.20f, "Kontrol kulesinin radar lambası")
  ),
  PIRATE_SHIP(
    "🏴‍☠️ Korsan Gemisi & Hazine",
    DiffSpot(0.42f, 0.40f, 0.22f, "Gemi ana yelkenindeki kurukafa amblemi"),
    DiffSpot(0.75f, 0.68f, 0.20f, "Güvertedeki altın hazine sandığı")
  ),
  SUBMARINE(
    "🌊 Derin Deniz & Denizaltı",
    DiffSpot(0.50f, 0.48f, 0.22f, "Denizaltının periskop projektör ışığı"),
    DiffSpot(0.22f, 0.65f, 0.20f, "Mercan kayalığındaki ahtapot kolu")
  ),
  TRAIN_BRIDGE(
    "🚂 Dağ Köprüsü & Buharlı Tren",
    DiffSpot(0.46f, 0.45f, 0.22f, "Lokomotifin baca dumanı rengi"),
    DiffSpot(0.80f, 0.65f, 0.20f, "Viyadük kemerindeki nehir dalgası")
  ),
  FERRIS_WHEEL(
    "🎡 Lunapark & Dönme Dolap",
    DiffSpot(0.50f, 0.35f, 0.22f, "Dönme dolabın en üst kabin rengi"),
    DiffSpot(0.24f, 0.68f, 0.20f, "Lunapark bilet kulübesinin ışığı")
  ),
  WINDMILL_TULIPS(
    "🌷 Lale Vadisi & Değirmen",
    DiffSpot(0.35f, 0.74f, 0.22f, "Ön sıradaki parlak lale çiçeği"),
    DiffSpot(0.70f, 0.42f, 0.20f, "Yel değirmeninin pencere panjuru")
  ),
  POLAR_IGLOO(
    "⛺ Buzul İglo & Kutup Ayısı",
    DiffSpot(0.38f, 0.65f, 0.22f, "İglo giriş kapısının buz kemeri"),
    DiffSpot(0.74f, 0.58f, 0.20f, "Kutup ayısının sevimli kış beresi")
  ),
  CYBER_HIGHWAY(
    "🤖 Siber Şehir & Uçan Araç",
    DiffSpot(0.52f, 0.48f, 0.22f, "Uçan aracın neon itici alevi"),
    DiffSpot(0.82f, 0.25f, 0.20f, "Gökdelendeki siber hologram logo")
  ),
  MEDIEVAL_MARKET(
    "🎪 Ortaçağ Pazarı & Çadırlar",
    DiffSpot(0.38f, 0.58f, 0.22f, "Kraliyet pazar çadırının flaması"),
    DiffSpot(0.72f, 0.66f, 0.20f, "Meyve tezgahındaki altın elma sepeti")
  ),
  SAFARI_SUNSET(
    "🦒 Afrika Safarisi & Zürafa",
    DiffSpot(0.32f, 0.52f, 0.22f, "Zürafanın sırt deseni ve kulakları"),
    DiffSpot(0.78f, 0.32f, 0.20f, "Akasya ağacındaki egzotik kuş")
  ),
  HAUNTED_MANOR(
    "🏰 Gizemli Malikane & Gece",
    DiffSpot(0.50f, 0.36f, 0.22f, "Tavan arası odasının gizemli ışığı"),
    DiffSpot(0.24f, 0.22f, 0.20f, "Dolunayın önünden geçen yarasa")
  ),
  SKI_SLOPE(
    "⛷️ Karlı Zirve & Kayak Pisti",
    DiffSpot(0.45f, 0.62f, 0.22f, "Kayakçının kayak montu ve gözlüğü"),
    DiffSpot(0.80f, 0.40f, 0.20f, "Dağdaki teleferik kabininin rengi")
  ),
  LIGHTHOUSE(
    "🗼 Fırtınalı Deniz & Deniz Feneri",
    DiffSpot(0.52f, 0.32f, 0.22f, "Fener kulesinin dönen ışık demeti"),
    DiffSpot(0.22f, 0.68f, 0.20f, "Kayalara çarpan dev fırtına dalgası")
  )
}

@Composable
fun FindDifferenceGame(
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
  var round by remember { mutableIntStateOf(1) }
  val shuffledThemes = remember { SceneTheme.values().toList().shuffled() }
  var themeIndex by remember { mutableIntStateOf(0) }
  var variationIndex by remember { mutableIntStateOf(Random.nextInt(2)) }

  val currentTheme = shuffledThemes[themeIndex % shuffledThemes.size]
  val currentSpot = if (variationIndex == 0) currentTheme.spotA else currentTheme.spotB

  var feedbackText by remember { mutableStateOf("") }
  var feedbackIsSuccess by remember { mutableStateOf(true) }
  var lastTapOffset by remember { mutableStateOf<Offset?>(null) }
  var correctCount by remember { mutableIntStateOf(0) }
  var isProcessingTap by remember { mutableStateOf(false) }

  fun handleBottomPanelTap(normX: Float, normY: Float, pixelOffset: Offset) {
    if (isProcessingTap) return
    isProcessingTap = true
    lastTapOffset = pixelOffset

    val dist = hypot((normX - currentSpot.targetX).toDouble(), (normY - currentSpot.targetY).toDouble())
    if (dist <= currentSpot.targetRadius) {
      // Correct! Farkı buldu
      correctCount++
      feedbackText = "✓ FARKI BULDUN! (+120 Puan)"
      feedbackIsSuccess = true
      onRoundSuccess(120, 0f)

      round++
      themeIndex++
      variationIndex = Random.nextInt(2)
      isProcessingTap = false
    } else {
      // Wrong! Doğrudan yanlış ve diğer soruya geç
      feedbackText = "✗ YANLIŞ! Doğrudan diğer soruya geçiliyor..."
      feedbackIsSuccess = false
      onRoundMistake(0f, "Yanlış yere dokundun! Fark: ${currentSpot.differenceHint}")

      if (!isRiskMode) {
        round++
        themeIndex++
        variationIndex = Random.nextInt(2)
      }
      isProcessingTap = false
    }
  }

  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    GameTopBar(
      title = "Görsel Farkı Bul",
      streak = streak,
      isRiskMode = isRiskMode,
      onBack = onBack
    )

    // Tur ve Tema Başlığı
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(Color.Black.copy(alpha = 0.55f))
          .border(1.2.dp, SkyBlueAccent, RoundedCornerShape(12.dp))
          .padding(horizontal = 12.dp, vertical = 5.dp)
      ) {
        Text(
          text = "${currentTheme.title} (Soru $round)",
          color = SkyBlueAccent,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          softWrap = false
        )
      }

      if (feedbackText.isNotEmpty()) {
        Text(
          text = feedbackText,
          color = if (feedbackIsSuccess) VibrantGreen else VibrantRed,
          fontSize = 11.sp,
          fontWeight = FontWeight.Black,
          maxLines = 1,
          softWrap = false
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Üst Panel: Orijinal Resim (Referans)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(152.dp)
        .padding(horizontal = 20.dp)
        .shadow(8.dp, RoundedCornerShape(18.dp), spotColor = Color(0x33000000))
        .clip(RoundedCornerShape(18.dp))
        .background(Color(0xFF0F172A))
        .border(2.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
    ) {
      ScenicThemeCanvas(theme = currentTheme, variationIndex = variationIndex, hasDifference = false)
      Box(
        modifier = Modifier
          .align(Alignment.TopStart)
          .padding(8.dp)
          .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(6.dp))
          .padding(horizontal = 8.dp, vertical = 3.dp)
      ) {
        Text(text = "Orijinal Resim", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Talimat
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(12.dp))
        .background(NeonOrange.copy(alpha = 0.22f))
        .border(1.5.dp, NeonOrange, RoundedCornerShape(12.dp))
        .padding(horizontal = 14.dp, vertical = 3.dp)
    ) {
      Text(
        text = "Farklı Olanı Bul",
        color = NeonOrange,
        fontSize = 11.sp,
        fontWeight = FontWeight.Black,
        maxLines = 1,
        softWrap = false
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Alt Panel: Farklı Olan Resim (Dokunulabilir)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(152.dp)
        .padding(horizontal = 20.dp)
        .shadow(12.dp, RoundedCornerShape(18.dp), spotColor = NeonOrange.copy(alpha = 0.4f))
        .clip(RoundedCornerShape(18.dp))
        .background(Color(0xFF0F172A))
        .border(2.5.dp, NeonOrange, RoundedCornerShape(18.dp))
        .pointerInput(currentTheme, round, variationIndex) {
          detectTapGestures { offset ->
            val normX = offset.x / size.width
            val normY = offset.y / size.height
            handleBottomPanelTap(normX, normY, offset)
          }
        }
    ) {
      ScenicThemeCanvas(theme = currentTheme, variationIndex = variationIndex, hasDifference = true)
      Box(
        modifier = Modifier
          .align(Alignment.TopStart)
          .padding(8.dp)
          .background(NeonOrange, RoundedCornerShape(6.dp))
          .padding(horizontal = 8.dp, vertical = 3.dp)
      ) {
        Text(text = "🔍 FARK BURADA (DOKUN)", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black, maxLines = 1, softWrap = false)
      }

      // Tap indicator highlight
      lastTapOffset?.let { offset ->
        Canvas(modifier = Modifier.fillMaxSize()) {
          drawCircle(
            color = if (feedbackIsSuccess) VibrantGreen.copy(alpha = 0.8f) else VibrantRed.copy(alpha = 0.8f),
            radius = 16.dp.toPx(),
            center = offset
          )
        }
      }
    }

    Spacer(modifier = Modifier.weight(1f))

    // Alt Süre Çemberi
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 14.dp),
      contentAlignment = Alignment.Center
    ) {
      CircularTimer(
        remainingSeconds = remainingSeconds,
        totalDurationSeconds = durationSeconds,
        size = 72.dp
      )
    }
  }
}

@Composable
fun ScenicThemeCanvas(theme: SceneTheme, variationIndex: Int, hasDifference: Boolean) {
  Canvas(modifier = Modifier.fillMaxSize()) {
    val w = size.width
    val h = size.height

    when (theme) {
      SceneTheme.SPACE -> {
        // Space scene
        drawRect(color = Color(0xFF030712), size = Size(w, h))

        // Stars
        drawCircle(Color.White, radius = 2.dp.toPx(), center = Offset(w * 0.12f, h * 0.20f))
        drawCircle(Color.White, radius = 3.dp.toPx(), center = Offset(w * 0.42f, h * 0.15f))
        drawCircle(Color.White, radius = 2.dp.toPx(), center = Offset(w * 0.88f, h * 0.40f))

        // Earth Planet
        drawCircle(Color(0xFF2563EB), radius = 24.dp.toPx(), center = Offset(w * 0.22f, h * 0.40f))
        drawCircle(Color(0xFF10B981), radius = 10.dp.toPx(), center = Offset(w * 0.20f, h * 0.38f))

        // Rocket body
        val rocketPath = Path().apply {
          moveTo(w * 0.48f, h * 0.70f)
          lineTo(w * 0.53f, h * 0.55f)
          lineTo(w * 0.58f, h * 0.70f)
          close()
        }
        drawPath(rocketPath, color = Color(0xFFE2E8F0))

        // Variation 1 Difference: Rocket engine plume flame at (0.53, 0.65)
        val flameColor = if (hasDifference && variationIndex == 1) CyanBright else Color(0xFFEF4444)
        drawCircle(flameColor, radius = 8.dp.toPx(), center = Offset(w * 0.53f, h * 0.72f))

        // Saturn Planet & Ring
        // Variation 0 Difference: Saturn Ring color at (0.72, 0.32)
        val saturnColor = if (hasDifference && variationIndex == 0) NeonOrange else NeonGold
        drawCircle(saturnColor, radius = 20.dp.toPx(), center = Offset(w * 0.72f, h * 0.32f))
        drawOval(
          color = if (hasDifference && variationIndex == 0) Color.White else Color(0xFFD97706),
          topLeft = Offset(w * 0.60f, h * 0.30f),
          size = Size(w * 0.24f, 10.dp.toPx())
        )
      }

      SceneTheme.CITY -> {
        // Night City skyline
        drawRect(color = Color(0xFF0F172A), size = Size(w, h))

        // Moon
        // Variation 1 Difference: Moon color/shape at (0.85, 0.20)
        val moonColor = if (hasDifference && variationIndex == 1) VibrantPink else Color(0xFFFDE68A)
        drawCircle(moonColor, radius = 16.dp.toPx(), center = Offset(w * 0.85f, h * 0.20f))

        // Skyscrapers
        drawRect(Color(0xFF1E293B), topLeft = Offset(w * 0.08f, h * 0.40f), size = Size(w * 0.22f, h * 0.60f))
        drawRect(Color(0xFF334155), topLeft = Offset(w * 0.35f, h * 0.28f), size = Size(w * 0.26f, h * 0.72f))
        drawRect(Color(0xFF1E293B), topLeft = Offset(w * 0.66f, h * 0.45f), size = Size(w * 0.26f, h * 0.55f))

        // Windows
        drawRect(Color(0xFFFBBF24), topLeft = Offset(w * 0.13f, h * 0.48f), size = Size(8.dp.toPx(), 8.dp.toPx()))
        drawRect(Color(0xFFFBBF24), topLeft = Offset(w * 0.18f, h * 0.60f), size = Size(8.dp.toPx(), 8.dp.toPx()))
        drawRect(Color(0xFFFBBF24), topLeft = Offset(w * 0.42f, h * 0.45f), size = Size(10.dp.toPx(), 10.dp.toPx()))

        // Radio Tower
        // Variation 0 Difference: Radio Tower Light at (0.45, 0.28)
        val towerLightColor = if (hasDifference && variationIndex == 0) Color(0xFFEF4444) else CyanBright
        drawCircle(towerLightColor, radius = 7.dp.toPx(), center = Offset(w * 0.45f, h * 0.26f))
        drawLine(
          color = Color(0xFF94A3B8),
          start = Offset(w * 0.45f, h * 0.26f),
          end = Offset(w * 0.45f, h * 0.28f),
          strokeWidth = 3.dp.toPx()
        )
      }

      SceneTheme.OCEAN -> {
        // Ocean & Island
        drawRect(color = Color(0xFF38BDF8), size = Size(w, h * 0.55f)) // Sky
        drawRect(color = Color(0xFF0284C7), topLeft = Offset(0f, h * 0.55f), size = Size(w, h * 0.45f)) // Water
        drawCircle(Color(0xFFF59E0B), radius = 18.dp.toPx(), center = Offset(w * 0.85f, h * 0.22f)) // Sun

        // Island Sand
        drawOval(Color(0xFFFDE68A), topLeft = Offset(w * 0.05f, h * 0.65f), size = Size(w * 0.45f, h * 0.30f))

        // Palm Tree Trunk
        val trunk = Path().apply {
          moveTo(w * 0.20f, h * 0.70f)
          lineTo(w * 0.24f, h * 0.45f)
          lineTo(w * 0.28f, h * 0.70f)
          close()
        }
        drawPath(trunk, color = Color(0xFF78350F))
        drawCircle(Color(0xFF16A34A), radius = 18.dp.toPx(), center = Offset(w * 0.24f, h * 0.42f))

        // Ship
        val ship = Path().apply {
          moveTo(w * 0.65f, h * 0.65f)
          lineTo(w * 0.85f, h * 0.65f)
          lineTo(w * 0.78f, h * 0.72f)
          lineTo(w * 0.68f, h * 0.72f)
          close()
        }
        drawPath(ship, color = Color(0xFF451A03))

        // Variation 1 Difference: Ship sail color at (0.75, 0.68)
        val sailColor = if (hasDifference && variationIndex == 1) Color(0xFF10B981) else Color.White
        drawCircle(sailColor, radius = 9.dp.toPx(), center = Offset(w * 0.75f, h * 0.58f))

        // Variation 0 Difference: Starfish/Crab on island sand at (0.25, 0.78)
        val creatureColor = if (hasDifference && variationIndex == 0) Color(0xFFEF4444) else Color(0xFFF59E0B)
        drawCircle(creatureColor, radius = 7.dp.toPx(), center = Offset(w * 0.25f, h * 0.78f))
      }

      SceneTheme.CAMP -> {
        // Mountain & Campfire
        drawRect(color = Color(0xFF1E293B), size = Size(w, h * 0.65f)) // Sky
        drawRect(color = Color(0xFF0F172A), topLeft = Offset(0f, h * 0.65f), size = Size(w, h * 0.35f)) // Ground

        // Mountains
        val mtn = Path().apply {
          moveTo(0f, h * 0.65f)
          lineTo(w * 0.30f, h * 0.30f)
          lineTo(w * 0.60f, h * 0.65f)
          close()
        }
        drawPath(mtn, color = Color(0xFF334155))

        // Pine Tree
        drawRect(Color(0xFF14532D), topLeft = Offset(w * 0.18f, h * 0.48f), size = Size(16.dp.toPx(), 36.dp.toPx()))

        // Tent
        // Variation 1 Difference: Tent color at (0.45, 0.65)
        val tentColor = if (hasDifference && variationIndex == 1) NeonOrange else Color(0xFF2563EB)
        val tent = Path().apply {
          moveTo(w * 0.35f, h * 0.75f)
          lineTo(w * 0.45f, h * 0.55f)
          lineTo(w * 0.55f, h * 0.75f)
          close()
        }
        drawPath(tent, color = tentColor)

        // Variation 0 Difference: Campfire sparks/color at (0.63, 0.68)
        val flameColor = if (hasDifference && variationIndex == 0) Color(0xFF10B981) else Color(0xFFF97316)
        val campfire = Path().apply {
          moveTo(w * 0.60f, h * 0.74f)
          lineTo(w * 0.63f, h * 0.66f)
          lineTo(w * 0.66f, h * 0.74f)
          close()
        }
        drawPath(campfire, color = flameColor)
        drawCircle(if (hasDifference && variationIndex == 0) NeonGold else Color(0xFFEF4444), radius = 5.dp.toPx(), center = Offset(w * 0.63f, h * 0.68f))
      }

      SceneTheme.CASTLE -> {
        // Royal Castle
        drawRect(color = Color(0xFF312E81), size = Size(w, h * 0.65f)) // Sky
        drawRect(color = Color(0xFF1E1B4B), topLeft = Offset(0f, h * 0.65f), size = Size(w, h * 0.35f)) // Ground

        // Castle Wall
        drawRect(Color(0xFF475569), topLeft = Offset(w * 0.20f, h * 0.45f), size = Size(w * 0.60f, h * 0.35f))

        // Left Tower
        // Variation 1 Difference: Left watchtower roof at (0.18, 0.38)
        val leftRoofColor = if (hasDifference && variationIndex == 1) NeonGold else Color(0xFF2563EB)
        drawRect(Color(0xFF334155), topLeft = Offset(w * 0.16f, h * 0.35f), size = Size(w * 0.14f, h * 0.45f))
        drawCircle(leftRoofColor, radius = 9.dp.toPx(), center = Offset(w * 0.23f, h * 0.35f))

        // Right Tower
        drawRect(Color(0xFF334155), topLeft = Offset(w * 0.70f, h * 0.35f), size = Size(w * 0.14f, h * 0.45f))
        // Center Tower
        drawRect(Color(0xFF1E293B), topLeft = Offset(w * 0.44f, h * 0.30f), size = Size(w * 0.12f, h * 0.50f))

        // Variation 0 Difference: Center tower flag at (0.50, 0.30)
        val flagColor = if (hasDifference && variationIndex == 0) Color(0xFFEC4899) else NeonGold
        val flagPath = Path().apply {
          moveTo(w * 0.50f, h * 0.24f)
          lineTo(w * 0.57f, h * 0.28f)
          lineTo(w * 0.50f, h * 0.32f)
          close()
        }
        drawPath(flagPath, color = flagColor)
        drawLine(Color.White, start = Offset(w * 0.50f, h * 0.22f), end = Offset(w * 0.50f, h * 0.34f), strokeWidth = 3f)
      }

      SceneTheme.RACETRACK -> {
        // Racetrack
        drawRect(color = Color(0xFF15803D), size = Size(w, h)) // Green grass
        drawOval(Color(0xFF334155), topLeft = Offset(w * 0.08f, h * 0.25f), size = Size(w * 0.84f, h * 0.60f)) // Asphalt track
        drawOval(Color(0xFF15803D), topLeft = Offset(w * 0.22f, h * 0.38f), size = Size(w * 0.56f, h * 0.34f)) // Infield

        // Variation 1 Difference: Checkered flag in infield at (0.35, 0.45)
        val flagColor = if (hasDifference && variationIndex == 1) Color(0xFFFBBF24) else Color.White
        drawRect(flagColor, topLeft = Offset(w * 0.32f, h * 0.42f), size = Size(w * 0.06f, 8.dp.toPx()))

        // Race car body
        drawRoundRect(
          color = Color(0xFFDC2626),
          topLeft = Offset(w * 0.58f, h * 0.68f),
          size = Size(w * 0.20f, h * 0.12f),
          cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
        )
        // Wheels
        drawCircle(Color.Black, radius = 5.dp.toPx(), center = Offset(w * 0.62f, h * 0.80f))
        drawCircle(Color.Black, radius = 5.dp.toPx(), center = Offset(w * 0.74f, h * 0.80f))

        // Variation 0 Difference: Racing Spoiler at (0.70, 0.65)
        val spoilerColor = if (hasDifference && variationIndex == 0) NeonGold else Color(0xFF1E293B)
        drawRect(
          color = spoilerColor,
          topLeft = Offset(w * 0.70f, h * 0.63f),
          size = Size(w * 0.08f, 6.dp.toPx())
        )
      }

      SceneTheme.DESERT -> {
        // Egyptian Desert & Pyramids
        drawRect(color = Color(0xFFFBBF24), size = Size(w, h * 0.50f)) // Orange/Yellow Sky
        drawRect(color = Color(0xFFD97706), topLeft = Offset(0f, h * 0.50f), size = Size(w, h * 0.50f)) // Sand

        // Variation 1 Difference: Sun glow at (0.82, 0.18)
        val sunColor = if (hasDifference && variationIndex == 1) VibrantPink else Color(0xFFEF4444)
        drawCircle(sunColor, radius = 22.dp.toPx(), center = Offset(w * 0.82f, h * 0.18f))

        // Left Pyramid
        val pyr1 = Path().apply {
          moveTo(w * 0.10f, h * 0.60f)
          lineTo(w * 0.32f, h * 0.35f)
          lineTo(w * 0.54f, h * 0.60f)
          close()
        }
        drawPath(pyr1, color = Color(0xFFB45309))

        // Main Pyramid
        val pyr2 = Path().apply {
          moveTo(w * 0.34f, h * 0.65f)
          lineTo(w * 0.56f, h * 0.32f)
          lineTo(w * 0.78f, h * 0.65f)
          close()
        }
        drawPath(pyr2, color = Color(0xFF92400E))

        // Variation 0 Difference: Golden Capstone on Main Pyramid at (0.52, 0.48)
        val capColor = if (hasDifference && variationIndex == 0) CyanBright else NeonGold
        val capPath = Path().apply {
          moveTo(w * 0.52f, h * 0.38f)
          lineTo(w * 0.56f, h * 0.32f)
          lineTo(w * 0.60f, h * 0.38f)
          close()
        }
        drawPath(capPath, color = capColor)
      }

      SceneTheme.LAB -> {
        // High-tech Sci-Fi Lab
        drawRect(color = Color(0xFF020617), size = Size(w, h))

        // Grid lines
        drawLine(Color(0xFF1E293B), start = Offset(0f, h * 0.60f), end = Offset(w, h * 0.60f), strokeWidth = 2f)
        drawLine(Color(0xFF1E293B), start = Offset(w * 0.40f, 0f), end = Offset(w * 0.40f, h), strokeWidth = 2f)

        // Lab Beaker & Flask
        val flask = Path().apply {
          moveTo(w * 0.70f, h * 0.72f)
          lineTo(w * 0.74f, h * 0.54f)
          lineTo(w * 0.78f, h * 0.54f)
          lineTo(w * 0.82f, h * 0.72f)
          close()
        }
        drawPath(flask, color = Color(0xFF64748B))

        // Variation 0 Difference: Beaker chemical liquid at (0.76, 0.60)
        val liquidColor = if (hasDifference && variationIndex == 0) Color(0xFF10B981) else VibrantPink
        drawCircle(liquidColor, radius = 9.dp.toPx(), center = Offset(w * 0.76f, h * 0.64f))

        // Robot Head
        drawRoundRect(
          color = Color(0xFF94A3B8),
          topLeft = Offset(w * 0.18f, h * 0.45f),
          size = Size(w * 0.16f, h * 0.22f),
          cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
        )
        // Variation 1 Difference: Robot Eyes color at (0.26, 0.52)
        val eyeColor = if (hasDifference && variationIndex == 1) Color(0xFFEF4444) else CyanBright
        drawCircle(eyeColor, radius = 4.dp.toPx(), center = Offset(w * 0.22f, h * 0.52f))
        drawCircle(eyeColor, radius = 4.dp.toPx(), center = Offset(w * 0.30f, h * 0.52f))
      }

      SceneTheme.JUNGLE -> {
        // Tropical Rainforest
        drawRect(color = Color(0xFF064E3B), size = Size(w, h * 0.60f)) // Green canopy sky
        drawRect(color = Color(0xFF047857), topLeft = Offset(0f, h * 0.60f), size = Size(w, h * 0.40f)) // Jungle floor

        // River
        val river = Path().apply {
          moveTo(w * 0.40f, h * 0.60f)
          lineTo(w * 0.60f, h * 0.60f)
          lineTo(w * 0.90f, h)
          lineTo(w * 0.50f, h)
          close()
        }
        drawPath(river, color = Color(0xFF0284C7))

        // Giant Vine Tree
        drawRect(Color(0xFF78350F), topLeft = Offset(w * 0.10f, h * 0.20f), size = Size(w * 0.15f, h * 0.80f))
        drawCircle(Color(0xFF16A34A), radius = 28.dp.toPx(), center = Offset(w * 0.20f, h * 0.25f))

        // Variation 0 Difference: Tropical Parrot on branch at (0.35, 0.38)
        val parrotColor = if (hasDifference && variationIndex == 0) Color(0xFFFACC15) else Color(0xFFDC2626)
        drawCircle(parrotColor, radius = 10.dp.toPx(), center = Offset(w * 0.35f, h * 0.38f))
        drawCircle(Color(0xFF38BDF8), radius = 4.dp.toPx(), center = Offset(w * 0.38f, h * 0.36f))

        // Variation 1 Difference: River Water Lily at (0.75, 0.72)
        val lilyColor = if (hasDifference && variationIndex == 1) VibrantPink else Color.White
        drawCircle(Color(0xFF059669), radius = 10.dp.toPx(), center = Offset(w * 0.75f, h * 0.72f))
        drawCircle(lilyColor, radius = 5.dp.toPx(), center = Offset(w * 0.75f, h * 0.72f))
      }

      SceneTheme.ARCTIC -> {
        // Polar Arctic & Glacier
        drawRect(color = Color(0xFF0F172A), size = Size(w, h * 0.65f)) // Night polar sky
        drawRect(color = Color(0xFF0369A1), topLeft = Offset(0f, h * 0.65f), size = Size(w, h * 0.35f)) // Freezing sea

        // Ice Mountain
        val glacier = Path().apply {
          moveTo(0f, h * 0.65f)
          lineTo(w * 0.40f, h * 0.30f)
          lineTo(w * 0.75f, h * 0.65f)
          close()
        }
        drawPath(glacier, color = Color(0xFFE0F2FE))

        // Floating Ice Floe
        drawOval(Color.White, topLeft = Offset(w * 0.15f, h * 0.68f), size = Size(w * 0.40f, h * 0.20f))

        // Variation 0 Difference: Penguin scarf at (0.30, 0.72)
        drawRoundRect(
          color = Color(0xFF1E293B),
          topLeft = Offset(w * 0.28f, h * 0.66f),
          size = Size(w * 0.08f, h * 0.12f),
          cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
        )
        val scarfColor = if (hasDifference && variationIndex == 0) NeonOrange else Color(0xFF2563EB)
        drawCircle(scarfColor, radius = 4.dp.toPx(), center = Offset(w * 0.32f, h * 0.70f))

        // Variation 1 Difference: Aurora wave at (0.70, 0.22)
        val auroraColor = if (hasDifference && variationIndex == 1) Color(0xFFA855F7) else Color(0xFF10B981)
        val auroraPath = Path().apply {
          moveTo(w * 0.50f, h * 0.25f)
          lineTo(w * 0.70f, h * 0.18f)
          lineTo(w * 0.90f, h * 0.24f)
        }
        drawLine(auroraColor, start = Offset(w * 0.50f, h * 0.22f), end = Offset(w * 0.90f, h * 0.22f), strokeWidth = 8.dp.toPx())
      }

      SceneTheme.BALLOON -> {
        // High Sky & Hot Air Balloon
        drawRect(color = Color(0xFF38BDF8), size = Size(w, h)) // Sky blue

        // Clouds
        drawCircle(Color.White.copy(alpha = 0.85f), radius = 18.dp.toPx(), center = Offset(w * 0.18f, h * 0.25f))
        drawCircle(Color.White.copy(alpha = 0.85f), radius = 22.dp.toPx(), center = Offset(w * 0.24f, h * 0.25f))

        // Hot Air Balloon Envelope
        drawCircle(Color(0xFFEF4444), radius = 26.dp.toPx(), center = Offset(w * 0.40f, h * 0.38f))

        // Variation 0 Difference: Balloon center stripe at (0.40, 0.35)
        val stripeColor = if (hasDifference && variationIndex == 0) NeonGold else Color.White
        drawOval(stripeColor, topLeft = Offset(w * 0.32f, h * 0.34f), size = Size(w * 0.16f, 8.dp.toPx()))

        // Basket
        drawRect(Color(0xFF78350F), topLeft = Offset(w * 0.37f, h * 0.52f), size = Size(w * 0.06f, 8.dp.toPx()))

        // Variation 1 Difference: Bird flock at (0.78, 0.48)
        val birdColor = if (hasDifference && variationIndex == 1) Color(0xFFB91C1C) else Color(0xFF0F172A)
        drawCircle(birdColor, radius = 3.dp.toPx(), center = Offset(w * 0.76f, h * 0.46f))
        drawCircle(birdColor, radius = 4.dp.toPx(), center = Offset(w * 0.80f, h * 0.48f))
        drawCircle(birdColor, radius = 3.dp.toPx(), center = Offset(w * 0.84f, h * 0.46f))
      }

      SceneTheme.UNDERWATER -> {
        // Deep Underwater Reef
        drawRect(color = Color(0xFF0C4A6E), size = Size(w, h * 0.70f))
        drawRect(color = Color(0xFFD97706), topLeft = Offset(0f, h * 0.70f), size = Size(w, h * 0.30f)) // Sand

        // Coral & Seaweed
        drawCircle(Color(0xFFEC4899), radius = 18.dp.toPx(), center = Offset(w * 0.85f, h * 0.70f))
        drawRect(Color(0xFF10B981), topLeft = Offset(w * 0.10f, h * 0.50f), size = Size(w * 0.05f, h * 0.30f))

        // Variation 0 Difference: Glowing jellyfish at (0.25, 0.40)
        val jellyColor = if (hasDifference && variationIndex == 0) NeonGold else Color(0xFFF43F5E)
        drawCircle(jellyColor, radius = 12.dp.toPx(), center = Offset(w * 0.25f, h * 0.40f))

        // Sunken Treasure Chest
        // Variation 1 Difference: Chest lock/contents at (0.72, 0.74)
        val chestColor = Color(0xFF78350F)
        drawRect(chestColor, topLeft = Offset(w * 0.65f, h * 0.70f), size = Size(w * 0.14f, 16.dp.toPx()))
        val treasureColor = if (hasDifference && variationIndex == 1) CyanBright else NeonGold
        drawCircle(treasureColor, radius = 5.dp.toPx(), center = Offset(w * 0.72f, h * 0.74f))
      }

      SceneTheme.VOLCANO -> {
        // Volcano & Magma
        drawRect(color = Color(0xFF1C1917), size = Size(w, h * 0.60f)) // Ash sky
        drawRect(color = Color(0xFF44403C), topLeft = Offset(0f, h * 0.60f), size = Size(w, h * 0.40f)) // Earth

        // Volcano Peak
        val volcano = Path().apply {
          moveTo(w * 0.10f, h * 0.70f)
          lineTo(w * 0.44f, h * 0.32f)
          lineTo(w * 0.56f, h * 0.32f)
          lineTo(w * 0.90f, h * 0.70f)
          close()
        }
        drawPath(volcano, color = Color(0xFF292524))

        // Variation 0 Difference: Crater Smoke Plume at (0.50, 0.32)
        val plumeColor = if (hasDifference && variationIndex == 0) Color(0xFF7C3AED) else Color(0xFFEA580C)
        drawCircle(plumeColor, radius = 16.dp.toPx(), center = Offset(w * 0.50f, h * 0.24f))

        // Variation 1 Difference: Lava Stream on flank at (0.25, 0.68)
        val lavaStreamColor = if (hasDifference && variationIndex == 1) Color(0xFFFACC15) else Color(0xFFDC2626)
        drawLine(
          color = lavaStreamColor,
          start = Offset(w * 0.46f, h * 0.35f),
          end = Offset(w * 0.25f, h * 0.68f),
          strokeWidth = 5.dp.toPx()
        )
      }

      SceneTheme.ARCADE -> {
        // Retro Arcade Hall
        drawRect(color = Color(0xFF09090B), size = Size(w, h * 0.65f)) // Dark wall
        drawRect(color = Color(0xFF2E1065), topLeft = Offset(0f, h * 0.65f), size = Size(w, h * 0.35f)) // Neon carpet

        // Arcade Cabinet
        drawRect(Color(0xFF3B82F6), topLeft = Offset(w * 0.30f, h * 0.35f), size = Size(w * 0.22f, h * 0.55f))
        // Screen bezel
        drawRect(Color.Black, topLeft = Offset(w * 0.33f, h * 0.42f), size = Size(w * 0.16f, h * 0.22f))

        // Variation 0 Difference: Arcade Screen Pixel Hero at (0.40, 0.48)
        val pixelHeroColor = if (hasDifference && variationIndex == 0) Color(0xFFEF4444) else Color(0xFF10B981)
        drawRect(pixelHeroColor, topLeft = Offset(w * 0.38f, h * 0.48f), size = Size(10.dp.toPx(), 10.dp.toPx()))

        // Wall Sign
        // Variation 1 Difference: High Score Banner at (0.75, 0.35)
        val signColor = if (hasDifference && variationIndex == 1) CyanBright else NeonGold
        drawRect(signColor, topLeft = Offset(w * 0.68f, h * 0.32f), size = Size(w * 0.18f, 10.dp.toPx()))
      }

      SceneTheme.FARM -> {
        // Rural Farm & Windmill
        drawRect(color = Color(0xFF60A5FA), size = Size(w, h * 0.60f)) // Blue sky
        drawRect(color = Color(0xFF16A34A), topLeft = Offset(0f, h * 0.60f), size = Size(w, h * 0.40f)) // Green pasture

        // Red Barn
        drawRect(Color(0xFFDC2626), topLeft = Offset(w * 0.12f, h * 0.50f), size = Size(w * 0.24f, h * 0.30f))

        // Variation 1 Difference: Sunflower by fence at (0.28, 0.75)
        val flowerColor = if (hasDifference && variationIndex == 1) Color(0xFFEC4899) else Color(0xFFFBBF24)
        drawCircle(Color(0xFF78350F), radius = 5.dp.toPx(), center = Offset(w * 0.28f, h * 0.75f))
        drawCircle(flowerColor, radius = 8.dp.toPx(), center = Offset(w * 0.28f, h * 0.75f))

        // Windmill Tower
        drawRect(Color(0xFFE2E8F0), topLeft = Offset(w * 0.66f, h * 0.32f), size = Size(w * 0.08f, h * 0.38f))

        // Variation 0 Difference: Windmill Sails at (0.70, 0.40)
        val bladeColor = if (hasDifference && variationIndex == 0) NeonOrange else Color.White
        drawLine(bladeColor, start = Offset(w * 0.70f, h * 0.25f), end = Offset(w * 0.70f, h * 0.45f), strokeWidth = 4.dp.toPx())
        drawLine(bladeColor, start = Offset(w * 0.60f, h * 0.35f), end = Offset(w * 0.80f, h * 0.35f), strokeWidth = 4.dp.toPx())
      }

      SceneTheme.AIRPORT -> {
        // Airport Runway & Jet
        drawRect(color = Color(0xFF0284C7), size = Size(w, h * 0.50f)) // Sky
        drawRect(color = Color(0xFF334155), topLeft = Offset(0f, h * 0.50f), size = Size(w, h * 0.50f)) // Tarmac

        // Runway White Dash
        drawRect(Color.White, topLeft = Offset(w * 0.10f, h * 0.80f), size = Size(w * 0.20f, 6.dp.toPx()))
        drawRect(Color.White, topLeft = Offset(w * 0.70f, h * 0.80f), size = Size(w * 0.20f, 6.dp.toPx()))

        // Control Tower
        drawRect(Color(0xFFCBD5E1), topLeft = Offset(w * 0.78f, h * 0.25f), size = Size(w * 0.06f, h * 0.35f))

        // Variation 1 Difference: Tower Radar Light at (0.80, 0.28)
        val radarColor = if (hasDifference && variationIndex == 1) Color(0xFF10B981) else Color(0xFFEF4444)
        drawCircle(radarColor, radius = 6.dp.toPx(), center = Offset(w * 0.81f, h * 0.24f))

        // Jet Airliner Fuselage
        val jet = Path().apply {
          moveTo(w * 0.30f, h * 0.58f)
          lineTo(w * 0.65f, h * 0.58f)
          lineTo(w * 0.72f, h * 0.52f)
          lineTo(w * 0.65f, h * 0.50f)
          lineTo(w * 0.30f, h * 0.50f)
          close()
        }
        drawPath(jet, color = Color.White)

        // Variation 0 Difference: Tail Fin Logo at (0.48, 0.52)
        val tailColor = if (hasDifference && variationIndex == 0) Color(0xFFEF4444) else Color(0xFF2563EB)
        drawCircle(tailColor, radius = 7.dp.toPx(), center = Offset(w * 0.48f, h * 0.52f))
      }

      SceneTheme.PIRATE_SHIP -> {
        // Pirate Ship on open sea
        drawRect(color = Color(0xFF0284C7), size = Size(w, h * 0.55f)) // Sky
        drawRect(color = Color(0xFF0369A1), topLeft = Offset(0f, h * 0.55f), size = Size(w, h * 0.45f)) // Sea

        // Wooden Hull
        val hull = Path().apply {
          moveTo(w * 0.20f, h * 0.65f)
          lineTo(w * 0.85f, h * 0.65f)
          lineTo(w * 0.72f, h * 0.78f)
          lineTo(w * 0.28f, h * 0.78f)
          close()
        }
        drawPath(hull, color = Color(0xFF451A03))

        // Mast
        drawLine(Color(0xFF78350F), start = Offset(w * 0.42f, h * 0.30f), end = Offset(w * 0.42f, h * 0.65f), strokeWidth = 5.dp.toPx())

        // Sail
        val sail = Path().apply {
          moveTo(w * 0.30f, h * 0.34f)
          lineTo(w * 0.55f, h * 0.34f)
          lineTo(w * 0.52f, h * 0.54f)
          lineTo(w * 0.32f, h * 0.54f)
          close()
        }
        drawPath(sail, color = Color(0xFFF8FAFC))

        // Variation 0 Difference: Skull emblem on sail at (0.42, 0.40)
        val skullColor = if (hasDifference && variationIndex == 0) Color(0xFFEF4444) else Color(0xFF0F172A)
        drawCircle(skullColor, radius = 6.dp.toPx(), center = Offset(w * 0.42f, h * 0.42f))

        // Variation 1 Difference: Gold Treasure Chest on deck at (0.75, 0.68)
        val chestColor = if (hasDifference && variationIndex == 1) CyanBright else NeonGold
        drawRect(chestColor, topLeft = Offset(w * 0.72f, h * 0.62f), size = Size(w * 0.08f, 10.dp.toPx()))
      }

      SceneTheme.SUBMARINE -> {
        // Deep Oceanic Submarine
        drawRect(color = Color(0xFF0B192C), size = Size(w, h * 0.75f))
        drawRect(color = Color(0xFF1E3E62), topLeft = Offset(0f, h * 0.75f), size = Size(w, h * 0.25f))

        // Submarine Hull
        drawOval(Color(0xFFF59E0B), topLeft = Offset(w * 0.30f, h * 0.42f), size = Size(w * 0.45f, h * 0.24f))
        // Periscope Tower
        drawRect(Color(0xFFD97706), topLeft = Offset(w * 0.48f, h * 0.32f), size = Size(w * 0.05f, h * 0.12f))

        // Variation 0 Difference: Periscope spotlight at (0.50, 0.48)
        val spotLightColor = if (hasDifference && variationIndex == 0) VibrantPink else CyanBright
        drawCircle(spotLightColor, radius = 7.dp.toPx(), center = Offset(w * 0.50f, h * 0.32f))

        // Coral & Seaweed
        drawCircle(Color(0xFF10B981), radius = 18.dp.toPx(), center = Offset(w * 0.15f, h * 0.75f))

        // Variation 1 Difference: Octopus tentacle on coral at (0.22, 0.65)
        val tentacleColor = if (hasDifference && variationIndex == 1) Color(0xFFA855F7) else Color(0xFFF97316)
        drawCircle(tentacleColor, radius = 8.dp.toPx(), center = Offset(w * 0.22f, h * 0.68f))
      }

      SceneTheme.TRAIN_BRIDGE -> {
        // Mountain Viaduct & Train
        drawRect(color = Color(0xFF38BDF8), size = Size(w, h * 0.50f)) // Sky
        drawRect(color = Color(0xFF166534), topLeft = Offset(0f, h * 0.50f), size = Size(w, h * 0.50f)) // Mountain Valley

        // Stone Bridge
        drawRect(Color(0xFF64748B), topLeft = Offset(0f, h * 0.52f), size = Size(w, 8.dp.toPx()))

        // Train Carriages
        drawRect(Color(0xFFDC2626), topLeft = Offset(w * 0.25f, h * 0.45f), size = Size(w * 0.22f, 14.dp.toPx()))
        // Locomotive
        drawRect(Color(0xFF1E293B), topLeft = Offset(w * 0.48f, h * 0.42f), size = Size(w * 0.15f, 18.dp.toPx()))

        // Variation 0 Difference: Chimney Smoke Cloud at (0.46, 0.45)
        val smokeColor = if (hasDifference && variationIndex == 0) Color(0xFFFBBF24) else Color.White
        drawCircle(smokeColor, radius = 9.dp.toPx(), center = Offset(w * 0.58f, h * 0.35f))

        // Variation 1 Difference: Mountain river wave below at (0.80, 0.65)
        val riverWaveColor = if (hasDifference && variationIndex == 1) Color(0xFFEC4899) else CyanBright
        drawOval(riverWaveColor, topLeft = Offset(w * 0.74f, h * 0.65f), size = Size(w * 0.14f, 6.dp.toPx()))
      }

      SceneTheme.FERRIS_WHEEL -> {
        // Night Carnival & Ferris Wheel
        drawRect(color = Color(0xFF0F172A), size = Size(w, h))

        // Ferris Wheel Hub & Rim
        drawCircle(Color(0xFF38BDF8), radius = 32.dp.toPx(), center = Offset(w * 0.50f, h * 0.42f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx()))
        drawCircle(Color.White, radius = 6.dp.toPx(), center = Offset(w * 0.50f, h * 0.42f))

        // Spoke lines
        drawLine(Color(0xFF94A3B8), start = Offset(w * 0.50f, h * 0.20f), end = Offset(w * 0.50f, h * 0.64f), strokeWidth = 2.dp.toPx())
        drawLine(Color(0xFF94A3B8), start = Offset(w * 0.30f, h * 0.42f), end = Offset(w * 0.70f, h * 0.42f), strokeWidth = 2.dp.toPx())

        // Variation 0 Difference: Top Gondola cabin at (0.50, 0.35)
        val topCabinColor = if (hasDifference && variationIndex == 0) Color(0xFFEF4444) else NeonGold
        drawCircle(topCabinColor, radius = 7.dp.toPx(), center = Offset(w * 0.50f, h * 0.20f))

        // Ticket booth
        drawRect(Color(0xFF4338CA), topLeft = Offset(w * 0.18f, h * 0.65f), size = Size(w * 0.14f, h * 0.25f))

        // Variation 1 Difference: Ticket booth sign light at (0.24, 0.68)
        val boothLight = if (hasDifference && variationIndex == 1) Color(0xFF10B981) else Color(0xFFF43F5E)
        drawCircle(boothLight, radius = 6.dp.toPx(), center = Offset(w * 0.25f, h * 0.65f))
      }

      SceneTheme.WINDMILL_TULIPS -> {
        // Dutch Windmill & Tulip Field
        drawRect(color = Color(0xFF93C5FD), size = Size(w, h * 0.50f)) // Sky
        drawRect(color = Color(0xFF15803D), topLeft = Offset(0f, h * 0.50f), size = Size(w, h * 0.50f)) // Grass

        // Windmill Body
        val wm = Path().apply {
          moveTo(w * 0.65f, h * 0.55f)
          lineTo(w * 0.70f, h * 0.25f)
          lineTo(w * 0.76f, h * 0.25f)
          lineTo(w * 0.81f, h * 0.55f)
          close()
        }
        drawPath(wm, color = Color(0xFFE2E8F0))

        // Blades
        drawLine(Color(0xFF78350F), start = Offset(w * 0.73f, h * 0.15f), end = Offset(w * 0.73f, h * 0.40f), strokeWidth = 3.dp.toPx())
        drawLine(Color(0xFF78350F), start = Offset(w * 0.63f, h * 0.28f), end = Offset(w * 0.83f, h * 0.28f), strokeWidth = 3.dp.toPx())

        // Variation 1 Difference: Windmill window shutter at (0.70, 0.42)
        val shutterColor = if (hasDifference && variationIndex == 1) CyanBright else Color(0xFFDC2626)
        drawRect(shutterColor, topLeft = Offset(w * 0.71f, h * 0.38f), size = Size(8.dp.toPx(), 8.dp.toPx()))

        // Tulips in foreground
        drawCircle(Color(0xFFEF4444), radius = 6.dp.toPx(), center = Offset(w * 0.18f, h * 0.74f))
        drawCircle(Color(0xFFFBBF24), radius = 6.dp.toPx(), center = Offset(w * 0.55f, h * 0.76f))

        // Variation 0 Difference: Front row tulip color at (0.35, 0.74)
        val tulipColor = if (hasDifference && variationIndex == 0) Color(0xFFA855F7) else Color(0xFFEF4444)
        drawCircle(tulipColor, radius = 8.dp.toPx(), center = Offset(w * 0.35f, h * 0.74f))
      }

      SceneTheme.POLAR_IGLOO -> {
        // Polar Tundra & Igloo
        drawRect(color = Color(0xFF1E293B), size = Size(w, h * 0.60f)) // Arctic dusk sky
        drawRect(color = Color(0xFFE2E8F0), topLeft = Offset(0f, h * 0.60f), size = Size(w, h * 0.40f)) // Snow

        // Igloo Dome
        drawOval(Color.White, topLeft = Offset(w * 0.20f, h * 0.45f), size = Size(w * 0.35f, h * 0.30f))

        // Variation 0 Difference: Igloo entrance arch at (0.38, 0.65)
        val archColor = if (hasDifference && variationIndex == 0) CyanBright else Color(0xFF94A3B8)
        drawOval(archColor, topLeft = Offset(w * 0.34f, h * 0.60f), size = Size(w * 0.10f, h * 0.14f))

        // Polar Bear Silhouette
        drawOval(Color(0xFFF8FAFC), topLeft = Offset(w * 0.68f, h * 0.50f), size = Size(w * 0.18f, h * 0.22f))
        drawCircle(Color(0xFFF8FAFC), radius = 10.dp.toPx(), center = Offset(w * 0.74f, h * 0.46f))

        // Variation 1 Difference: Polar Bear Winter Beanie at (0.74, 0.58)
        val beanieColor = if (hasDifference && variationIndex == 1) Color(0xFFEF4444) else Color(0xFF3B82F6)
        drawCircle(beanieColor, radius = 6.dp.toPx(), center = Offset(w * 0.74f, h * 0.43f))
      }

      SceneTheme.CYBER_HIGHWAY -> {
        // Cyberpunk Neon Highway
        drawRect(color = Color(0xFF030712), size = Size(w, h))

        // Grid road
        drawLine(CyanBright, start = Offset(w * 0.50f, h * 0.40f), end = Offset(0f, h), strokeWidth = 2.dp.toPx())
        drawLine(CyanBright, start = Offset(w * 0.50f, h * 0.40f), end = Offset(w, h), strokeWidth = 2.dp.toPx())

        // Skyscraper building
        drawRect(Color(0xFF111827), topLeft = Offset(w * 0.75f, h * 0.15f), size = Size(w * 0.20f, h * 0.55f))

        // Variation 1 Difference: Skyscraper Hologram Logo at (0.82, 0.25)
        val logoColor = if (hasDifference && variationIndex == 1) VibrantGreen else VibrantPink
        drawCircle(logoColor, radius = 9.dp.toPx(), center = Offset(w * 0.84f, h * 0.26f))

        // Flying car
        drawRoundRect(Color(0xFFE2E8F0), topLeft = Offset(w * 0.42f, h * 0.45f), size = Size(w * 0.20f, h * 0.10f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f))

        // Variation 0 Difference: Flying car neon thruster flame at (0.52, 0.48)
        val thrusterColor = if (hasDifference && variationIndex == 0) Color(0xFFF59E0B) else CyanBright
        drawCircle(thrusterColor, radius = 6.dp.toPx(), center = Offset(w * 0.52f, h * 0.55f))
      }

      SceneTheme.MEDIEVAL_MARKET -> {
        // Medieval Village Market
        drawRect(color = Color(0xFF78716C), size = Size(w, h)) // Cobblestone ground

        // Market Tent 1
        drawRect(Color(0xFFB45309), topLeft = Offset(w * 0.25f, h * 0.45f), size = Size(w * 0.26f, h * 0.35f))
        val roof = Path().apply {
          moveTo(w * 0.22f, h * 0.45f)
          lineTo(w * 0.38f, h * 0.30f)
          lineTo(w * 0.54f, h * 0.45f)
          close()
        }
        drawPath(roof, color = Color(0xFFDC2626))

        // Variation 0 Difference: Market Tent Banner at (0.38, 0.58)
        val bannerColor = if (hasDifference && variationIndex == 0) NeonGold else Color(0xFF2563EB)
        drawCircle(bannerColor, radius = 7.dp.toPx(), center = Offset(w * 0.38f, h * 0.30f))

        // Fruit Stall Table
        drawRect(Color(0xFF78350F), topLeft = Offset(w * 0.65f, h * 0.58f), size = Size(w * 0.20f, h * 0.22f))

        // Variation 1 Difference: Fruit crate apples at (0.72, 0.66)
        val appleColor = if (hasDifference && variationIndex == 1) Color(0xFF10B981) else Color(0xFFEF4444)
        drawCircle(appleColor, radius = 6.dp.toPx(), center = Offset(w * 0.72f, h * 0.58f))
        drawCircle(appleColor, radius = 6.dp.toPx(), center = Offset(w * 0.78f, h * 0.58f))
      }

      SceneTheme.SAFARI_SUNSET -> {
        // African Safari Sunset
        drawRect(color = Color(0xFFEA580C), size = Size(w, h * 0.65f)) // Orange Sunset
        drawRect(color = Color(0xFF451A03), topLeft = Offset(0f, h * 0.65f), size = Size(w, h * 0.35f)) // Earth

        // Variation 1 Difference: Sun glow at (0.80, 0.30)
        val sunColor = if (hasDifference && variationIndex == 1) Color(0xFFFDE047) else Color(0xFFDC2626)
        drawCircle(sunColor, radius = 22.dp.toPx(), center = Offset(w * 0.80f, h * 0.28f))

        // Acacia Tree Silhouette
        drawRect(Color(0xFF1C1917), topLeft = Offset(w * 0.68f, h * 0.45f), size = Size(w * 0.04f, h * 0.22f))
        drawOval(Color(0xFF1C1917), topLeft = Offset(w * 0.58f, h * 0.38f), size = Size(w * 0.24f, 16.dp.toPx()))

        // Giraffe Silhouette
        drawRect(Color(0xFF1C1917), topLeft = Offset(w * 0.28f, h * 0.45f), size = Size(w * 0.08f, h * 0.24f))
        drawLine(Color(0xFF1C1917), start = Offset(w * 0.32f, h * 0.45f), end = Offset(w * 0.32f, h * 0.28f), strokeWidth = 5.dp.toPx())

        // Variation 0 Difference: Giraffe head / ear color at (0.32, 0.52)
        val giraffeEar = if (hasDifference && variationIndex == 0) NeonGold else Color(0xFF1C1917)
        drawCircle(giraffeEar, radius = 6.dp.toPx(), center = Offset(w * 0.32f, h * 0.27f))
      }

      SceneTheme.HAUNTED_MANOR -> {
        // Haunted Manor & Full Moon
        drawRect(color = Color(0xFF020617), size = Size(w, h))

        // Full Moon
        drawCircle(Color(0xFFFDE68A), radius = 20.dp.toPx(), center = Offset(w * 0.25f, h * 0.25f))

        // Variation 1 Difference: Flying Bat near moon at (0.24, 0.22)
        val batColor = if (hasDifference && variationIndex == 1) Color(0xFFEF4444) else Color(0xFF0F172A)
        drawCircle(batColor, radius = 4.dp.toPx(), center = Offset(w * 0.28f, h * 0.24f))

        // Gothic Manor Silhouette
        drawRect(Color(0xFF1E293B), topLeft = Offset(w * 0.38f, h * 0.42f), size = Size(w * 0.38f, h * 0.45f))
        // Tower roof
        val gRoof = Path().apply {
          moveTo(w * 0.45f, h * 0.42f)
          lineTo(w * 0.57f, h * 0.25f)
          lineTo(w * 0.69f, h * 0.42f)
          close()
        }
        drawPath(gRoof, color = Color(0xFF0F172A))

        // Variation 0 Difference: Attic Glowing Window at (0.50, 0.36)
        val windowGlow = if (hasDifference && variationIndex == 0) CyanBright else Color(0xFFFACC15)
        drawRect(windowGlow, topLeft = Offset(w * 0.54f, h * 0.34f), size = Size(8.dp.toPx(), 12.dp.toPx()))
      }

      SceneTheme.SKI_SLOPE -> {
        // Alpine Ski Slope & Cable Car
        drawRect(color = Color(0xFF38BDF8), size = Size(w, h * 0.45f)) // Sky
        drawRect(Color.White, topLeft = Offset(0f, h * 0.45f), size = Size(w, h * 0.55f)) // Snow Mountain

        // Cable Wire
        drawLine(Color(0xFF475569), start = Offset(0f, h * 0.30f), end = Offset(w, h * 0.45f), strokeWidth = 2.dp.toPx())

        // Variation 1 Difference: Cable car gondola color at (0.80, 0.40)
        val gondolaColor = if (hasDifference && variationIndex == 1) Color(0xFFEF4444) else Color(0xFF2563EB)
        drawRect(gondolaColor, topLeft = Offset(w * 0.75f, h * 0.38f), size = Size(w * 0.10f, 12.dp.toPx()))

        // Skier on slope
        drawCircle(Color(0xFF0F172A), radius = 5.dp.toPx(), center = Offset(w * 0.45f, h * 0.60f))

        // Variation 0 Difference: Skier jacket color at (0.45, 0.62)
        val jacketColor = if (hasDifference && variationIndex == 0) NeonOrange else Color(0xFF10B981)
        drawRect(jacketColor, topLeft = Offset(w * 0.43f, h * 0.63f), size = Size(w * 0.05f, 10.dp.toPx()))
      }

      SceneTheme.LIGHTHOUSE -> {
        // Stormy Sea & Lighthouse
        drawRect(color = Color(0xFF0F172A), size = Size(w, h * 0.55f)) // Night storm sky
        drawRect(color = Color(0xFF0369A1), topLeft = Offset(0f, h * 0.55f), size = Size(w, h * 0.45f)) // Ocean

        // Rocky cliff
        drawOval(Color(0xFF334155), topLeft = Offset(w * 0.40f, h * 0.55f), size = Size(w * 0.50f, h * 0.40f))

        // Lighthouse Tower
        drawRect(Color.White, topLeft = Offset(w * 0.55f, h * 0.32f), size = Size(w * 0.08f, h * 0.28f))
        drawRect(Color(0xFFDC2626), topLeft = Offset(w * 0.55f, h * 0.42f), size = Size(w * 0.08f, 10.dp.toPx()))

        // Variation 0 Difference: Light beam lantern at (0.52, 0.32)
        val beamColor = if (hasDifference && variationIndex == 0) CyanBright else Color(0xFFFDE047)
        drawCircle(beamColor, radius = 7.dp.toPx(), center = Offset(w * 0.59f, h * 0.30f))

        // Variation 1 Difference: Crashing white sea wave at (0.22, 0.68)
        val waveColor = if (hasDifference && variationIndex == 1) Color.White else CyanBright
        drawOval(waveColor, topLeft = Offset(w * 0.15f, h * 0.65f), size = Size(w * 0.20f, 8.dp.toPx()))
      }
    }
  }
}
