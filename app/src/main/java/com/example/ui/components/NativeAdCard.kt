package com.example.ui.components

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ads.AdManager
import com.example.ui.theme.VibrantPurple
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

/**
 * Custom Native Advanced Ad Card that matches the game's modern vibrant UI.
 * Displays seamlessly within lists (e.g. HomeScreen or GameResultScreen).
 */
@Composable
fun NativeAdCard(
  modifier: Modifier = Modifier,
  adUnitId: String = AdManager.NATIVE_AD_UNIT_ID
) {
  val context = LocalContext.current
  var loadedNativeAd by remember { mutableStateOf<NativeAd?>(null) }
  var adFailedToLoad by remember { mutableStateOf(false) }

  DisposableEffect(adUnitId) {
    var currentAd: NativeAd? = null
    try {
      val adLoader = AdLoader.Builder(context, adUnitId)
        .forNativeAd { ad: NativeAd ->
          currentAd = ad
          loadedNativeAd = ad
          adFailedToLoad = false
          Log.d("NativeAdCard", "Native ad loaded successfully: ${ad.headline}")
        }
        .withAdListener(object : AdListener() {
          override fun onAdFailedToLoad(error: LoadAdError) {
            super.onAdFailedToLoad(error)
            Log.w("NativeAdCard", "Native ad failed to load: ${error.message} (code: ${error.code})")
            adFailedToLoad = true
          }
        })
        .withNativeAdOptions(
          NativeAdOptions.Builder()
            .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
            .build()
        )
        .build()

      adLoader.loadAd(AdRequest.Builder().build())
    } catch (e: Throwable) {
      Log.w("NativeAdCard", "Exception requesting Native Ad: ${e.message}")
      adFailedToLoad = true
    }

    onDispose {
      currentAd?.destroy()
    }
  }

  // Only render container if ad is loaded and not failed
  if (loadedNativeAd != null && !adFailedToLoad) {
    val nativeAd = loadedNativeAd!!
    Box(
      modifier = modifier
        .fillMaxWidth()
        .shadow(8.dp, RoundedCornerShape(22.dp), spotColor = VibrantPurple.copy(alpha = 0.25f))
        .clip(RoundedCornerShape(22.dp))
        .background(Color.White)
        .border(1.5.dp, VibrantPurple.copy(alpha = 0.35f), RoundedCornerShape(22.dp))
        .padding(14.dp)
    ) {
      AndroidView(
        factory = { ctx ->
          createNativeAdView(ctx, nativeAd)
        },
        update = { adView ->
          populateNativeAdView(adView, nativeAd)
        },
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}

/**
 * Creates the NativeAdView programmatically with modern styling
 */
private fun createNativeAdView(context: Context, nativeAd: NativeAd): NativeAdView {
  val nativeAdView = NativeAdView(context)
  
  // Outer horizontal container
  val rootLayout = android.widget.LinearLayout(context).apply {
    orientation = android.widget.LinearLayout.HORIZONTAL
    layoutParams = android.view.ViewGroup.LayoutParams(
      android.view.ViewGroup.LayoutParams.MATCH_PARENT,
      android.view.ViewGroup.LayoutParams.WRAP_CONTENT
    )
    gravity = android.view.Gravity.CENTER_VERTICAL
  }

  // App / Sponsor Icon
  val iconView = ImageView(context).apply {
    layoutParams = android.widget.LinearLayout.LayoutParams(
      (46 * context.resources.displayMetrics.density).toInt(),
      (46 * context.resources.displayMetrics.density).toInt()
    ).apply {
      rightMargin = (12 * context.resources.displayMetrics.density).toInt()
    }
    scaleType = ImageView.ScaleType.CENTER_CROP
  }
  nativeAdView.iconView = iconView
  rootLayout.addView(iconView)

  // Middle text column (Ad badge + Headline + Body)
  val textLayout = android.widget.LinearLayout(context).apply {
    orientation = android.widget.LinearLayout.VERTICAL
    layoutParams = android.widget.LinearLayout.LayoutParams(
      0,
      android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
      1f
    )
  }

  // Top Row: "Reklam / Ad" Badge + Headline
  val headerRow = android.widget.LinearLayout(context).apply {
    orientation = android.widget.LinearLayout.HORIZONTAL
    gravity = android.view.Gravity.CENTER_VERTICAL
  }

  val badgeView = TextView(context).apply {
    text = "REKLAM"
    textSize = 9f
    setTextColor(android.graphics.Color.WHITE)
    setBackgroundColor(android.graphics.Color.parseColor("#7C3AED"))
    setPadding(
      (6 * context.resources.displayMetrics.density).toInt(),
      (2 * context.resources.displayMetrics.density).toInt(),
      (6 * context.resources.displayMetrics.density).toInt(),
      (2 * context.resources.displayMetrics.density).toInt()
    )
    layoutParams = android.widget.LinearLayout.LayoutParams(
      android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
      android.view.ViewGroup.LayoutParams.WRAP_CONTENT
    ).apply {
      rightMargin = (6 * context.resources.displayMetrics.density).toInt()
    }
  }
  headerRow.addView(badgeView)

  val headlineView = TextView(context).apply {
    textSize = 14f
    setTextColor(android.graphics.Color.parseColor("#0F172A"))
    setTypeface(typeface, android.graphics.Typeface.BOLD)
    maxLines = 1
    ellipsize = android.text.TextUtils.TruncateAt.END
  }
  nativeAdView.headlineView = headlineView
  headerRow.addView(headlineView)
  textLayout.addView(headerRow)

  val bodyView = TextView(context).apply {
    textSize = 12f
    setTextColor(android.graphics.Color.parseColor("#64748B"))
    maxLines = 2
    ellipsize = android.text.TextUtils.TruncateAt.END
    setPadding(0, (4 * context.resources.displayMetrics.density).toInt(), 0, 0)
  }
  nativeAdView.bodyView = bodyView
  textLayout.addView(bodyView)

  rootLayout.addView(textLayout)

  // Action Button (Yükle / Ziyaret Et / Oyna vb.)
  val callToActionView = Button(context).apply {
    textSize = 12f
    setTextColor(android.graphics.Color.WHITE)
    setBackgroundColor(android.graphics.Color.parseColor("#06B6D4")) // Neon Cyan
    isAllCaps = false
    layoutParams = android.widget.LinearLayout.LayoutParams(
      android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
      (40 * context.resources.displayMetrics.density).toInt()
    ).apply {
      leftMargin = (10 * context.resources.displayMetrics.density).toInt()
    }
    setPadding(
      (12 * context.resources.displayMetrics.density).toInt(),
      0,
      (12 * context.resources.displayMetrics.density).toInt(),
      0
    )
  }
  nativeAdView.callToActionView = callToActionView
  rootLayout.addView(callToActionView)

  nativeAdView.addView(rootLayout)
  populateNativeAdView(nativeAdView, nativeAd)
  return nativeAdView
}

private fun populateNativeAdView(adView: NativeAdView, nativeAd: NativeAd) {
  (adView.headlineView as? TextView)?.text = nativeAd.headline

  val icon = nativeAd.icon
  val iconView = adView.iconView as? ImageView
  if (icon != null && iconView != null) {
    iconView.setImageDrawable(icon.drawable)
    iconView.visibility = View.VISIBLE
  } else {
    iconView?.visibility = View.GONE
  }

  val bodyView = adView.bodyView as? TextView
  if (nativeAd.body != null && bodyView != null) {
    bodyView.text = nativeAd.body
    bodyView.visibility = View.VISIBLE
  } else {
    bodyView?.visibility = View.GONE
  }

  val ctaView = adView.callToActionView as? Button
  if (nativeAd.callToAction != null && ctaView != null) {
    ctaView.text = nativeAd.callToAction
    ctaView.visibility = View.VISIBLE
  } else {
    ctaView?.visibility = View.GONE
  }

  adView.setNativeAd(nativeAd)
}
