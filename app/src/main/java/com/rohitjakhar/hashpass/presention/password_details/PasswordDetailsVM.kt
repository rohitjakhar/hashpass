package com.rohitjakhar.hashpass.presention.password_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohitjakhar.hashpass.data.remote.RemoteRepo
import com.rohitjakhar.hashpass.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordDetailsVM @Inject constructor(
    private val remoteRepo: RemoteRepo
) : ViewModel() {
    var deleteState = MutableStateFlow<Resource<Unit>>(Resource.Loading())
        private set

    fun deletePassword(passwordId: String) {
        viewModelScope.launch(IO) {
            deleteState.emit(Resource.Loading())
            deleteState.emit(remoteRepo.deletePassword(passwordId))
        }
    }
}
