package com.rohitjakhar.hashpass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohitjakhar.hashpass.data.remote.LoginRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val loginRepo: LoginRepo
) : ViewModel() {

    init {
        checkLoginState()
    }

    var loginState: MutableStateFlow<Boolean> = MutableStateFlow(false)
        private set

    private fun checkLoginState() {
        viewModelScope.launch(IO) {
            loginRepo.checkLogin().collectLatest {
                loginState.emit(it)
            }
        }
    }
}
