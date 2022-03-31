package com.rohitjakhar.hashpass.presention

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.rohitjakhar.hashpass.R
import com.rohitjakhar.hashpass.data.local.PreferenceDataImpl
import com.rohitjakhar.hashpass.utils.bioMetricsPrompts
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import javax.inject.Inject

const val UPDATE_REQUEST_CODE = 524

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    @Inject
    lateinit var dataStorePref: PreferenceDataImpl

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultLauncher ->
            if (resultLauncher.resultCode == RESULT_OK) {
            }
        }
    private val appUpdateManager: AppUpdateManager by lazy {
        AppUpdateManagerFactory.create(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkUpdate()
        setContentView(R.layout.activity_splash_screen)
        actionBar?.hide()
        supportActionBar?.hide()
    }

    private fun checkUpdate() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                appUpdateManager.startUpdateFlowForResult(
                    it,
                    this,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE)
                        .setAllowAssetPackDeletion(true)
                        .build(),
                    UPDATE_REQUEST_CODE
                )
                resultLauncher.launch(intent)
            } else {
                checkUserLogin()
            }
        }
            .addOnFailureListener {
                checkUserLogin()
            }
    }

    private fun checkUserLogin() {
        if (Firebase.auth.currentUser != null) {
            lifecycleScope.launchWhenStarted {
                if (dataStorePref.isFingerLockOn.first()) {
                    bioMetricsPrompts()
                } else {
                    startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                    finish()
                }
            }
        } else {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }
}
