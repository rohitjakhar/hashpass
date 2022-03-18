package com.rohitjakhar.hashpass.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferenceDataImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : HashPassPreferenceDataStore {
    override val isLogin: Flow<Boolean>
        get() = dataStore.data.map {
            it[PREF_IS_LOGIN] ?: false
        }

    override suspend fun changeLogin(isLogin: Boolean) {
        dataStore.edit {
            it[PREF_IS_LOGIN] = isLogin
        }
    }

    companion object {
        private val PREF_IS_LOGIN = booleanPreferencesKey("isLogin")
    }
}
