package com.example

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.repository.LeaderboardRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LeaderboardRepositoryRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun testSubmitScoreSucceedsWhenAuthenticated() = runBlocking {
    val uid = signInTestUser("user1@example.com")
    val repository = LeaderboardRepository(firestore, auth)

    val result = repository.submitScore(
      userName = "Ahmet",
      avatarId = "ninja",
      avatarEmoji = "🥷",
      brainScore = 4500,
      level = 8,
      city = "İstanbul"
    )

    assertTrue("Score submission should succeed", result.isSuccess)

    val entry = repository.observeUserEntry(uid).first()
    assertNotNull(entry)
    assertEquals(uid, entry?.userId)
    assertEquals("Ahmet", entry?.userName)
    assertEquals(4500, entry?.brainScore)
    assertEquals("İstanbul", entry?.city)
  }

  @Test
  fun testSubmitScoreFailsWhenUnauthenticated() = runBlocking {
    auth.signOut()
    val repository = LeaderboardRepository(firestore, auth)

    val result = repository.submitScore(
      userName = "Anon",
      avatarId = "default",
      avatarEmoji = "🧑‍🚀",
      brainScore = 1000,
      level = 1,
      city = "Ankara"
    )

    assertTrue("Score submission should fail when unauthenticated", result.isFailure)
  }

  @Test
  fun testLeaderboardOrdering() = runBlocking {
    val user1 = signInTestUser("p1@example.com")
    val repo1 = LeaderboardRepository(firestore, auth)
    repo1.submitScore("PlayerLow", "default", "🧑‍🚀", 1200, 3, "İzmir")

    auth.signOut()
    val user2 = signInTestUser("p2@example.com")
    val repo2 = LeaderboardRepository(firestore, auth)
    repo2.submitScore("PlayerHigh", "ninja", "🥷", 9800, 10, "Ankara")

    val list = repo2.observeTopLeaderboard().first()
    assertTrue(list.size >= 2)
    val firstEntry = list.first()
    assertEquals("PlayerHigh", firstEntry.userName)
    assertEquals(9800, firstEntry.brainScore)
  }
}
