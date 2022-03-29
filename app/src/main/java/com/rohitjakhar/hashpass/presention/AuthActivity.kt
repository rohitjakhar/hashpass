package com.rohitjakhar.hashpass.presention

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.rohitjakhar.hashpass.R
import com.rohitjakhar.hashpass.databinding.ActivityAuthBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar?.hide()
        supportActionBar?.hide()
        val fragmentManager: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.auth_container) as NavHostFragment
        val navController = fragmentManager.navController
    }
}
