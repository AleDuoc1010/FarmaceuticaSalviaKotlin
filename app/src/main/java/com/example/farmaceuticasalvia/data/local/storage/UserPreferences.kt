package com.example.farmaceuticasalvia.data.local.storage

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences (private val context: Context){

    companion object {
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
        private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_UUID_KEY = stringPreferencesKey("user_uuid")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_ROLE_KEY = stringPreferencesKey("user_role")
    }

    suspend fun  saveUserSession(token: String, uuid: String, name: String, email: String, role: String) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN_KEY] = true
            prefs[AUTH_TOKEN_KEY] = token
            prefs[USER_UUID_KEY] = uuid
            prefs[USER_NAME_KEY] = name
            prefs[USER_EMAIL_KEY] = email
            prefs[USER_ROLE_KEY] = role
        }
    }

    suspend fun clearSession(){
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[IS_LOGGED_IN_KEY] ?: false }
    val authToken: Flow<String?> = context.dataStore.data.map { it[AUTH_TOKEN_KEY] }
    val userUuid: Flow<String?> = context.dataStore.data.map { it[USER_UUID_KEY] }
    val userRole: Flow<String?> = context.dataStore.data.map { it[USER_ROLE_KEY] }
}