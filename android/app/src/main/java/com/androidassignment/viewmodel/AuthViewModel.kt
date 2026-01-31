package com.androidassignment.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.androidassignment.analytics.AnalyticsLogger
import com.androidassignment.data.OtpManager
import com.androidassignment.data.OtpValidateResult
import com.androidassignment.data.SessionData
import com.androidassignment.data.SessionStore
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
  application: Application,
  private val otpManager: OtpManager,
  private val sessionStore: SessionStore,
  private val analyticsLogger: AnalyticsLogger,
) : AndroidViewModel(application) {
  private val _state = MutableStateFlow<AuthState>(AuthState.Login())
  val state: StateFlow<AuthState> = _state.asStateFlow()

  constructor(application: Application) : this(
    application = application,
    otpManager = OtpManager(),
    sessionStore = SessionStore(application.applicationContext),
    analyticsLogger = AnalyticsLogger(),
  )

  init {
    viewModelScope.launch {
      sessionStore.sessionFlow.distinctUntilChanged().collect { session ->
        val current = _state.value
        if (session == null) {
          if (current is AuthState.Session) _state.value = AuthState.Login()
          return@collect
        }
        if (current !is AuthState.Session) {
          _state.value = AuthState.Session(email = session.email, startTime = session.startTime)
        }
      }
    }
  }

  fun sendOtp(email: String) {
    val normalized = email.trim().lowercase()
    if (!isValidEmail(normalized)) {
      _state.value = AuthState.Login(error = "Enter a valid email.")
      return
    }

    val (otp, snapshot) = otpManager.generate(normalized)
    analyticsLogger.otpGenerated(normalized)
    _state.value = AuthState.Otp(
      email = normalized,
      secondsLeft = snapshot.secondsLeft,
      attemptsLeft = snapshot.attemptsLeft,
      resendCooldownSecondsLeft = snapshot.resendSecondsLeft,
      debugOtp = otp,
      error = null,
    )
  }

  fun tick() {
    val current = _state.value
    if (current !is AuthState.Otp) return

    val snap = otpManager.snapshot(current.email)
    if (snap == null) {
      _state.value = current.copy(secondsLeft = 0, error = "OTP expired. Please resend.")
      return
    }

    _state.value = current.copy(
      secondsLeft = snap.secondsLeft,
      attemptsLeft = snap.attemptsLeft,
      resendCooldownSecondsLeft = snap.resendSecondsLeft,
    )
  }

  fun verifyOtp(inputOtp: String) {
    val current = _state.value
    if (current !is AuthState.Otp) return

    val result = otpManager.validate(current.email, inputOtp)
    when (result) {
      OtpValidateResult.Success -> {
        analyticsLogger.otpValidationSuccess(current.email)
        val startTime = System.currentTimeMillis()
        _state.value = AuthState.Session(email = current.email, startTime = startTime)
        viewModelScope.launch {
          sessionStore.saveSession(SessionData(email = current.email, startTime = startTime))
        }
      }
      OtpValidateResult.NotFound -> {
        analyticsLogger.otpValidationFailure(current.email, "not_found")
        _state.value = current.copy(error = "No active OTP. Please resend.")
      }
      OtpValidateResult.Expired -> {
        analyticsLogger.otpValidationFailure(current.email, "expired")
        _state.value = current.copy(secondsLeft = 0, error = "OTP expired. Please resend.")
      }
      is OtpValidateResult.Locked -> {
        analyticsLogger.otpValidationFailure(current.email, "locked")
        _state.value = current.copy(
          secondsLeft = result.snapshot.secondsLeft,
          attemptsLeft = result.snapshot.attemptsLeft,
          resendCooldownSecondsLeft = result.snapshot.resendSecondsLeft,
          error = "Attempts exceeded. Please resend OTP.",
        )
      }
      is OtpValidateResult.Invalid -> {
        analyticsLogger.otpValidationFailure(current.email, "invalid")
        _state.value = current.copy(
          secondsLeft = result.snapshot.secondsLeft,
          attemptsLeft = result.snapshot.attemptsLeft,
          resendCooldownSecondsLeft = result.snapshot.resendSecondsLeft,
          error = "Wrong OTP.",
        )
      }
    }
  }

  fun resendOtp() {
    val current = _state.value
    if (current !is AuthState.Otp) return
    val (otp, snapshot) = otpManager.generate(current.email)
    analyticsLogger.otpGenerated(current.email)
    _state.value = current.copy(
      secondsLeft = snapshot.secondsLeft,
      attemptsLeft = snapshot.attemptsLeft,
      resendCooldownSecondsLeft = snapshot.resendSecondsLeft,
      debugOtp = otp,
      error = null,
    )
  }

  fun logout() {
    val current = _state.value
    val email = when (current) {
      is AuthState.Login -> null
      is AuthState.Otp -> current.email
      is AuthState.Session -> current.email
    }
    analyticsLogger.logout(email)
    _state.value = AuthState.Login()
    viewModelScope.launch {
      sessionStore.clearSession()
    }
  }

  fun clearError() {
    val current = _state.value
    _state.value = when (current) {
      is AuthState.Login -> current.copy(error = null)
      is AuthState.Otp -> current.copy(error = null)
      is AuthState.Session -> current
    }
  }

  private fun isValidEmail(email: String): Boolean {
    if (email.isBlank()) return false
    return Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(email)
  }
}
