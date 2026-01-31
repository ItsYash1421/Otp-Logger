package com.androidassignment.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun SessionScreen(
  email: String,
  startTime: Long,
  onLogout: () -> Unit,
) {
  var now by remember { mutableStateOf(System.currentTimeMillis()) }

  LaunchedEffect(startTime) {
    while (true) {
      delay(1000)
      now = System.currentTimeMillis()
    }
  }

  val duration = formatDurationMs(now - startTime)
  val startTimeText = formatDateTime(startTime)

  Column(
    modifier = Modifier.fillMaxSize().navigationBarsPadding().padding(20.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Text(text = "Session", style = MaterialTheme.typography.headlineSmall)
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(),
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Text(text = "Email", style = MaterialTheme.typography.labelMedium)
        Text(text = email, style = MaterialTheme.typography.bodyLarge)
        Text(text = "Start time", style = MaterialTheme.typography.labelMedium)
        Text(text = startTimeText, style = MaterialTheme.typography.bodyLarge)
      }
    }

    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(),
    ) {
      Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(text = "Live duration", style = MaterialTheme.typography.labelMedium)
          Text(text = duration, style = MaterialTheme.typography.displaySmall)
        }
      }
    }

    Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) { Text("Logout") }
  }
}
