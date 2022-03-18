package com.rohitjakhar.hashpass.data.local

import kotlinx.coroutines.flow.Flow

interface HashPassPreferenceDataStore {
    val isLogin: Flow<Boolean>

    suspend fun changeLogin(isLogin: Boolean)
}
