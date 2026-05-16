package com.example.sprinttrack.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sprinttrack.MainActivity
import com.example.sprinttrack.databinding.ActivityRegisterBinding
import com.example.sprinttrack.firebase.FirebaseConfig
import com.example.sprinttrack.model.User

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.btnCadastrar.setOnClickListener {

            val nome = binding.edtNome.text.toString()
            val email = binding.edtEmail.text.toString()
            val senha = binding.edtSenha.text.toString()

            FirebaseConfig.auth
                .createUserWithEmailAndPassword(email, senha)
                .addOnSuccessListener { result ->

                    val uid = result.user?.uid ?: ""

                    val user = User(
                        uid = uid,
                        nome = nome,
                        email = email
                    )

                    FirebaseConfig.firestore
                        .collection("usuarios")
                        .document(uid)
                        .set(user)

                    Toast.makeText(
                        this,
                        "Conta criada!",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(
                        Intent(this, MainActivity::class.java)
                    )

                    finish()

                }.addOnFailureListener {

                    Toast.makeText(
                        this,
                        "Erro ao cadastrar",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}