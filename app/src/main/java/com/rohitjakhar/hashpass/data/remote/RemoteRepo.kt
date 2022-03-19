package com.rohitjakhar.hashpass.data.remote

import com.rohitjakhar.hashpass.utils.Resource
import javax.inject.Inject

class RemoteRepo @Inject constructor() {
    suspend fun addPassword(): Resource<Unit> {
        return Resource.Loading()
    }

    suspend fun loadCategory(): Resource<Unit> {
        return Resource.Loading()
    }

    suspend fun addCategory(): Resource<Unit> {
        return Resource.Loading()
    }

    suspend fun getPasswords(): Resource<Unit> {
        return Resource.Loading()
    }
}
