package com.example.util

sealed class NicknameValidationResult {
  object Valid : NicknameValidationResult()
  data class Invalid(val errorMessage: String) : NicknameValidationResult()
}

object ProfanityFilter {

  // Words that are forbidden as substrings anywhere (min 4 chars to prevent false positives)
  private val forbiddenSubstrings = listOf(
    // Ağır küfürler & cinsel içerikli argo
    "amcik", "amcuk", "amck", "yarrak", "yarak", "yarram", "yarrag",
    "siktir", "siktirgit", "sikis", "sikik", "sokuk", "sokam", "sikerim", "siktim",
    "sikeyim", "sikecem", "sikem", "sikti", "soktu", "sokayim", "sokarlar",
    "orospu", "orospucocugu", "orospucocu", "orspu", "dalyarak", "pezevenk",
    "gotlek", "gotveren", "kaltak", "kevase", "surtuk", "fahise", "tasak", "tassak",
    "yavsak", "gavat", "kavat", "kahpe", "ibnetor", "kasar", "amguard",
    "aminakoy", "amkoyim", "aminkoyim", "aminakoyayim", "gotunu", "sikini", "tasagini",
    "yarakkafa", "gotos", "porno", "hentai", "gotdeligi", "gotkili",

    // Hakaret ve aşağılayıcı kelimeler (kullanıcı talebi: salak, aptal, argo kelimeler genişletildi)
    "salak", "aptal", "gerizekali", "dangalak", "ahmak", "enayi", "mankafa",
    "beyinsiz", "moron", "embesil", "angut", "keriz", "serefsiz", "namussuz",
    "haysiyetsiz", "alcak", "zibidi", "zuppe", "lavuk", "dingil", "kopeksoyu",
    "itinoglu", "hayvanoglu", "suratsiz", "pislik", "suruntu", "yalaka", "cirkin",
    "ahraz", "davar", "kereste", "teneke",

    // English slurs & profanities
    "fuck", "fucker", "fucking", "motherfucker", "bitch", "asshole", "cunt",
    "bastard", "nigger", "nigga", "faggot", "slut", "whore", "dumbass", "retard"
  )

  // Short words / acronyms forbidden as standalone words/tokens
  // (Prevents false positives on names like 'Kemal', 'Cemal', 'Aslan', 'Bora', 'Hitit')
  private val forbiddenExactWords = listOf(
    // Kısa küfür & kısaltmalar
    "amk", "aq", "amq", "oc", "pic", "got", "dol", "sg", "it",
    "mal", "okuz", "hiyar", "ibne", "pust", "sik", "amina",
    "puşt", "piç", "göt", "döl", "öc", "oç", "bok", "çüş",
    "kazma", "odun", "cacik", "hirt", "ezik",

    // İngilizce kısa küfürler
    "dick", "pussy", "shit", "sex", "cum", "cock", "tits", "boobs", "ass", "idiot"
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
      .replace('2', 'z')
      .replace('@', 'a')
      .replace('$', 's')
      .replace('!', 'i')

    return text
  }

  /**
   * Collapses repeating consecutive characters (e.g. "saalaakkk" -> "salak").
   */
  fun collapseRepeatedLetters(input: String): String {
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
    // Strip non-letter chars for substring checks
    val alphanumericOnly = normalized.filter { it.isLetter() }
    val collapsedAlpha = collapseRepeatedLetters(alphanumericOnly)

    // Tokenized words (split by spaces, underscores, numbers, symbols)
    val tokens = normalized.split(Regex("[^a-z]+")).filter { it.isNotBlank() }

    // 1. Check exact forbidden words against tokens
    for (token in tokens) {
      val collapsedToken = collapseRepeatedLetters(token)
      for (exact in forbiddenExactWords) {
        if (token == exact || collapsedToken == exact) {
          return true
        }
      }
      for (sub in forbiddenSubstrings) {
        if (token == sub || collapsedToken == sub) {
          return true
        }
      }
    }

    // 2. Check whole stripped string against forbidden exact words (catches "s.i.k", "a.m.k", "o.ç", "p.i.ç")
    for (exact in forbiddenExactWords) {
      if (alphanumericOnly == exact || collapsedAlpha == exact) {
        return true
      }
    }

    // 3. Check forbidden substrings against alphanumeric and collapsed streams
    for (sub in forbiddenSubstrings) {
      if (alphanumericOnly.contains(sub) || collapsedAlpha.contains(sub)) {
        return true
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
      return NicknameValidationResult.Invalid("Uygunsuz, argo veya küfürlü kelimeler kullanılamaz!")
    }
    return NicknameValidationResult.Valid
  }
}
