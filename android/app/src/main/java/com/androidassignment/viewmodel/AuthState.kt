package com.androidassignment.viewmodel

sealed interface AuthState {
  data class Login(val error: String? = null) : AuthState

  data class Otp(
    val email: String,
    val secondsLeft: Int,
    val attemptsLeft: Int,
    val resendCooldownSecondsLeft: Int = 0,
    val debugOtp: String? = null,
    val error: String? = null,
  ) : AuthState

  data class Session(
    val email: String,
    val startTime: Long,
  ) : AuthState
}
