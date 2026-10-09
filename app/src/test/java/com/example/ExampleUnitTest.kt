package com.example

import com.example.util.NicknameValidationResult
import com.example.util.ProfanityFilter
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun profanityFilter_detectsProfanities() {
    assertTrue(ProfanityFilter.containsProfanity("amk"))
    assertTrue(ProfanityFilter.containsProfanity("orospu"))
    assertTrue(ProfanityFilter.containsProfanity("siktir"))
    assertTrue(ProfanityFilter.containsProfanity("piç"))
    assertTrue(ProfanityFilter.containsProfanity("yarrak"))
    assertTrue(ProfanityFilter.containsProfanity("s.i.k"))
    assertTrue(ProfanityFilter.containsProfanity("oç"))

    assertFalse(ProfanityFilter.containsProfanity("Ahmet"))
    assertFalse(ProfanityFilter.containsProfanity("Zehra_06"))
    assertFalse(ProfanityFilter.containsProfanity("Kartal"))
    assertFalse(ProfanityFilter.containsProfanity("Emir"))
  }

  @Test
  fun nicknameValidation_worksCorrectly() {
    assertTrue(ProfanityFilter.validateNickname("Emir") is NicknameValidationResult.Valid)
    assertTrue(ProfanityFilter.validateNickname("Kartal99") is NicknameValidationResult.Valid)

    assertTrue(ProfanityFilter.validateNickname("") is NicknameValidationResult.Invalid)
    assertTrue(ProfanityFilter.validateNickname("a") is NicknameValidationResult.Invalid)
    assertTrue(ProfanityFilter.validateNickname("BuTakmaAdCokFazlaUzunOldu") is NicknameValidationResult.Invalid)
    assertTrue(ProfanityFilter.validateNickname("amk_cocugu") is NicknameValidationResult.Invalid)
  }
}
