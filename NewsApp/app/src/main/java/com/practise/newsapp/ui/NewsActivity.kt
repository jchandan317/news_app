package com.practise.newsapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.practise.newsapp.R

class NewsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        val newsNavHostFragment =
            supportFragmentManager.findFragmentById(R.id.newsNavHostFragment)
        newsNavHostFragment?.findNavController()?.let {
            findViewById<BottomNavigationView>(R.id.bottomNavigationView).setupWithNavController(
                it
            )
        }
    }

}