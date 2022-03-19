package com.rohitjakhar.hashpass.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.rohitjakhar.hashpass.data.local.PreferenceDataImpl
import com.rohitjakhar.hashpass.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoginRepo @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val dataStorePref: PreferenceDataImpl
) {
    suspend fun loginUser(email: String, password: String) =
        flow<Resource<Unit>> {
            emit(Resource.Loading())
            val response = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            response.user?.let { firebaseUser ->
                // TODO: Retrive User Data from Hasura
                dataStorePref.changeLogin(true)
                emit(Resource.Sucess(Unit))
            } ?: emit(Resource.Error(message = "Failed"))
        }.catch {
            emit(Resource.Error(message = it.localizedMessage))
        }

    suspend fun registerUser(
        email: String,
        username: String,
        password: String
    ) = flow<Resource<Unit>> {
        emit(Resource.Loading())
        val response = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        response.user?.let {
            // TODO: Add User to Hasura
            dataStorePref.changeLogin(true)
            it.uid
            emit(Resource.Loading())
        } ?: emit(Resource.Error(message = "Error"))
    }.catch {
        firebaseAuth.signOut()
        emit(Resource.Error(message = it.message.toString()))
    }

    suspend fun logoutUser() = flow<Resource<Unit>> {
        emit(Resource.Loading())
        try {
            firebaseAuth.signOut()
            dataStorePref.changeLogin(false)
            emit(Resource.Sucess(Unit))
        } catch (
            e: Exception
        ) {
            emit(Resource.Error(message = e.localizedMessage))
        }
    }

    suspend fun forgetPassword(email: String) = flow<Resource<Unit>> {
        emit(Resource.Loading())
        try {
            firebaseAuth.sendPasswordResetEmail(email).await()
        } catch (e: Exception) {
            emit(Resource.Error(message = e.localizedMessage))
        }
    }

    suspend fun loginWithGoogle(idToken: String): Resource<Unit> {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            return Resource.Loading()
        } catch (e: Exception) {
            return Resource.Error(message = "")
        }
    }

    suspend fun checkLogin() = callbackFlow<Boolean> {
        firebaseAuth.addAuthStateListener {
            it.currentUser?.let {
                trySend(true)
            } ?: trySend(false)
            if (it.currentUser == null) {
                trySend(false)
            } else trySend(true)
        }
        awaitClose {
            trySend(false)
        }
    }
}
