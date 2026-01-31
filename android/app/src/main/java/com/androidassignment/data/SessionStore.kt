package com.androidassignment.data

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class SessionData(
  val email: String,
  val startTime: Long,
)

class SessionStore(context: Context) {
  private val dataStore =
    PreferenceDataStoreFactory.create(
      produceFile = { context.preferencesDataStoreFile("session.preferences_pb") },
    )

  val sessionFlow: Flow<SessionData?> =
    dataStore.data.map { prefs ->
      val email = prefs[Keys.EMAIL] ?: return@map null
      val startTime = prefs[Keys.START_TIME] ?: return@map null
      SessionData(email = email, startTime = startTime)
    }

  suspend fun saveSession(session: SessionData) {
    dataStore.edit { prefs ->
      prefs[Keys.EMAIL] = session.email
      prefs[Keys.START_TIME] = session.startTime
    }
  }

  suspend fun clearSession() {
    dataStore.edit { prefs ->
      prefs.remove(Keys.EMAIL)
      prefs.remove(Keys.START_TIME)
    }
  }

  private object Keys {
    val EMAIL = stringPreferencesKey("email")
    val START_TIME = longPreferencesKey("start_time")
  }
}

