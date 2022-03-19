package com.rohitjakhar.hashpass.presention.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohitjakhar.hashpass.data.remote.LoginRepo
import com.rohitjakhar.hashpass.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginVM @Inject constructor(
    private val loginRepo: LoginRepo
) : ViewModel() {

    var loginUser: MutableStateFlow<Resource<Unit>> = MutableStateFlow(Resource.Loading())
        private set

    fun loginUser(email: String, password: String) {
        viewModelScope.launch(IO) {
            loginRepo.loginUser(email, password)
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch(IO) {
            loginRepo.loginWithGoogle(idToken)
        }
    }
}
