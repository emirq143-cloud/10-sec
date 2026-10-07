package com.example.data.auth

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.example.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.tasks.await

class GoogleAuthManager {
  private val auth: FirebaseAuth? = try {
    FirebaseAuth.getInstance()
  } catch (e: Exception) {
    null
  }

  val currentUser: FirebaseUser?
    get() = auth?.currentUser

  val authStateFlow: Flow<FirebaseUser?> = if (auth != null) {
    callbackFlow {
      val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        trySend(firebaseAuth.currentUser)
      }
      auth.addAuthStateListener(listener)
      awaitClose { auth.removeAuthStateListener(listener) }
    }
  } else {
    emptyFlow()
  }

  suspend fun signInWithGoogle(context: Context): Result<FirebaseUser> {
    val firebaseAuth = auth ?: return Result.failure(
      IllegalStateException("Firebase Auth servisi hazır değil.")
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

      val result = credentialManager.getCredential(context = context, request = request)
      val credential = result.credential

      if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
        val idToken = googleIdTokenCredential.idToken
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
        authResult.user ?: error("Firebase kullanıcı doğrulaması başarısız oldu.")
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
    auth?.signOut()
  }
}
