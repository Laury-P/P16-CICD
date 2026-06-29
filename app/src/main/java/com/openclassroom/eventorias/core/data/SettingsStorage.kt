package com.openclassroom.eventorias.core.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsStorage (private val context: Context) {

    companion object {
        private val ALL_NOTIFICATIONS_KEY = booleanPreferencesKey("all_notifications_enabled")
    }

    val isNotificationsEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[ALL_NOTIFICATIONS_KEY] == true
        }

    suspend fun setNotificationsEnabled(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ALL_NOTIFICATIONS_KEY] = isEnabled
        }
    }
}
