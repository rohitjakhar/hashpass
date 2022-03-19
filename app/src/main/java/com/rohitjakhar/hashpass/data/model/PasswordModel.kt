package com.rohitjakhar.hashpass.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PasswordModel(
    val email: String,
    val passwordHash: String,
    val createdAt: Long,
    val categoryModel: CategoryModel
) : Parcelable