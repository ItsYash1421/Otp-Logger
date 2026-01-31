package com.androidassignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.androidassignment.ui.AppRoot
import com.androidassignment.ui.theme.AndroidAssignmentTheme
import com.androidassignment.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      AndroidAssignmentTheme {
        val authViewModel: AuthViewModel = viewModel()
        AppRoot(viewModel = authViewModel)
      }
    }
  }
}
