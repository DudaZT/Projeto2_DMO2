package com.example.sprinttrack.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.sprinttrack.R
import com.example.sprinttrack.databinding.ActivityMainBinding

/**
 * A MainActivity é o container principal.
 * Ela gerencia a BottomNavigationView
 * e faz a troca entre os 3 Fragments: Home, Histórico e Perfil.
*/
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Ao abrir o app, carrega a tela Home
        replaceFragment(HomeFragment())

        // Configura a barra de navegação inferior com 3 abas
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

            true // Indica que o item foi selecionado
        }
    }

    /**
     * Substitui o Fragment exibido no container principal.
     * Usamos 'replace' para manter apenas um Fragment por vez.
     */
    private fun replaceFragment(fragment: Fragment) {

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameContainer, fragment)
            .commit()
    }
}