package com.example.sprinttrack.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sprinttrack.ui.MainActivity
import com.example.sprinttrack.databinding.ActivityRegisterBinding
import com.example.sprinttrack.firebase.FirebaseConfig
import com.example.sprinttrack.model.User

/**
 * No cadastro, primeiro criamos a conta de autenticação
 * e depois salvamos os dados do perfil no Firestore,
 * usando o UID como chave primária do documento.
 */
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

            // Cria a conta no Firebase Authentication
            FirebaseConfig.auth
                .createUserWithEmailAndPassword(email, senha)
                .addOnSuccessListener { result ->
                    // Pega o UID gerado automaticamente
                    val uid = result.user?.uid ?: ""

                    // Cria o objeto usuário com os dados do formulário
                    val user = User(
                        uid = uid,
                        nome = nome,
                        email = email
                    )

                    // Salva dados complementares no Firestore
                    // O documento usa o UID como ID para referência direta
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