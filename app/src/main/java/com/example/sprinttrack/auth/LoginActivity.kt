package com.example.sprinttrack.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.Toast
import com.example.sprinttrack.ui.MainActivity
import com.example.sprinttrack.databinding.ActivityLoginBinding
import com.example.sprinttrack.firebase.FirebaseConfig
import com.example.sprinttrack.auth.RegisterActivity

/**
 * O LoginActivity verifica se já existe sessão ativa,
 * faz autenticação por email/senha e redireciona para cadastro.
 * Tudo usando listeners assíncronos do Firebase.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Se já existe um usuário logado, pula direto para a MainActivity
        // Isso implementa a persistência de sessão
        if (FirebaseConfig.auth.currentUser != null) {

            startActivity(
                Intent(this, MainActivity::class.java)
            )

            finish()
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura o botão de login
        binding.btnLogin.setOnClickListener {

            val email = binding.edtEmail.text.toString()
            val senha = binding.edtSenha.text.toString()

            // Autentica com Firebase Authentication usando email e senha
            FirebaseConfig.auth
                .signInWithEmailAndPassword(email, senha)
                .addOnSuccessListener {

                    Toast.makeText(
                        this,
                        "Login realizado!",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(
                        Intent(this, MainActivity::class.java)
                    )

                }.addOnFailureListener {

                    Toast.makeText(
                        this,
                        "Erro no login",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        // Link para navegar para a tela de cadastro
        binding.txtCriarConta.setOnClickListener {

            startActivity(
                Intent(this, RegisterActivity::class.java)
            )
        }
    }
}