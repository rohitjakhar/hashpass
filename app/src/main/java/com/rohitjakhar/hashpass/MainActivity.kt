package com.rohitjakhar.hashpass

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.play.core.review.ReviewManagerFactory
import com.rohitjakhar.hashpass.databinding.ActivityMainBinding
import com.rohitjakhar.hashpass.utils.hide
import com.rohitjakhar.hashpass.utils.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel by viewModels<MainViewModel>()
    private val reviewManager by lazy { ReviewManagerFactory.create(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        askReview()
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
        title = navController.currentDestination?.label
        handleBottomBar()
        binding.bottomNavBar.setupWithNavController(navController)
    }

    private fun askReview() {
        val reviewRequest = reviewManager.requestReviewFlow()
        reviewRequest.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val flow = reviewManager.launchReviewFlow(this, reviewInfo)
                flow.addOnCompleteListener {
                }
            }
        }
    }

    private fun handleBottomBar() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            title = destination.label
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
    }
}
