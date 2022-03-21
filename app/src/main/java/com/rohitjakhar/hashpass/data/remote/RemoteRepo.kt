package com.rohitjakhar.hashpass.data.remote

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.rohitjakhar.hashpass.DeletePasswordMutation
import com.rohitjakhar.hashpass.GetPasswordQuery
import com.rohitjakhar.hashpass.InsertPasswordMutation
import com.rohitjakhar.hashpass.UpdatePasswordMutation
import com.rohitjakhar.hashpass.data.model.PasswordModel
import com.rohitjakhar.hashpass.utils.ErrorType
import com.rohitjakhar.hashpass.utils.Resource
import com.rohitjakhar.hashpass.utils.decrypt
import com.rohitjakhar.hashpass.utils.encrypt
import com.rohitjakhar.hashpass.utils.toInputAny
import com.rohitjakhar.hashpass.utils.toInputString
import javax.inject.Inject

class RemoteRepo @Inject constructor(
    private val apolloClient: ApolloClient
) {
    suspend fun addPassword(passwordModel: PasswordModel): Resource<Unit> {
        return insertPassword(passwordModel)
    }

    suspend fun getPasswords(): Resource<List<PasswordModel>> {
        try {
            val task = apolloClient.query(GetPasswordQuery()).await()
            if (!task.hasErrors()) {
                task.data?.let { data ->
                    val passwordList = mutableListOf<PasswordModel>()
                    data.password().forEach {
                        passwordList.add(
                            PasswordModel(
                                email = it.email(),
                                passwordHash = it.password().decrypt(it.uuid().toString()),
                                description = it.descriptions(),
                                userName = it.username(),
                                url = it.url() ?: "",
                                securityAnswer = it.security_answer(),
                                securityQuestion = it.security_question(),
                                title = it.Title(),
                                uuid = it.uuid().toString(),
                                createdAt = it.createdAt().toString().toLongOrNull() ?: 0L,
                                userId = it.username().toString()
                            )
                        )
                    }
                    return Resource.Sucess(data = passwordList)
                } ?: return Resource.Error(errorType = ErrorType.EMPTY_DATA)
            } else {
                return Resource.Error(message = task.errors?.first()?.message ?: "Unknown Error")
            }
        } catch (e: Exception) {
            return Resource.Error(message = e.localizedMessage ?: "Unknown Error")
        }
    }

    private suspend fun insertPassword(passwordModel: PasswordModel): Resource<Unit> {
        try {
            val task = apolloClient.mutate(
                InsertPasswordMutation(
                    passwordModel.title.toInputString(),
                    passwordModel.createdAt.toInputAny(),
                    passwordModel.description.toInputString(),
                    passwordModel.email.toInputString(),
                    passwordModel.passwordHash.encrypt(passwordModel.uuid).toInputString(),
                    passwordModel.remarks.toInputString(),
                    passwordModel.securityQuestion.toInputString(),
                    passwordModel.securityAnswer.toInputString(),
                    passwordModel.url.toInputString(),
                    passwordModel.userId.toInputString(),
                    passwordModel.userName.toInputString(),
                    passwordModel.uuid.toInputAny()
                )
            ).await()
            return Resource.Loading()
        } catch (e: Exception) {
            return Resource.Error(message = e.localizedMessage ?: "Unknown Error")
        }
    }

    suspend fun deletePassword(uuid: String): Resource<Unit> {
        return try {
            val task = apolloClient.mutate(DeletePasswordMutation(uuid.toInputAny())).await()
            if (!task.hasErrors()) {
                Resource.Sucess(Unit)
            } else {
                Resource.Error(message = task.errors?.first()?.message ?: "Unknown Error")
            }
        } catch (e: Exception) {
            Resource.Error(message = e.localizedMessage ?: "Unknown Error")
        }
    }

    suspend fun updatePassword(passwordModel: PasswordModel): Resource<Unit> {
        try {
            val task = apolloClient.mutate(
                UpdatePasswordMutation(
                    passwordModel.title.toInputString(),
                    passwordModel.createdAt.toInputAny(),
                    passwordModel.description.toInputString(),
                    passwordModel.email.toInputString(),
                    passwordModel.passwordHash.encrypt(passwordModel.uuid).toInputString(),
                    passwordModel.remarks.toInputString(),
                    passwordModel.securityQuestion.toInputString(),
                    passwordModel.securityAnswer.toInputString(),
                    passwordModel.url.toInputString(),
                    passwordModel.userName.toInputString(),
                    passwordModel.uuid.toInputAny()
                )
            ).await()
            if (!task.hasErrors()) {
                return Resource.Sucess(Unit)
            } else {
                return Resource.Error(message = task.errors?.first()?.message ?: "Unknown Error")
            }
        } catch (e: Exception) {
            return Resource.Error(message = e.localizedMessage ?: "Unknown Error")
        }
    }
}
