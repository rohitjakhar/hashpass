package com.rohitjakhar.hashpass.presention.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohitjakhar.hashpass.data.remote.LoginRepo
import com.rohitjakhar.hashpass.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingVM @Inject constructor(
    private val loginRepo: LoginRepo
) : ViewModel() {
    var logoutState =
        MutableStateFlow<Resource<Boolean>>(Resource.Loading())
        private set

    fun logout() {
        viewModelScope.launch(IO) {
            loginRepo.logoutUser().collectLatest {
                logoutState.emit(it)
            }
        }
    }
}
