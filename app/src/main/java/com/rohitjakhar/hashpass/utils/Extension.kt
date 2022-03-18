package com.rohitjakhar.hashpass.utils

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.toast(message: String) {
    Toast.makeText(this.requireContext(), message, android.widget.Toast.LENGTH_SHORT)
        .show()
}
