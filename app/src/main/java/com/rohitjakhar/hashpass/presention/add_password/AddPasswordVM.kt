package com.rohitjakhar.hashpass.presention.add_password

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
class AddPasswordVM @Inject constructor(
    private val remoteRepo: RemoteRepo
) : ViewModel() {
    var addPasswordStatus = MutableStateFlow<Resource<Unit>>(Resource.Loading())
        private set

    fun addPassword() {
        viewModelScope.launch(IO) {
            addPasswordStatus.emit(remoteRepo.addPassword())
        }
    }
}
