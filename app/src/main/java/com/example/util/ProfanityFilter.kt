package com.example.util

sealed class NicknameValidationResult {
  object Valid : NicknameValidationResult()
  data class Invalid(val errorMessage: String) : NicknameValidationResult()
}

object ProfanityFilter {

  // List of forbidden root words and insults in Turkish and English
  private val forbiddenRoots = listOf(
    // Turkish vulgarities & slurs
    "amk", "aq", "amq", "amına", "amina", "amcik", "amcık", "amck", "amcuk",
    "yarrak", "yarak", "yarram", "yarrag", "yarragim", "sik", "siki", "sikeyim",
    "siktir", "siktirgit", "sikis", "sikiş", "sikik", "sokuk", "sokam",
    "orospu", "orospucocugu", "orospucocu", "orospu cocugu", "orspu", "orosp",
    "oc", "oç", "pic", "piç", "got", "göt", "gotlek", "götlek", "gotveren", "götveren",
    "gavat", "kavat", "kahpe", "ibne", "ibnetor", "pust", "puşt", "fahise", "fahişe",
    "dol", "döl", "tasak", "taşak", "tassak", "taşşak", "yavsak", "yavşak",
    "pezevenk", "kaltak", "kevase", "kevaşe", "surtuk", "sürtük", "dalyarak",
    "siktim", "sikerim", "amguard", "amci", "amcı", "got deli",

    // Common abbreviations
    "sg", "o.c", "o.ç", "a.m.k", "a.q",

    // English bad words
    "fuck", "fucker", "fucking", "shit", "bitch", "asshole", "cunt",
    "bastard", "dick", "pussy", "nigger", "nigga", "faggot", "slut", "whore"
  )

  /**
   * Normalizes text by lowercasing, converting Turkish characters,
   * replacing leetspeak digits/symbols, and collapsing repeating letters.
   */
  fun normalizeText(input: String): String {
    var text = input.trim().lowercase()

    // Turkish character replacement
    text = text
      .replace('ı', 'i')
      .replace('İ', 'i')
      .replace('ğ', 'g')
      .replace('ü', 'u')
      .replace('ş', 's')
      .replace('ö', 'o')
      .replace('ç', 'c')

    // Common leetspeak replacements
    text = text
      .replace('0', 'o')
      .replace('1', 'i')
      .replace('3', 'e')
      .replace('4', 'a')
      .replace('5', 's')
      .replace('7', 't')
      .replace('8', 'b')
      .replace('@', 'a')
      .replace('$', 's')
      .replace('!', 'i')

    return text
  }

  /**
   * Collapses repeating consecutive characters (e.g. "siiiikkk" -> "sik").
   */
  private fun collapseRepeatedLetters(input: String): String {
    if (input.isEmpty()) return ""
    val sb = StringBuilder()
    var lastChar = input[0]
    sb.append(lastChar)
    for (i in 1 until input.length) {
      val c = input[i]
      if (c != lastChar) {
        sb.append(c)
        lastChar = c
      }
    }
    return sb.toString()
  }

  /**
   * Checks whether the input string contains any prohibited words/slurs.
   */
  fun containsProfanity(rawText: String): Boolean {
    if (rawText.isBlank()) return false

    val normalized = normalizeText(rawText)
    // Strip all non-alphanumeric characters (spaces, underscores, dots, hyphens)
    val alphanumericOnly = normalized.filter { it.isLetter() }
    val collapsed = collapseRepeatedLetters(alphanumericOnly)

    // 1. Direct word check against tokens separated by spaces or punctuation
    val words = normalized.split(Regex("[^a-z]+")).filter { it.isNotBlank() }
    for (word in words) {
      val collapsedWord = collapseRepeatedLetters(word)
      for (forbidden in forbiddenRoots) {
        if (word == forbidden || collapsedWord == forbidden) {
          return true
        }
      }
    }

    // 2. Substring check on the stripped and collapsed string
    for (forbidden in forbiddenRoots) {
      // For short words (2-3 chars), check exact matches or word boundary to prevent false positives (e.g. "oc", "sg")
      if (forbidden.length <= 2) {
        if (words.contains(forbidden)) return true
      } else {
        if (alphanumericOnly.contains(forbidden) || collapsed.contains(forbidden)) {
          return true
        }
      }
    }

    return false
  }

  /**
   * Validates a candidate nickname.
   */
  fun validateNickname(name: String): NicknameValidationResult {
    val trimmed = name.trim()
    if (trimmed.isBlank()) {
      return NicknameValidationResult.Invalid("Takma ad boş bırakılamaz.")
    }
    if (trimmed.length < 2) {
      return NicknameValidationResult.Invalid("Takma ad en az 2 karakter olmalıdır.")
    }
    if (trimmed.length > 16) {
      return NicknameValidationResult.Invalid("Takma ad en fazla 16 karakter olabilir.")
    }
    if (containsProfanity(trimmed)) {
      return NicknameValidationResult.Invalid("Uygunsuz veya küfürlü kelimeler kullanılamaz!")
    }
    return NicknameValidationResult.Valid
  }
}
