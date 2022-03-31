package com.rohitjakhar.hashpass.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.net.Uri
import android.util.Base64
import android.util.Base64.decode
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.apollographql.apollo.api.Input
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.rohitjakhar.hashpass.MainActivity
import com.rohitjakhar.hashpass.data.local.PreferenceDataImpl
import com.rohitjakhar.hashpass.data.model.UserDetailsModel
import com.rohitjakhar.hashpass.databinding.DialogLoadingViewBinding
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

fun Fragment.toast(message: String) {
    Toast.makeText(this.requireContext(), message, android.widget.Toast.LENGTH_SHORT)
        .show()
}

fun Fragment.copyText(text: String) {
    val clipboardManager =
        this.requireContext().getSystemService(ClipboardManager::class.java) as ClipboardManager
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

    val pswdIterations = 65536
    val keySize = 256
    val saltBytes = byteArrayOf(0, 1, 2, 3, 4, 5, 6)

    val factory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")

    val spec: PBEKeySpec = PBEKeySpec(
        password.toCharArray(),
        saltBytes,
        pswdIterations,
        keySize
    )

    val secretKey = factory.generateSecret(spec)
    val secretKeySpec = SecretKeySpec(secretKey.encoded, "AES")
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
    val pswdIterations = 65536
    val keySize = 256
    val saltBytes = byteArrayOf(0, 1, 2, 3, 4, 5, 6)

    val factory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")

    val spec: PBEKeySpec = PBEKeySpec(
        password.toCharArray(),
        saltBytes,
        pswdIterations,
        keySize
    )

    val secretKey = factory.generateSecret(spec)
    val secretKeySpec = SecretKeySpec(secretKey.encoded, "AES")
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

fun Context.messageDialog(message: String, okClick: (DialogInterface) -> Unit): AlertDialog {
    return MaterialAlertDialogBuilder(this)
        .setTitle(message)
        .setPositiveButton("Ok") { dialogInterface, _ ->
            okClick.invoke(dialogInterface)
        }
        .setCancelable(false)
        .create()
}

fun Context.optionDialog(
    message: String,
    yesClick: (DialogInterface) -> Unit,
    noClick: (DialogInterface) -> Unit
): AlertDialog {
    return MaterialAlertDialogBuilder(this)
        .setTitle(message)
        .setPositiveButton("Yes") { dialogInterface, _ ->
            yesClick.invoke(dialogInterface)
        }
        .setNegativeButton("No") { dialogInterface, _ ->
            noClick.invoke(dialogInterface)
        }
        .setCancelable(true)
        .create()
}

fun TextInputLayout.setText(text: String?) {
    this.editText?.setText(text)
}

fun Activity.openInBrowser(link: String) {
    startActivity(
        Intent(Intent.ACTION_VIEW).also {
            it.data = Uri.parse(link)
        }
    )
}

fun View.backgroundGradientDrawable(@ColorInt startColor: Int, @ColorInt endColor: Int) {
    val h = this.height.toFloat()
    val shapeDrawable = ShapeDrawable(RectShape())
    shapeDrawable.paint.shader =
        LinearGradient(0f, 0f, 0f, h, startColor, endColor, Shader.TileMode.REPEAT)
    this.background = shapeDrawable
}

fun Activity.bioMetricsPrompts() {
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Unlock")
        .setSubtitle("Use Finger")
        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
        .build()

    val biometricPrompt = BiometricPrompt(
        this as FragmentActivity,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(this@bioMetricsPrompts, "$errString", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                finish()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                startActivity(Intent(this@bioMetricsPrompts, MainActivity::class.java))
                finish()
            }
        }
    )
    biometricPrompt.authenticate(promptInfo)
}

fun String.toLink(): String {
    var url: String = this
    if (!url.startsWith("www.") && !url.startsWith("http://")) {
        url = "www.$url"
    }
    if (!url.startsWith("http://")) {
        url = "http://$url"
    }
    return url
}

fun Long.toDate(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val netDate = Date(this)
    return sdf.format(netDate)
}
