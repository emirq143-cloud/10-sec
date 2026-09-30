package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CardWhite
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextDarkSecondary

/**
 * Google Play Store compliant Privacy Policy & Terms Dialog.
 * Clearly discloses advertising (Google AdMob), data safety, and user rights.
 */
@Composable
fun PrivacyPolicyDialog(
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(26.dp))
        .background(CardWhite)
        .border(2.dp, SkyBlueAccent, RoundedCornerShape(26.dp))
        .padding(22.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0xFFEFF6FF)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.PrivacyTip,
                contentDescription = null,
                tint = BluePrimary,
                modifier = Modifier.size(24.dp)
              )
            }
            Column {
              Text(
                text = "Gizlilik Politikası",
                color = TextDark,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
              )
              Text(
                text = "Google Play Uyumluluk Beyanı",
                color = TextDarkMuted,
                fontSize = 11.sp
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Kapat", tint = TextDarkSecondary)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Scrollable content
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp)
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          PolicySection(
            title = "1. Genel Bilgilendirme",
            body = "10 SEC mobil uygulaması (\"Uygulama\"), kullanıcıların zeka, refleks, dikkat ve hafıza becerilerini geliştirmelerine yönelik hazırlanmış bir bulmaca ve refleks oyunudur. Gizliliğinize büyük önem veriyoruz."
          )

          PolicySection(
            title = "2. Toplanan Veriler ve Kullanımı",
            body = "• Oyun İçi Veriler: Skorlarınız, tamamladığınız görevler, reaksiyon süresi analizleriniz ve seçtiğiniz avatarlar cihazınızın yerel hafızasında (Room Database) saklanır.\n• Liderlik Tablosu: Türkiye ve dünya sıralamalarında yalnızca belirlediğiniz takma adınız (nickname), puanınız ve liginiz görüntülenir. Gerçek adınız veya kimlik bilgileriniz talep edilmez."
          )

          PolicySection(
            title = "3. Reklamlar ve AdMob Hizmetleri",
            body = "Uygulamamız ücretsiz olarak sunulmaktadır. Geliştirme maliyetlerini karşılamak ve yeni mini oyunlar sunabilmek amacıyla Google AdMob reklam kütüphanesi kullanılabilir.\n• Reklam Formatları: Banner reklamlar, geçiş reklamları ve kullanıcı isteğine bağlı ödüllü video reklamlar (2x skor ve can kazanma).\n• Veri Kullanımı: Google AdMob, kullanıcılara ilgi alanlarına uygun veya kişiselleştirilmemiş reklamlar sunabilmek için standart Android Reklam Kimliği (AAID) ve IP adresi gibi teknik verileri kullanabilir. Bu veriler yalnızca reklam sunumu, sahtekarlık tespiti ve performans raporlaması amacıyla kullanılır."
          )

          PolicySection(
            title = "4. Google Play Veri Güvenliği Beyanı",
            body = "Google Play Store Veri Güvenliği (Data Safety) formuna uygun olarak:\n• Toplanan ve Paylaşılan Veriler: Yalnızca anonim reklam kimliği (Cihaz/diğer kimlikler) reklam ve analitik işlevleri için üçüncü taraf reklam ağlarıyla (Google AdMob) paylaşılabilir.\n• Güvenlik Uygulamaları: Tüm veri aktarımları HTTPS şifrelemesi ile korunur. Kullanıcı dilediğinde uygulama verilerini temizleyerek yerel tüm verilerini silebilir."
          )

          PolicySection(
            title = "5. Yaş Politikası ve Çocuk Güvenliği",
            body = "Uygulamamız 13 yaş ve üzeri kitle için uygundur. COPPA (Children's Online Privacy Protection Act) ve GDPR ilkelerine tam uyumludur. Bilerek 13 yaşından küçük çocuklara ait kişisel veri toplanmaz."
          )

          PolicySection(
            title = "6. Cihaz İzinleri",
            body = "• VIBRATE: Oyun içi başarı ve hata durumlarında dokunsal geri bildirim sağlamak amacıyla kullanılır.\n• INTERNET & ACCESS_NETWORK_STATE: Canlı liderlik sıralamasını güncellemek ve reklam sunumu için gereklidir.\nUygulama konum, kamera, mikrofon veya rehber erişimi talep etmez."
          )

          PolicySection(
            title = "7. İletişim",
            body = "Gizlilik politikamız veya uygulamanız hakkındaki tüm soru, öneri ve destek talepleriniz için bize ulaşabilirsiniz:\nE-posta: emirq143@gmail.com"
          )

          Text(
            text = "Son güncelleme: Eylül 2026 • Sürüm 1.0.0",
            color = TextDarkMuted,
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 4.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
        ) {
          Text(text = "Anladım ve Kabul Ediyorum", color = Color.White, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun PolicySection(title: String, body: String) {
  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Text(
      text = title,
      color = BluePrimary,
      fontSize = 13.sp,
      fontWeight = FontWeight.Bold
    )
    Text(
      text = body,
      color = TextDarkSecondary,
      fontSize = 12.sp,
      lineHeight = 18.sp
    )
  }
}
