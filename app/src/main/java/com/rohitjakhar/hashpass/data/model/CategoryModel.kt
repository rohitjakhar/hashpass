package com.rohitjakhar.hashpass.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryModel(
    val id: String,
    val name: String,
    val icon: String,
    val passwordCount: Int
) : Parcelable
