package com.rohitjakhar.hashpass

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.rohitjakhar.hashpass.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        collectAuth()
        val fragmentManager: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_container) as NavHostFragment
        navController = fragmentManager.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_add_password,
                R.id.nav_generate_password,
                R.id.nav_setting
            )
        )
        binding.bottomNavBar.setupWithNavController(navController)
    }

    private fun collectAuth() {
        lifecycleScope.launchWhenStarted {
            viewModel.loginState.collectLatest {
                when (it) {
                    true -> {
                        navController.navigate(R.id.nav_home)
                        // TODO: navigate to home screen
                    }
                    false -> {
                        navController.navigate(R.id.loginFragment)
                        // TODO: navigate to login screen
                    }
                }
            }
        }
    }
}
