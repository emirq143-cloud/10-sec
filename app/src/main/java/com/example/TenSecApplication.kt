package com.example

import android.app.Application
import android.util.Log
import com.example.data.auth.FirebaseInitializer

class TenSecApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    try {
      FirebaseInitializer.init(this)
      Log.d("TenSecApplication", "App initialized, Firebase initialized at startup.")
    } catch (e: Throwable) {
      Log.w("TenSecApplication", "Error during early Firebase init: ${e.message}")
    }
  }
}
