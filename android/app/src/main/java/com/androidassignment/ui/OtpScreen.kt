package com.androidassignment.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun OtpScreen(
  email: String,
  secondsLeft: Int,
  attemptsLeft: Int,
  resendCooldownSecondsLeft: Int,
  debugOtp: String?,
  error: String?,
  onTick: () -> Unit,
  onVerify: (String) -> Unit,
  onResend: () -> Unit,
  onChangeEmail: () -> Unit,
  onClearError: () -> Unit,
) {
  var otp by rememberSaveable { mutableStateOf("") }
  val focusManager = LocalFocusManager.current
  val scrollState = rememberScrollState()
  val otpFocusRequester = FocusRequester()

  LaunchedEffect(secondsLeft, email) {
    if (secondsLeft <= 0) return@LaunchedEffect
    delay(1000)
    onTick()
  }

  LaunchedEffect(email) {
    otpFocusRequester.requestFocus()
  }

  Column(
    modifier =
      Modifier
        .fillMaxSize()
        .navigationBarsPadding()
        .imePadding()
        .verticalScroll(scrollState)
        .padding(20.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Text(text = "Enter OTP", style = MaterialTheme.typography.headlineSmall)
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(),
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Text(text = "Email", style = MaterialTheme.typography.labelMedium)
        Text(text = email, style = MaterialTheme.typography.bodyLarge)
        Text(text = "Expires in: ${secondsLeft}s", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Attempts left: $attemptsLeft", style = MaterialTheme.typography.bodyMedium)
        if (resendCooldownSecondsLeft > 0) {
          Text(text = "Resend available in: ${resendCooldownSecondsLeft}s", style = MaterialTheme.typography.bodyMedium)
        }
        if (debugOtp != null) {
          Text(text = "Dev OTP: $debugOtp", style = MaterialTheme.typography.bodySmall)
        }
        LinearProgressIndicator(
          progress = { (secondsLeft.coerceIn(0, 60) / 60f) },
        )

        OutlinedTextField(
          modifier = Modifier.fillMaxWidth().focusRequester(otpFocusRequester),
          value = otp,
          onValueChange = {
            otp = it.filter(Char::isDigit).take(6)
            if (error != null) onClearError()
          },
          label = { Text("6-digit OTP") },
          keyboardOptions =
            KeyboardOptions(
              keyboardType = KeyboardType.NumberPassword,
              imeAction = ImeAction.Done,
            ),
          keyboardActions = KeyboardActions(
            onDone = {
              val canVerify = otp.trim().length == 6 && secondsLeft > 0 && attemptsLeft > 0
              if (canVerify) {
                focusManager.clearFocus()
                onVerify(otp)
              } else {
                focusManager.clearFocus()
              }
            },
          ),
          singleLine = true,
        )

        if (error != null) {
          Text(text = error, color = MaterialTheme.colorScheme.error)
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          val canResend = (secondsLeft <= 0 || attemptsLeft <= 0) && resendCooldownSecondsLeft <= 0
          Button(
            onClick = {
              focusManager.clearFocus()
              onResend()
            },
            enabled = canResend,
            modifier = Modifier.weight(1f),
          ) {
            val label =
              if (resendCooldownSecondsLeft > 0) {
                "Resend (${resendCooldownSecondsLeft}s)"
              } else {
                "Resend"
              }
            Text(label)
          }
          Button(
            onClick = onChangeEmail,
            modifier = Modifier.weight(1f),
          ) { Text("Change Email") }
        }

        val canVerify = otp.trim().length == 6 && secondsLeft > 0 && attemptsLeft > 0
        Button(
          onClick = {
            focusManager.clearFocus()
            onVerify(otp)
          },
          enabled = canVerify,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Text("Verify OTP")
        }
      }
    }
  }
}
