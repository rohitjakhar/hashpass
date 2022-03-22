package com.rohitjakhar.hashpass.presention.edit_profile

import androidx.lifecycle.ViewModel
import com.rohitjakhar.hashpass.data.remote.LoginRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditProfileVM @Inject constructor(
    private val loginRepo: LoginRepo
) : ViewModel() {

}
