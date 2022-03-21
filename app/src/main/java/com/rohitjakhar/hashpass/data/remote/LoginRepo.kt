package com.rohitjakhar.hashpass.data.remote

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.rohitjakhar.hashpass.DeleteUserMutation
import com.rohitjakhar.hashpass.GetUserDetailsQuery
import com.rohitjakhar.hashpass.InsertUserDetailsMutation
import com.rohitjakhar.hashpass.UpdateUserMutation
import com.rohitjakhar.hashpass.data.local.PreferenceDataImpl
import com.rohitjakhar.hashpass.data.model.UserDetailsModel
import com.rohitjakhar.hashpass.utils.ErrorType
import com.rohitjakhar.hashpass.utils.Resource
import com.rohitjakhar.hashpass.utils.toInputString
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoginRepo @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val dataStorePref: PreferenceDataImpl,
    private val apolloClient: ApolloClient
) {
    suspend fun loginUser(email: String, password: String) =
        flow<Resource<Unit>> {
            emit(Resource.Loading())
            val response = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            response.user?.let {
                when (val hasura = getUserDataFromHasura(email)) {
                    is Resource.Error -> {
                        emit(Resource.Error(message = "Failed"))
                    }
                    is Resource.Loading -> {}
                    is Resource.Sucess -> {
                        dataStorePref.changeLogin(true)
                        emit(Resource.Sucess(data = Unit))
                    }
                }
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
            if (addUserDataToHasura(
                    email = email,
                    userName = username,
                    useId = it.uid,
                    userImage = it.photoUrl.toString()
                )
            ) {
                dataStorePref.changeLogin(true)
                it.uid
                emit(Resource.Sucess(Unit))
            } else {
                emit(Resource.Error(message = "Unknown Error"))
            }
        } ?: emit(Resource.Error(message = "Error"))
    }.catch {
        firebaseAuth.signOut()
        emit(Resource.Error(message = it.message.toString()))
    }

    suspend fun logoutUser() = flow<Resource<Boolean>> {
        emit(Resource.Loading())
        try {
            firebaseAuth.signOut()
            dataStorePref.changeLogin(false)
            emit(Resource.Sucess(true))
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

    suspend fun loginWithGoogle(idToken: String) = flow<Resource<Unit>> {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            if (result.user != null) {
                if (!result.additionalUserInfo!!.isNewUser) {
                    // When user is old
                    result.user!!.email?.let {
                        when (val userData = getUserDataFromHasura(it)) {
                            is Resource.Error -> {
                                emit(Resource.Error(message = userData.message))
                            }
                            is Resource.Loading -> {}
                            is Resource.Sucess -> {
                                dataStorePref.changeLogin(true)
                                emit(Resource.Sucess(data = Unit))
                            }
                        }
                    } ?: emit(Resource.Error(errorType = ErrorType.EMPTY_DATA))
                } else {
                    // TODO: Add User Details at Hasura
                    val user = result.user!!
                    if (addUserDataToHasura(
                            email = user.email!!,
                            useId = user.uid,
                            userName = user.displayName ?: "No Name",
                            userImage = user.photoUrl.toString()
                        )
                    ) {
                        emit(Resource.Sucess(Unit))
                    } else {
                        emit(Resource.Error(message = "Unknown Issue"))
                    }
                }
            } else emit(Resource.Error(errorType = ErrorType.EMPTY_DATA))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.localizedMessage ?: "Unknown Error"))
        }
    }

    private suspend fun getUserDataFromHasura(userEmail: String): Resource<Unit> {
        try {
            val task = apolloClient.query(GetUserDetailsQuery(userEmail)).await()
            task.data?.let {
                val user = it.users().firstOrNull()
                return if (user != null) {
                    Resource.Sucess(data = Unit)
                } else {
                    Resource.Error(errorType = ErrorType.EMPTY_DATA, message = "User Not Found!")
                }
            } ?: return Resource.Error(
                errorType = ErrorType.EMPTY_DATA,
                message = "User Not Found!"
            )
        } catch (e: Exception) {
            return Resource.Error(errorType = ErrorType.EMPTY_DATA, message = "User Not Found!")
        }
    }

    private suspend fun addUserDataToHasura(
        email: String,
        useId: String,
        userImage: String,
        userName: String
    ): Boolean {
        return try {
            val task =
                apolloClient.mutate(InsertUserDetailsMutation(email, useId, userImage, userName))
                    .await()
            !task.hasErrors()
        } catch (e: Exception) {
            false
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

    suspend fun updateUser(userDetailsModel: UserDetailsModel): Resource<Unit> {
        try {
            firebaseAuth.currentUser!!.updateEmail(userDetailsModel.email).await()
            val task = apolloClient.mutate(
                UpdateUserMutation(
                    userDetailsModel.id.toInputString(),
                    userDetailsModel.email.toInputString(),
                    userDetailsModel.userImage.toInputString(),
                    userDetailsModel.name.toInputString()
                )
            ).await()
            if (task.hasErrors()) {
                return Resource.Error(message = "")
            } else {
                return Resource.Sucess(Unit)
            }
        } catch (e: Exception) {
            return Resource.Error(message = "")
        }
    }

    suspend fun deleteUserAccount(userId: String) {
        try {
            firebaseAuth.currentUser!!.delete().await()
            val task = apolloClient.mutate(DeleteUserMutation(userId.toInputString())).await()
        } catch (e: Exception) {
        }
    }
}
