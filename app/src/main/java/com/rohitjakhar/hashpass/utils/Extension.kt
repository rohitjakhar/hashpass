package com.rohitjakhar.hashpass.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rohitjakhar.hashpass.R
import com.rohitjakhar.hashpass.databinding.DialogLoadingViewBinding

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
