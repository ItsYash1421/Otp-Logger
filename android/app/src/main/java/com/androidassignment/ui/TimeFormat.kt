package com.androidassignment.ui

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDurationMs(ms: Long): String {
  val totalSeconds = maxOf(0, ms / 1000)
  val minutes = totalSeconds / 60
  val seconds = totalSeconds % 60
  return "%02d:%02d".format(minutes, seconds)
}

fun formatDateTime(epochMs: Long): String {
  return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(epochMs))
}

