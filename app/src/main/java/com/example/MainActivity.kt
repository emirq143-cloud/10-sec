package com.example

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ads.AdManager
import com.example.ui.GameViewModel
import com.example.ui.MainScreen
import com.example.ui.theme.MyApplicationTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    try {
      if (FirebaseApp.getApps(applicationContext).isEmpty()) {
        FirebaseApp.initializeApp(applicationContext)
      }
    } catch (e: Throwable) {
      Log.w("MainActivity", "FirebaseApp init: ${e.message}")
    }
    setContent {
      MyApplicationTheme {
        val viewModel: GameViewModel = viewModel()
        MainScreen(viewModel = viewModel)
      }
    }
    try {
      AdManager.initialize(applicationContext)
    } catch (e: Throwable) {
      Log.w("MainActivity", "AdManager init: ${e.message}")
    }
  }
}
