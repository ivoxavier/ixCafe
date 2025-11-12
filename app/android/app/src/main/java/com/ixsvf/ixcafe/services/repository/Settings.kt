package com.ixsvf.ixcafe.services.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ixsvf.ixcafe.constants.IxCafeConstants
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name= IxCafeConstants.APP_SETTINGS.SETTINGS_FILE_NAME)
class Settings(private val context: Context) {


    private companion object {
        val IS_FIRST_RUN_KEY = booleanPreferencesKey(IxCafeConstants.APP_SETTINGS.IS_FIRST_RUN)
        val AUTH_TOKEN_KEY = stringPreferencesKey(IxCafeConstants.APP_SETTINGS.AUTH_TOKEN_KEY)
        val USER_NAME_KEY = stringPreferencesKey(IxCafeConstants.APP_SETTINGS.USER_NAME_KEY)
        val LOGIN_KEY = stringPreferencesKey(IxCafeConstants.APP_SETTINGS.LOGIN_KEY)
    }

    suspend fun fetchLogin(): String? {
        return context.dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[LOGIN_KEY]
        }.first()


        suspend fun clearLogin() {
            context.dataStore.edit { preferences ->
                preferences.remove(LOGIN_KEY)
            }
        }
    }
}