package com.androidassignment.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
  error: String?,
  onSendOtp: (String) -> Unit,
  onClearError: () -> Unit,
) {
  var email by rememberSaveable { mutableStateOf("") }
  val focusManager = LocalFocusManager.current
  val scrollState = rememberScrollState()

  Column(
    modifier =
      Modifier
        .fillMaxSize()
        .navigationBarsPadding()
        .imePadding()
        .verticalScroll(scrollState)
        .padding(PaddingValues(20.dp)),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Text(text = "Passwordless Login", style = MaterialTheme.typography.headlineSmall)
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(),
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        OutlinedTextField(
          modifier = Modifier.fillMaxWidth(),
          value = email,
          onValueChange = {
            email = it
            if (error != null) onClearError()
          },
          label = { Text("Email") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
          keyboardActions = KeyboardActions(
            onDone = {
              val trimmed = email.trim()
              if (trimmed.isNotEmpty()) {
                focusManager.clearFocus()
                onSendOtp(trimmed)
              }
            },
          ),
          singleLine = true,
        )
        if (error != null) {
          Text(text = error, color = MaterialTheme.colorScheme.error)
        }
        Button(
          onClick = { onSendOtp(email.trim()) },
          enabled = email.trim().isNotEmpty(),
          modifier = Modifier.fillMaxWidth(),
          colors = ButtonDefaults.buttonColors(),
        ) {
          Text("Send OTP")
        }
      }
    }
  }
}
