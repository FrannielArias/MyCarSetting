package edu.ucne.loginapi.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.ucne.loginapi.domain.model.SessionInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.sessionDataStore by preferencesDataStore(name = "session_prefs")

@Singleton
class SessionDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_ID = intPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
    }

    fun getSession(): Flow<SessionInfo> {
        return context.sessionDataStore.data
            .catch { exception ->
                if (exception is IOException) emit(emptyPreferences()) else throw exception
            }
            .map { prefs: Preferences ->
                SessionInfo(
                    isLoggedIn = prefs[Keys.IS_LOGGED_IN] ?: false,
                    userId = prefs[Keys.USER_ID],
                    userName = prefs[Keys.USER_NAME]
                )
            }
    }

    suspend fun saveSession(info: SessionInfo) {
        context.sessionDataStore.edit { prefs ->
            prefs[Keys.IS_LOGGED_IN] = info.isLoggedIn
            if (info.userId != null) prefs[Keys.USER_ID] =
                info.userId else prefs.remove(Keys.USER_ID)
            if (info.userName != null) prefs[Keys.USER_NAME] =
                info.userName else prefs.remove(Keys.USER_NAME)
        }
    }

    suspend fun clearSession() {
        context.sessionDataStore.edit { prefs -> prefs.clear() }
    }

}
