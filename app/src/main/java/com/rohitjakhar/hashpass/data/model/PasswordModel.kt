package com.rohitjakhar.hashpass.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class PasswordModel(
    val email: String? = null,
    val passwordHash: String,
    val createdAt: Long,
    val url: String,
    val uuid: UUID,
    val userName: String? = null,
    val title: String,
    val description: String? = null,
    val securityQuestion: String? = null,
    val securityAnswer: String? = null,
    val remarks: String? = null
) : Parcelable
