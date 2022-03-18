package com.rohitjakhar.hashpass.presention.forget_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohitjakhar.hashpass.data.remote.LoginRepo
import com.rohitjakhar.hashpass.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgetPasswordVM @Inject constructor(
    private val loginRepo: LoginRepo
) : ViewModel() {
    private val IOExceptoinHandler = CoroutineExceptionHandler { _, exception ->
    }
    var resetPassword: MutableStateFlow<Resource<Unit>> = MutableStateFlow(Resource.Loading())
        private set

    fun resetPassword(email: String) {
        viewModelScope.launch(IO + IOExceptoinHandler) {
            resetPassword.emit(Resource.Loading())
            loginRepo.forgetPassword(email).collectLatest {
                resetPassword.emit(it)
            }
        }
    }
}
