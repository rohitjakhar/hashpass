package com.rohitjakhar.hashpass.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Build
import android.widget.Toast
import androidx.fragment.app.Fragment

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
