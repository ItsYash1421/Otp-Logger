package com.androidassignment.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
  primary = Color(0xFF3F51B5),
  secondary = Color(0xFF03DAC6),
  background = Color(0xFFF7F7FB),
  surface = Color(0xFFFFFFFF),
)

private val DarkColors = darkColorScheme(
  primary = Color(0xFF9FA8DA),
  secondary = Color(0xFF66FFF9),
  background = Color(0xFF000000),
  surface = Color(0xFF121212),
)

@Composable
fun AndroidAssignmentTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val view = LocalView.current
  SideEffect {
    val window = (view.context as? android.app.Activity)?.window ?: return@SideEffect
    WindowCompat.setDecorFitsSystemWindows(window, true)
  }

  MaterialTheme(
    colorScheme = if (darkTheme) DarkColors else LightColors,
    typography = Typography,
    content = content,
  )
}
