package com.rohitjakhar.hashpass

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.hasura.sdk.Hasura
import io.hasura.sdk.ProjectConfig

@HiltAndroidApp
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val hasuraConfig = ProjectConfig.Builder()
            .setProjectName("hashpass")
            .build()
        Hasura.setProjectConfig(hasuraConfig)
            .enableLogs()
            .initialise(this)
        val user = Hasura.getClient().user
    }
}
