package com.example.data.auth

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class GoogleAuthManager(private val initialContext: Context? = null) {

  fun getAuth(context: Context? = initialContext): FirebaseAuth? {
    return try {
      val appContext = context?.applicationContext ?: initialContext?.applicationContext
      if (appContext != null && FirebaseApp.getApps(appContext).isEmpty()) {
        FirebaseApp.initializeApp(appContext)
      }
      FirebaseAuth.getInstance()
    } catch (e: Throwable) {
      Log.w("GoogleAuthManager", "FirebaseAuth not available: ${e.message}")
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
      IllegalStateException("Firebase başlatılamadı. Lütfen Google Play Hizmetlerini ve internetinizi kontrol edin.")
    )

    return runCatching {
      val credentialManager = CredentialManager.create(context)
      
      // Attempt to retrieve Web Client ID from generated resources or fallback constant
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
        throw IllegalStateException("Giriş işlemi iptal edildi.")
      } catch (e: GetCredentialException) {
        Log.e("GoogleAuthManager", "CredentialManager hatası: ${e.message}", e)
        throw IllegalStateException("Google hesabı seçilemedi: ${e.message ?: "İşlem iptal edildi"}")
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
