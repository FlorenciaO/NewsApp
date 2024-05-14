package com.example.newsapp.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.newsapp.R.id
import com.example.newsapp.databinding.ActivityMainBinding
import com.example.newsapp.ui.callback.ToolbarListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ToolbarListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpBottomNavigation()
        hideToolbar()
    }

    private fun setUpBottomNavigation() {
        val navView: BottomNavigationView = binding.navView
        navController = findNavController(id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                id.navigation_news, id.navigation_users
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                id.navigation_news, id.navigation_users -> {
                    navView.visibility = View.VISIBLE
                    supportActionBar?.hide()
                }

                else -> {}
            }
        }
    }

    override fun showToolbar() {
        binding.navView.visibility = View.GONE
        supportActionBar?.show()
    }

    private fun hideToolbar() {
        supportActionBar?.hide()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}
