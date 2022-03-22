package com.rohitjakhar.hashpass.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
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

    override val userEmail: Flow<String>
        get() = dataStore.data.map {
            it[PREF_USER_EMAIL] ?: ""
        }

    override val userId: Flow<String>
        get() = dataStore.data.map {
            it[PREF_USER_ID] ?: ""
        }

    override val userImage: Flow<String>
        get() = dataStore.data.map {
            it[PREF_USER_IMAGE] ?: ""
        }

    override val userName: Flow<String>
        get() = dataStore.data.map {
            it[PREF_USER_NAME] ?: ""
        }

    override val notificationOn: Flow<Boolean>
        get() = dataStore.data.map {
            it[PREF_NOTIFICATION] ?: true
        }

    override suspend fun updateNotification(isNotificationOn: Boolean) {
        dataStore.edit {
            it[PREF_NOTIFICATION] = isNotificationOn
        }
    }

    override suspend fun changeLogin(isLogin: Boolean) {
        dataStore.edit {
            it[PREF_IS_LOGIN] = isLogin
        }
    }

    override suspend fun updateName(userName: String) {
        dataStore.edit {
            it[PREF_USER_NAME] = userName
        }
    }

    override suspend fun updateEmail(email: String) {
        dataStore.edit {
            it[PREF_USER_EMAIL] = email
        }
    }

    override suspend fun updateImage(imageLink: String) {
        dataStore.edit {
            it[PREF_USER_IMAGE] = imageLink
        }
    }

    override suspend fun updateId(userId: String) {
        dataStore.edit {
            it[PREF_USER_ID] = userId
        }
    }

    companion object {
        private val PREF_IS_LOGIN = booleanPreferencesKey("isLogin")
        private val PREF_USER_NAME = stringPreferencesKey("user_name")
        private val PREF_USER_EMAIL = stringPreferencesKey("user_email")
        private val PREF_USER_ID = stringPreferencesKey("user_id")
        private val PREF_USER_IMAGE = stringPreferencesKey("user_image")
        private val PREF_NOTIFICATION = booleanPreferencesKey("notification")
    }
}
