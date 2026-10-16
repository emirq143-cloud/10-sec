package com.example.data.auth

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class GoogleAuthManager(private val initialContext: Context? = null) {

  private var lastInitError: String? = null

  fun getAuth(context: Context? = initialContext): FirebaseAuth? {
    return try {
      val appContext = context?.applicationContext ?: initialContext?.applicationContext

      // Firebase henüz başlamadıysa garanti başlat
      if (appContext != null && FirebaseApp.getApps(appContext).isEmpty()) {
        try {
          FirebaseApp.initializeApp(appContext)
        } catch (e: Exception) {
          Log.w("GoogleAuthManager", "Default initializeApp failed, fallback with options: ${e.message}")
          try {
            val options = FirebaseOptions.Builder()
              .setApplicationId("1:703490968047:android:a1c0d16ff63d76e33d0628")
              .setProjectId("tensecchallenge-nvqkz")
              .setApiKey("dummy-auth-key")
              .build()
            FirebaseApp.initializeApp(appContext, options)
          } catch (e2: Exception) {
            lastInitError = e2.message
            Log.e("GoogleAuthManager", "Firebase initialize options failed", e2)
          }
        }
      }
      FirebaseAuth.getInstance()
    } catch (e: Throwable) {
      lastInitError = e.message
      Log.e("GoogleAuthManager", "FirebaseAuth.getInstance() failed: ${e.message}", e)
      null
    }
  }

  val currentUser: FirebaseUser?
    get() = getAuth()?.currentUser

  val authStateFlow: Flow<FirebaseUser?> = callbackFlow {
    val firebaseAuth = getAuth()
    if (firebaseAuth != null) {
      val listener = FirebaseAuth.AuthStateListener { auth ->
        trySend(auth.currentUser)
      }
      firebaseAuth.addAuthStateListener(listener)
      trySend(firebaseAuth.currentUser)
      awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    } else {
      trySend(null)
      awaitClose {}
    }
  }

  suspend fun signInWithGoogle(context: Context): Result<FirebaseUser> {
    val firebaseAuth = getAuth(context) ?: return Result.failure(
      IllegalStateException("Firebase başlatılamadı: ${lastInitError ?: "Google Play Hizmetleri veya internet bağlantısını kontrol edin."}")
    )

    return runCatching {
      val credentialManager = CredentialManager.create(context)

      val webClientId = try {
        val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
        if (resId != 0) context.getString(resId) else "703490968047-fvdk38smirt651rgtd6lm7jtnjpbo3m8.apps.googleusercontent.com"
      } catch (_: Exception) {
        "703490968047-fvdk38smirt651rgtd6lm7jtnjpbo3m8.apps.googleusercontent.com"
      }

      val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(webClientId)
        .build()

      val request = GetCredentialRequest.Builder()
        .addCredentialOption(signInWithGoogleOption)
        .build()

      val result = try {
        credentialManager.getCredential(context = context, request = request)
      } catch (e: GetCredentialCancellationException) {
        Log.w("GoogleAuthManager", "GetCredentialCancellationException: ${e.message}", e)
        throw IllegalStateException("Giriş işlemi iptal edildi.")
      } catch (e: GetCredentialException) {
        Log.e("GoogleAuthManager", "CredentialManager hatası: type=${e.type}, message=${e.message}", e)
        val detailedMsg = when {
          e.type.contains("DeveloperError", ignoreCase = true) || e.message?.contains("10") == true ->
            "Google doğrulama hatası (Play Console SHA-1 parmak izi yapılandırmasını kontrol edin)."
          else ->
            "Google hesabı seçilemedi: ${e.message ?: "İşlem iptal edildi"}"
        }
        throw IllegalStateException(detailedMsg)
      }

      val credential = result.credential

      if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
        val idToken = googleIdTokenCredential.idToken
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
        authResult.user ?: error("Google ile kullanıcı doğrulaması yapılamadı.")
      } else {
        error("Bilinmeyen kimlik bilgisi türü: ${credential.type}")
      }
    }
  }

  suspend fun signOut(context: Context) {
    try {
      val credentialManager = CredentialManager.create(context)
      credentialManager.clearCredentialState(ClearCredentialStateRequest())
    } catch (_: Exception) {
    }
    getAuth(context)?.signOut()
  }
}