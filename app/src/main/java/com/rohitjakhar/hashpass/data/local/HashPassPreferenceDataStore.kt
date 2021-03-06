package com.rohitjakhar.hashpass.data.local

import kotlinx.coroutines.flow.Flow

interface HashPassPreferenceDataStore {
    val isLogin: Flow<Boolean>
    val userName: Flow<String>
    val userEmail: Flow<String>
    val userImage: Flow<String>
    val userId: Flow<String>
    val notificationOn: Flow<Boolean>
    val isFingerLockOn: Flow<Boolean>

    suspend fun changeLogin(isLogin: Boolean)
    suspend fun updateName(userName: String)
    suspend fun updateEmail(email: String)
    suspend fun updateImage(imageLink: String)
    suspend fun updateId(userId: String)
    suspend fun updateNotification(isNotificationOn: Boolean)
    suspend fun changeFingerLock(isFingerEnable: Boolean)
}
