package com.rohitjakhar.hashpass.presention.edit_profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohitjakhar.hashpass.data.model.UserDetailsModel
import com.rohitjakhar.hashpass.data.remote.LoginRepo
import com.rohitjakhar.hashpass.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileVM @Inject constructor(
    private val loginRepo: LoginRepo
) : ViewModel() {

    var uploadImageUrl: MutableStateFlow<Resource<String>> = MutableStateFlow(Resource.Loading())
        private set
    var userDetailsState = MutableStateFlow<UserDetailsModel?>(null)
        private set
    var updateState = MutableStateFlow<Resource<Unit>>(Resource.Loading())
        private set

    fun getUserDetails() {
        viewModelScope.launch(IO) {
            userDetailsState.emit(loginRepo.getUserDetails())
        }
    }

    fun updateProfile(userDetailsModel: UserDetailsModel) {
        viewModelScope.launch(IO) {
            updateState.emit(Resource.Loading())
            updateState.emit(loginRepo.updateUser(userDetailsModel))
        }
    }

    fun uploadImage(photoUri: Uri) {
        TODO("Not yet implemented")
    }
}
