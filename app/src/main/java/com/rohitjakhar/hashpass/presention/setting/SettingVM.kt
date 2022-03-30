package com.rohitjakhar.hashpass.presention.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohitjakhar.hashpass.data.model.UserDetailsModel
import com.rohitjakhar.hashpass.data.remote.LoginRepo
import com.rohitjakhar.hashpass.data.remote.RemoteRepo
import com.rohitjakhar.hashpass.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingVM @Inject constructor(
    private val loginRepo: LoginRepo,
    private val remoteRepo: RemoteRepo
) : ViewModel() {
    var logoutState =
        MutableStateFlow<Resource<Boolean>>(Resource.Loading())
        private set

    var notificationState = MutableStateFlow<Boolean>(true)
        private set

    var userDetails = MutableStateFlow<UserDetailsModel?>(null)
        private set

    fun logout() {
        viewModelScope.launch(IO) {
            loginRepo.logoutUser().collectLatest {
                logoutState.emit(Resource.Loading())
                logoutState.emit(it)
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch(IO) {
            logoutState.emit(Resource.Loading())
            loginRepo.deleteUserAccount().collectLatest {
                logoutState.emit(it)
            }
        }
    }

    fun getUserDetails() {
        viewModelScope.launch(IO) {
            userDetails.emit(loginRepo.getUserDetails())
        }
    }

    fun getNotification() {
        viewModelScope.launch(IO) {
            notificationState.emit(remoteRepo.getNotificationStatus())
        }
    }

    fun changeNotification(notificationOn: Boolean) {
        viewModelScope.launch(IO) {
            remoteRepo.changeNotification(notificationOn)
        }
    }
}
