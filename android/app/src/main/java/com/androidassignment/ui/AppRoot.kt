package com.androidassignment.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.androidassignment.viewmodel.AuthState
import com.androidassignment.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot(viewModel: AuthViewModel) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  val title = when (state) {
    is AuthState.Login -> "Login"
    is AuthState.Otp -> "OTP"
    is AuthState.Session -> "Session"
  }

  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(title = { Text(title) })
    },
  ) { padding ->
    Surface(
      modifier = Modifier.fillMaxSize().padding(padding),
      color = MaterialTheme.colorScheme.background,
    ) {
      Box {
        when (state) {
          is AuthState.Login -> {
            val s = state as AuthState.Login
            LoginScreen(
              error = s.error,
              onSendOtp = viewModel::sendOtp,
              onClearError = viewModel::clearError,
            )
          }
          is AuthState.Otp -> {
            val s = state as AuthState.Otp
            OtpScreen(
              email = s.email,
              secondsLeft = s.secondsLeft,
              attemptsLeft = s.attemptsLeft,
            resendCooldownSecondsLeft = s.resendCooldownSecondsLeft,
              debugOtp = s.debugOtp,
              error = s.error,
              onTick = viewModel::tick,
              onVerify = viewModel::verifyOtp,
              onResend = viewModel::resendOtp,
              onChangeEmail = viewModel::logout,
              onClearError = viewModel::clearError,
            )
          }
          is AuthState.Session -> {
            val s = state as AuthState.Session
            SessionScreen(
              email = s.email,
              startTime = s.startTime,
              onLogout = viewModel::logout,
            )
          }
        }
      }
    }
  }
}
