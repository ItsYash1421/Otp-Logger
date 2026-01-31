package com.androidassignment.analytics

import timber.log.Timber

class AnalyticsLogger {
  fun otpGenerated(email: String) {
    Timber.d("OTP Generated email=%s", email)
  }

  fun otpValidationSuccess(email: String) {
    Timber.d("OTP Validation Success email=%s", email)
  }

  fun otpValidationFailure(email: String, reason: String) {
    Timber.d("OTP Validation Failure email=%s reason=%s", email, reason)
  }

  fun logout(email: String?) {
    Timber.d("Logout email=%s", email)
  }
}

