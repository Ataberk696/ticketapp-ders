package com.turkcell.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

// Single Source garantilemek.
private val Context.authDataStore by preferencesDataStore(name = "auth_prefs")

class TokenStore(private val context: Context)
{
    private object Keys {
        val ACCESS = stringPreferencesKey("access_token")
        val REFRESH = stringPreferencesKey("refresh_token")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_ROLE = stringPreferencesKey("user_role")
    }

    // UI tarafından collect edilmek için
    val accessToken: Flow<String?> = context.authDataStore.data.map { it[Keys.ACCESS] }
    val refreshToken: Flow<String?> = context.authDataStore.data.map { it[Keys.REFRESH] }

    val userId: Flow<String?> = context.authDataStore.data.map { it[Keys.USER_ID] }
    val userEmail: Flow<String?> = context.authDataStore.data.map { it[Keys.USER_EMAIL] }
    val userRole: Flow<String?> = context.authDataStore.data.map { it[Keys.USER_ROLE] }


    /*
    suspend fun saveUser(id: String, email: String,  role: String){
        context.authDataStore.edit {
            prefs ->
                prefs[Keys.USER_ID] = id
                prefs[Keys.USER_EMAIL] = email
                prefs[Keys.USER_ROLE] = role
        }
    }
    */


    suspend fun saveAll(
        access: String,
        refresh: String,
        userId: String,
        userEmail: String,
        userRole: String
    ) {
        context.authDataStore.edit { prefs ->
            prefs[Keys.ACCESS] = access
            prefs[Keys.REFRESH] = refresh
            prefs[Keys.USER_ID] = userId
            prefs[Keys.USER_EMAIL] = userEmail
            prefs[Keys.USER_ROLE] = userRole
        }
    }

    suspend fun clear() {
        context.authDataStore.edit { prefs ->
            prefs.remove(Keys.ACCESS)
            prefs.remove(Keys.REFRESH)
            prefs.remove(Keys.USER_ID)
            prefs.remove(Keys.USER_EMAIL)
            prefs.remove(Keys.USER_ROLE)
        }
    }

    fun accessTokenBlocking(): String? = runBlocking { accessToken.first() }
    fun refreshTokenBlocking(): String? = runBlocking { refreshToken.first() }
    fun saveBlocking(access: String, refresh: String) = runBlocking {
        context.authDataStore.edit { prefs ->
            prefs[Keys.ACCESS] = access
            prefs[Keys.REFRESH] = refresh
        }
    }
    fun clearBlocking() = runBlocking { clear() }

}