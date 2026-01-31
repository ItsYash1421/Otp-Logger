package com.androidassignment.data

import kotlin.math.ceil

data class OtpData(
  val otp: String,
  val createdAt: Long,
  var attemptsLeft: Int,
  var resendAvailableAt: Long,
)

data class OtpSnapshot(
  val createdAt: Long,
  val expiresAt: Long,
  val secondsLeft: Int,
  val attemptsLeft: Int,
  val resendSecondsLeft: Int,
)

sealed interface OtpValidateResult {
  data object Success : OtpValidateResult
  data object NotFound : OtpValidateResult
  data object Expired : OtpValidateResult
  data class Locked(val snapshot: OtpSnapshot) : OtpValidateResult
  data class Invalid(val snapshot: OtpSnapshot) : OtpValidateResult
}

class OtpManager(
  private val ttlMs: Long = 60_000,
  private val maxAttempts: Int = 3,
  private val resendCooldownMs: Long = 5_000,
  private val lockoutResendCooldownMs: Long = 10_000,
) {
  private val otpByEmail = mutableMapOf<String, OtpData>()

  fun generate(email: String, now: Long = System.currentTimeMillis()): Pair<String, OtpSnapshot> {
    val key = normalizeEmail(email)
    val otp = generateOtp()
    val data =
      OtpData(
        otp = otp,
        createdAt = now,
        attemptsLeft = maxAttempts,
        resendAvailableAt = now + resendCooldownMs,
      )
    otpByEmail[key] = data
    return otp to snapshot(data, now)
  }

  fun snapshot(email: String, now: Long = System.currentTimeMillis()): OtpSnapshot? {
    val key = normalizeEmail(email)
    val data = otpByEmail[key] ?: return null
    val snap = snapshot(data, now)
    if (snap.secondsLeft <= 0) {
      otpByEmail.remove(key)
      return null
    }
    return snap
  }

  fun validate(email: String, inputOtp: String, now: Long = System.currentTimeMillis()): OtpValidateResult {
    val key = normalizeEmail(email)
    val data = otpByEmail[key] ?: return OtpValidateResult.NotFound

    val snap = snapshot(data, now)
    if (snap.secondsLeft <= 0) {
      otpByEmail.remove(key)
      return OtpValidateResult.Expired
    }

    if (data.attemptsLeft <= 0) {
      return OtpValidateResult.Locked(snapshot(data, now))
    }

    if (inputOtp.trim() == data.otp) {
      otpByEmail.remove(key)
      return OtpValidateResult.Success
    }

    data.attemptsLeft -= 1
    if (data.attemptsLeft <= 0) {
      data.resendAvailableAt = maxOf(data.resendAvailableAt, now + lockoutResendCooldownMs)
    }
    otpByEmail[key] = data
    val next = snapshot(data, now)
    return if (data.attemptsLeft <= 0) {
      OtpValidateResult.Locked(next)
    } else {
      OtpValidateResult.Invalid(next)
    }
  }

  private fun snapshot(data: OtpData, now: Long): OtpSnapshot {
    val expiresAt = data.createdAt + ttlMs
    val secondsLeft = maxOf(0, ceil((expiresAt - now) / 1000.0).toInt())
    val resendSecondsLeft = maxOf(0, ceil((data.resendAvailableAt - now) / 1000.0).toInt())
    return OtpSnapshot(
      createdAt = data.createdAt,
      expiresAt = expiresAt,
      secondsLeft = secondsLeft,
      attemptsLeft = data.attemptsLeft,
      resendSecondsLeft = resendSecondsLeft,
    )
  }

  private fun normalizeEmail(email: String) = email.trim().lowercase()

  private fun generateOtp(): String = (100000..999999).random().toString()
}
