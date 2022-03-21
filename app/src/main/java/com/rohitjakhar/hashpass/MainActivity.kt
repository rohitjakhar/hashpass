package com.rohitjakhar.hashpass

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.rohitjakhar.hashpass.databinding.ActivityMainBinding
import com.rohitjakhar.hashpass.presention.AuthActivity
import com.rohitjakhar.hashpass.utils.hide
import com.rohitjakhar.hashpass.utils.show
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
        handleBottomBar()
        binding.bottomNavBar.setupWithNavController(navController)
    }

    private fun handleBottomBar() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_home -> {
                    showBar()
                }
                R.id.nav_add_password -> {
                    showBar()
                }
                R.id.nav_generate_password -> {
                    showBar()
                }
                R.id.nav_setting -> {
                    showBar()
                }
                else -> {
                    hideBar()
                }
            }
        }
    }

    private fun hideBar() {
        binding.bottomNavBar.hide()
    }

    private fun showBar() {
        binding.bottomNavBar.show()
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
                        startActivity(Intent(this@MainActivity, AuthActivity::class.java))
                        // TODO: navigate to login screen
                    }
                }
            }
        }
    }
}
