package com.doubean.ford

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.doubean.ford.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var navController: NavController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        navController = findNavController(this, R.id.nav_host_fragment_activity_main)
        setupWithNavController(binding.navView, navController!!)
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}