package com.rohitjakhar.hashpass.presention.add_password

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
class AddPasswordVM @Inject constructor(
    private val remoteRepo: RemoteRepo
) : ViewModel() {
    var addPasswordStatus = MutableStateFlow<Resource<Unit>>(Resource.Loading())
        private set

    fun addPassword(passwordModel: PasswordModel) {
        viewModelScope.launch(IO) {
            addPasswordStatus.emit(Resource.Loading())
            addPasswordStatus.emit(remoteRepo.addPassword(passwordModel))
        }
    }

    fun updatePassword(passwordModel: PasswordModel) {
        viewModelScope.launch(IO) {
            addPasswordStatus.emit(Resource.Loading())
            addPasswordStatus.emit(remoteRepo.updatePassword(passwordModel))
        }
    }

    fun generatePassword(): String {
        var password = ""
        val list = ArrayList<Int>()
        list.add(0)
        list.add(1)
        list.add(2)
        list.add(3)

        for (i in 1..8) {
            when (list.random()) {
                0 -> password += ('A'..'Z').random().toString()
                1 -> password += ('a'..'z').random().toString()
                2 -> password += ('0'..'9').random().toString()
                3 -> password += listOf(
                    '!',
                    '@',
                    '#',
                    '$',
                    '%',
                    '&',
                    '*',
                    '+',
                    '=',
                    '-',
                    '~',
                    '?',
                    '/',
                    '_'
                ).random().toString()
            }
        }
        return password
    }
}
