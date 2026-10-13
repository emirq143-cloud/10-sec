package com.example.data.auth

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

object FirebaseInitializer {
  private var lastInitError: String? = null

  fun getLastError(): String? = lastInitError

  fun init(context: Context): FirebaseApp? {
    val appContext = context.applicationContext
    if (FirebaseApp.getApps(appContext).isNotEmpty()) {
      return FirebaseApp.getInstance()
    }

    // Try standard resource-based initialization first
    try {
      val app = FirebaseApp.initializeApp(appContext)
      if (app != null) {
        lastInitError = null
        Log.d("FirebaseInitializer", "Firebase initialized successfully via resources.")
        return app
      }
    } catch (e: Throwable) {
      Log.w("FirebaseInitializer", "Standard Firebase init failed: ${e.message}, attempting explicit options...")
      lastInitError = e.message
    }

    // Fallback: Explicit initialization using options from project configuration
    return try {
      val options = FirebaseOptions.Builder()
        .setApplicationId("1:703490968047:android:0db30a4478e4b8c3a60e1a")
        .setApiKey("AIzaSyCbJVRprynqvD_eb52BR2-UH8H5lb2M4MU")
        .setProjectId("gen-lang-client-0314333356")
        .setGcmSenderId("703490968047")
        .setStorageBucket("gen-lang-client-0314333356.firebasestorage.app")
        .build()

      val app = FirebaseApp.initializeApp(appContext, options)
      lastInitError = null
      Log.d("FirebaseInitializer", "Firebase initialized successfully via explicit options.")
      app
    } catch (ex: Throwable) {
      Log.e("FirebaseInitializer", "Explicit Firebase init failed: ${ex.message}", ex)
      lastInitError = ex.message
      null
    }
  }
}
