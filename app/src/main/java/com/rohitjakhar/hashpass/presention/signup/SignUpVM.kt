package com.rohitjakhar.hashpass.presention.signup

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
class SignUpVM @Inject constructor(
    private val loginRepo: LoginRepo
) : ViewModel() {

    var signUpUser: MutableStateFlow<Resource<Unit>> = MutableStateFlow(Resource.Loading())
        private set

    fun signUpUser(email: String, password: String, name: String) {
        viewModelScope.launch(IO) {
            loginRepo.registerUser(email = email, password = password, username = name)
                .collectLatest { resource ->
                    signUpUser.emit(Resource.Loading())
                    signUpUser.emit(resource)
                }
        }
    }
}
