package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.FirestoreLeaderboardEntry
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class LeaderboardRepository(
  private val context: Context? = null,
  private var db: FirebaseFirestore? = null,
  private var auth: FirebaseAuth? = null
) {
  constructor(db: FirebaseFirestore?, auth: FirebaseAuth?) : this(null, db, auth)

  constructor(context: Context) : this(
    context.applicationContext,
    try {
      val appContext = context.applicationContext
      if (FirebaseApp.getApps(appContext).isEmpty()) {
        FirebaseApp.initializeApp(appContext)
      }
      val dbId = try {
        appContext.getString(R.string.firestore_database_id)
      } catch (e: Exception) {
        ""
      }
      if (dbId.isNotBlank() && dbId != "(default)") {
        FirebaseFirestore.getInstance(FirebaseApp.getInstance(), dbId)
      } else {
        FirebaseFirestore.getInstance()
      }
    } catch (e: Throwable) {
      Log.w("LeaderboardRepository", "Firestore not available: ${e.message}")
      null
    },
    try {
      val appContext = context.applicationContext
      if (FirebaseApp.getApps(appContext).isEmpty()) {
        FirebaseApp.initializeApp(appContext)
      }
      FirebaseAuth.getInstance()
    } catch (e: Throwable) {
      Log.w("LeaderboardRepository", "FirebaseAuth not available: ${e.message}")
      null
    }
  )

  fun getDatabase(): FirebaseFirestore? {
    if (db != null) return db
    val appContext = context?.applicationContext
    if (appContext != null && FirebaseApp.getApps(appContext).isEmpty()) {
      try { FirebaseApp.initializeApp(appContext) } catch (_: Throwable) {}
    }
    return try {
      val dbId = try {
        appContext?.getString(R.string.firestore_database_id) ?: ""
      } catch (_: Exception) { "" }
      db = if (dbId.isNotBlank() && dbId != "(default)" && appContext != null && FirebaseApp.getApps(appContext).isNotEmpty()) {
        FirebaseFirestore.getInstance(FirebaseApp.getInstance(), dbId)
      } else {
        FirebaseFirestore.getInstance()
      }
      db
    } catch (e: Throwable) {
      Log.w("LeaderboardRepository", "Firestore not available: ${e.message}")
      null
    }
  }

  fun getFirebaseAuth(): FirebaseAuth? {
    if (auth != null) return auth
    val appContext = context?.applicationContext
    if (appContext != null && FirebaseApp.getApps(appContext).isEmpty()) {
      try { FirebaseApp.initializeApp(appContext) } catch (_: Throwable) {}
    }
    return try {
      auth = FirebaseAuth.getInstance()
      auth
    } catch (e: Throwable) {
      Log.w("LeaderboardRepository", "FirebaseAuth not available: ${e.message}")
      null
    }
  }

  fun requireUserId(): String {
    return getFirebaseAuth()?.currentUser?.uid ?: error("Kullanıcı giriş yapmamış.")
  }

  suspend fun submitScore(
    userName: String,
    avatarId: String,
    avatarEmoji: String,
    brainScore: Int,
    level: Int,
    city: String
  ): Result<Unit> {
    val database = getDatabase() ?: return Result.failure(IllegalStateException("Bulut veritabanı hazır değil"))
    val uid = try {
      requireUserId()
    } catch (e: Exception) {
      return Result.failure(e)
    }

    val path = "leaderboard/$uid"
    return try {
      val payload = mapOf(
        "userId" to uid,
        "userName" to userName.trim().ifBlank { "Oyuncu" }.take(30),
        "avatarId" to avatarId.ifBlank { "default" }.take(30),
        "avatarEmoji" to avatarEmoji.ifBlank { "🧑‍🚀" }.take(10),
        "brainScore" to brainScore.coerceIn(0, 50000),
        "level" to level.coerceIn(1, 100),
        "city" to city.trim().ifBlank { "İstanbul" }.take(30),
        "updatedAt" to FieldValue.serverTimestamp()
      )

      database.collection("leaderboard").document(uid).set(payload).await()
      Result.success(Unit)
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.WRITE, path)
      Result.failure(e)
    }
  }

  fun observeTopLeaderboard(limit: Long = 50): Flow<List<FirestoreLeaderboardEntry>> {
    val database = getDatabase() ?: return emptyFlow()
    return database.collection("leaderboard")
      .orderBy("brainScore", Query.Direction.DESCENDING)
      .limit(limit)
      .snapshots()
      .map { snapshot ->
        snapshot.documents.mapNotNull { doc ->
          val uid = doc.getString("userId") ?: doc.id
          val userName = doc.getString("userName") ?: "Oyuncu"
          val avatarId = doc.getString("avatarId") ?: "default"
          val avatarEmoji = doc.getString("avatarEmoji") ?: "🧑‍🚀"
          val brainScore = (doc.getLong("brainScore") ?: 0L).toInt()
          val level = (doc.getLong("level") ?: 1L).toInt()
          val city = doc.getString("city") ?: "İstanbul"
          val updatedAt = doc.getTimestamp("updatedAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)

          FirestoreLeaderboardEntry(
            userId = uid,
            userName = userName,
            avatarId = avatarId,
            avatarEmoji = avatarEmoji,
            brainScore = brainScore,
            level = level,
            city = city,
            updatedAt = updatedAt
          )
        }
      }
  }

  fun observeUserEntry(userId: String): Flow<FirestoreLeaderboardEntry?> {
    val database = getDatabase() ?: return emptyFlow()
    return database.collection("leaderboard").document(userId)
      .snapshots()
      .map { doc ->
        if (!doc.exists()) return@map null
        val uid = doc.getString("userId") ?: doc.id
        val userName = doc.getString("userName") ?: "Oyuncu"
        val avatarId = doc.getString("avatarId") ?: "default"
        val avatarEmoji = doc.getString("avatarEmoji") ?: "🧑‍🚀"
        val brainScore = (doc.getLong("brainScore") ?: 0L).toInt()
        val level = (doc.getLong("level") ?: 1L).toInt()
        val city = doc.getString("city") ?: "İstanbul"
        val updatedAt = doc.getTimestamp("updatedAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)

        FirestoreLeaderboardEntry(
          userId = uid,
          userName = userName,
          avatarId = avatarId,
          avatarEmoji = avatarEmoji,
          brainScore = brainScore,
          level = level,
          city = city,
          updatedAt = updatedAt
        )
      }
  }
}
