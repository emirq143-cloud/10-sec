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
import kotlinx.coroutines.tasks.await

class GoogleAuthManager(
  private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
  val currentUser: FirebaseUser?
    get() = auth.currentUser

  val authStateFlow: Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
      trySend(firebaseAuth.currentUser)
    }
    auth.addAuthStateListener(listener)
    awaitClose { auth.removeAuthStateListener(listener) }
  }

  suspend fun signInWithGoogle(context: Context): Result<FirebaseUser> {
    return runCatching {
      val credentialManager = CredentialManager.create(context)
      val webClientId = context.getString(R.string.default_web_client_id)

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
        val authResult = auth.signInWithCredential(firebaseCredential).await()
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
    auth.signOut()
  }
}
