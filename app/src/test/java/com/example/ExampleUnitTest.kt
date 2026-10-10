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
  fun profanityFilter_detectsProfanitiesAndSlang() {
    // Küfürler
    assertTrue(ProfanityFilter.containsProfanity("amk"))
    assertTrue(ProfanityFilter.containsProfanity("orospu"))
    assertTrue(ProfanityFilter.containsProfanity("siktir"))
    assertTrue(ProfanityFilter.containsProfanity("piç"))
    assertTrue(ProfanityFilter.containsProfanity("yarrak"))
    assertTrue(ProfanityFilter.containsProfanity("s.i.k"))
    assertTrue(ProfanityFilter.containsProfanity("oç"))

    // Argo ve Hakaretler (salak, aptal, gerizekalı vb.)
    assertTrue(ProfanityFilter.containsProfanity("salak"))
    assertTrue(ProfanityFilter.containsProfanity("aptal"))
    assertTrue(ProfanityFilter.containsProfanity("gerizekalı"))
    assertTrue(ProfanityFilter.containsProfanity("enayi"))
    assertTrue(ProfanityFilter.containsProfanity("dangalak"))
    assertTrue(ProfanityFilter.containsProfanity("ahmak"))
    assertTrue(ProfanityFilter.containsProfanity("moron"))
    assertTrue(ProfanityFilter.containsProfanity("embesil"))
    assertTrue(ProfanityFilter.containsProfanity("şerefsiz"))
    assertTrue(ProfanityFilter.containsProfanity("kaşar"))
    assertTrue(ProfanityFilter.containsProfanity("yavşak"))
    assertTrue(ProfanityFilter.containsProfanity("beyinsiz"))
    assertTrue(ProfanityFilter.containsProfanity("mankafa"))
    assertTrue(ProfanityFilter.containsProfanity("dingil"))
    assertTrue(ProfanityFilter.containsProfanity("pislik"))
    assertTrue(ProfanityFilter.containsProfanity("ezik"))

    // Gizleme varyasyonları
    assertTrue(ProfanityFilter.containsProfanity("s.a.l.a.k"))
    assertTrue(ProfanityFilter.containsProfanity("s@l@k"))
    assertTrue(ProfanityFilter.containsProfanity("s4l4k"))
    assertTrue(ProfanityFilter.containsProfanity("4pt4l"))
    assertTrue(ProfanityFilter.containsProfanity("saalaakk"))

    // Meşru isimler kesinlikle engellenmemeli
    assertFalse(ProfanityFilter.containsProfanity("Ahmet"))
    assertFalse(ProfanityFilter.containsProfanity("Mehmet"))
    assertFalse(ProfanityFilter.containsProfanity("Kemal"))
    assertFalse(ProfanityFilter.containsProfanity("Cemal"))
    assertFalse(ProfanityFilter.containsProfanity("Zehra_06"))
    assertFalse(ProfanityFilter.containsProfanity("Kartal"))
    assertFalse(ProfanityFilter.containsProfanity("Emir"))
    assertFalse(ProfanityFilter.containsProfanity("Can"))
    assertFalse(ProfanityFilter.containsProfanity("Burak"))
    assertFalse(ProfanityFilter.containsProfanity("Elif"))
    assertFalse(ProfanityFilter.containsProfanity("Deniz"))
    assertFalse(ProfanityFilter.containsProfanity("Aslan"))
  }

  @Test
  fun nicknameValidation_worksCorrectly() {
    assertTrue(ProfanityFilter.validateNickname("Emir") is NicknameValidationResult.Valid)
    assertTrue(ProfanityFilter.validateNickname("Kartal99") is NicknameValidationResult.Valid)
    assertTrue(ProfanityFilter.validateNickname("Kemal_TR") is NicknameValidationResult.Valid)

    assertTrue(ProfanityFilter.validateNickname("") is NicknameValidationResult.Invalid)
    assertTrue(ProfanityFilter.validateNickname("a") is NicknameValidationResult.Invalid)
    assertTrue(ProfanityFilter.validateNickname("BuTakmaAdCokFazlaUzunOldu") is NicknameValidationResult.Invalid)
    assertTrue(ProfanityFilter.validateNickname("salak_cocuk") is NicknameValidationResult.Invalid)
    assertTrue(ProfanityFilter.validateNickname("amk_cocugu") is NicknameValidationResult.Invalid)
    assertTrue(ProfanityFilter.validateNickname("gerizekali") is NicknameValidationResult.Invalid)
  }
}
