package com.rohitjakhar.hashpass.presention.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohitjakhar.hashpass.data.model.PasswordModel
import com.rohitjakhar.hashpass.data.remote.RemoteRepo
import com.rohitjakhar.hashpass.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    private val remoteRepo: RemoteRepo
) : ViewModel() {
    var passwordListState = MutableStateFlow<Resource<List<PasswordModel>>>(Resource.Loading())
        private set

    fun getPasswordList() {
        viewModelScope.launch(IO) {
            passwordListState.emit(Resource.Loading())
            passwordListState.emit(remoteRepo.getPasswords())
        }
    }
}
