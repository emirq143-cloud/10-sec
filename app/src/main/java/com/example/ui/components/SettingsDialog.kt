package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

@Composable
fun SettingsDialog(
  soundEnabled: Boolean,
  vibrationEnabled: Boolean,
  onToggleSound: (Boolean) -> Unit,
  onToggleVibration: (Boolean) -> Unit,
  onOpenPrivacyPolicy: () -> Unit,
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
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Top Bar
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
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                tint = BluePrimary,
                modifier = Modifier.size(24.dp)
              )
            }
            Text(
              text = "Ayarlar",
              color = TextDark,
              fontSize = 20.sp,
              fontWeight = FontWeight.ExtraBold
            )
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Kapat", tint = TextDarkSecondary)
          }
        }

        // Settings items
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          // Sound toggle
          SettingToggleItem(
            icon = Icons.Default.VolumeUp,
            title = "Ses Efektleri",
            subtitle = "Tuş sesleri, zafer fanfare'i ve geri sayım",
            checked = soundEnabled,
            onCheckedChange = onToggleSound
          )

          // Vibration toggle
          SettingToggleItem(
            icon = Icons.Default.Vibration,
            title = "Haptik Titreşim",
            subtitle = "Başarılı ve hatalı dokunuş geri bildirimleri",
            checked = vibrationEnabled,
            onCheckedChange = onToggleVibration
          )

          // Privacy Policy button
          SettingClickableItem(
            icon = Icons.Default.PrivacyTip,
            title = "Gizlilik Politikası",
            subtitle = "Google Play & AdMob reklam uyumluluğu",
            onClick = onOpenPrivacyPolicy
          )
        }

        // App Version & Store Info
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
            .padding(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "10 SEC: Test Your Limits",
                color = TextDark,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Google Play Sürümü v1.0.0",
                color = TextDarkMuted,
                fontSize = 11.sp
              )
            }
            Text(
              text = "13+ Uyumlu",
              color = Color(0xFF0284C7),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

@Composable
private fun SettingToggleItem(
  icon: ImageVector,
  title: String,
  subtitle: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(Color(0xFFF8FAFC))
      .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
      .padding(horizontal = 14.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.weight(1f)
    ) {
      Icon(icon, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(24.dp))
      Column {
        Text(text = title, color = TextDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(text = subtitle, color = TextDarkSecondary, fontSize = 11.sp)
      }
    }
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = Color.White,
        checkedTrackColor = BluePrimary,
        uncheckedThumbColor = Color.White,
        uncheckedTrackColor = Color(0xFFCBD5E1)
      )
    )
  }
}

@Composable
private fun SettingClickableItem(
  icon: ImageVector,
  title: String,
  subtitle: String,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(Color(0xFFF8FAFC))
      .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
      .clickable { onClick() }
      .padding(horizontal = 14.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.weight(1f)
    ) {
      Icon(icon, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(24.dp))
      Column {
        Text(text = title, color = TextDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(text = subtitle, color = TextDarkSecondary, fontSize = 11.sp)
      }
    }
    Icon(
      imageVector = Icons.Default.ChevronRight,
      contentDescription = null,
      tint = TextDarkMuted,
      modifier = Modifier.size(22.dp)
    )
  }
}
