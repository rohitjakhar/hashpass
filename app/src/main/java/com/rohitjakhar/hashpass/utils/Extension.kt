package com.rohitjakhar.hashpass.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Build
import android.util.Base64
import android.util.Base64.decode
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.apollographql.apollo.api.Input
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.rohitjakhar.hashpass.data.local.PreferenceDataImpl
import com.rohitjakhar.hashpass.data.model.UserDetailsModel
import com.rohitjakhar.hashpass.databinding.DialogLoadingViewBinding
import kotlinx.coroutines.flow.first
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

fun Fragment.toast(message: String) {
    Toast.makeText(this.requireContext(), message, android.widget.Toast.LENGTH_SHORT)
        .show()
}

fun Fragment.copyText(text: String) {
    val clipboardManager =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.requireContext().getSystemService(ClipboardManager::class.java) as ClipboardManager
        } else {
            TODO("VERSION.SDK_INT < M")
        }
    val clipData = ClipData.newPlainText("text", text)
    clipboardManager.setPrimaryClip(clipData)
}

fun Activity.loadingView(loadingText: String? = null, cancelable: Boolean = true): AlertDialog {
    val binding = DialogLoadingViewBinding.inflate(this.layoutInflater)
    return MaterialAlertDialogBuilder(this)
        .setView(binding.root)
        .setCancelable(cancelable)
        .create()
}

fun View.show() {
    this.isVisible = true
}

fun View.hide() {
    this.isVisible = false
}

fun String.encrypt(password: String): String {
    val secretKeySpec = SecretKeySpec(password.toByteArray(), "AES")
    val iv = ByteArray(16)
    val charArray = password.toCharArray()
    for (i in charArray.indices) {
        iv[i] = charArray[i].toByte()
    }
    val ivParameterSpec = IvParameterSpec(iv)

    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)

    val encryptedValue = cipher.doFinal(this.toByteArray())
    return Base64.encodeToString(encryptedValue, Base64.DEFAULT)
}

fun String.decrypt(password: String): String {
    val secretKeySpec = SecretKeySpec(password.toByteArray(), "AES")
    val iv = ByteArray(16)
    val charArray = password.toCharArray()
    for (i in charArray.indices) {
        iv[i] = charArray[i].toByte()
    }
    val ivParameterSpec = IvParameterSpec(iv)

    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)

    val decryptedByteValue = cipher.doFinal(decode(this, Base64.DEFAULT))
    return String(decryptedByteValue)
}

fun String?.toInputString(): Input<String> {
    return Input.optional(this)
}

fun String?.toInputAny(): Input<Any> {
    return Input.optional(this)
}

fun Long.toInputAny(): Input<Any> {
    return Input.optional(this)
}

suspend fun PreferenceDataImpl.getUserId(): String {
    return this.userId.first().toString()
}

suspend fun PreferenceDataImpl.getUserDetails(): UserDetailsModel {
    this.apply {
        return UserDetailsModel(
            name = userName.first(),
            email = userEmail.first(),
            id = userId.first(),
            userImage = userImage.first()
        )
    }
}

fun TextInputLayout.getText(): String {
    return this.editText?.text.toString()
}
