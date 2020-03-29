package com.example.pepirelax.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import com.example.pepirelax.R
import com.example.pepirelax.ui.fragment.SingInFragment
import com.example.pepirelax.ui.fragment.SingUpFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadFragment(SingInFragment())



        menuSing.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.btnSingUp -> {
                    loadFragment(SingUpFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.btnSingIn -> {
                    loadFragment(SingInFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    return@setOnNavigationItemSelectedListener false
                }
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentHeader,fragment)
            addToBackStack(null)
            commit()
        }
    }
}
