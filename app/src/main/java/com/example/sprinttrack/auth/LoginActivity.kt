package com.example.sprinttrack.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.Toast
import com.example.sprinttrack.MainActivity
import com.example.sprinttrack.databinding.ActivityLoginBinding
import com.example.sprinttrack.firebase.FirebaseConfig
import com.example.sprinttrack.auth.RegisterActivity
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (FirebaseConfig.auth.currentUser != null) {

            startActivity(
                Intent(this, MainActivity::class.java)
            )

            finish()
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {

            val email = binding.edtEmail.text.toString()
            val senha = binding.edtSenha.text.toString()

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

        binding.txtCriarConta.setOnClickListener {

            startActivity(
                Intent(this, RegisterActivity::class.java)
            )
        }
    }
}