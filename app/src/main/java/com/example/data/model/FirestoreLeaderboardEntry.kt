package com.example.data.model

import com.google.firebase.Timestamp

data class FirestoreLeaderboardEntry(
  val userId: String = "",
  val userName: String = "",
  val avatarId: String = "default",
  val avatarEmoji: String = "🧑‍🚀",
  val brainScore: Int = 0,
  val level: Int = 1,
  val city: String = "İstanbul",
  val updatedAt: Timestamp? = null
)
