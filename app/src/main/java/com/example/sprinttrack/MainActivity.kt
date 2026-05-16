package com.example.sprinttrack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.sprinttrack.databinding.ActivityMainBinding
import com.example.sprinttrack.ui.HistoricoFragment
import com.example.sprinttrack.ui.HomeFragment
import com.example.sprinttrack.ui.PerfilFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        replaceFragment(HomeFragment())

        binding.bottomNav.setOnItemSelectedListener {

            when(it.itemId) {

                R.id.menu_home -> {
                    replaceFragment(HomeFragment())
                }

                R.id.menu_historico -> {
                    replaceFragment(HistoricoFragment())
                }

                R.id.menu_perfil -> {
                    replaceFragment(PerfilFragment())
                }
            }

            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameContainer, fragment)
            .commit()
    }
}