package com.example.sprinttrack.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sprinttrack.databinding.ActivityLeaderboardBinding
import com.example.sprinttrack.firebase.FirebaseConfig
import com.example.sprinttrack.model.LeaderboardItem

class LeaderboardActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLeaderboardBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.rvLeaderboard.layoutManager = LinearLayoutManager(this)
        binding.rvLeaderboard.setHasFixedSize(true)

        binding.btnVoltar.setOnClickListener {
            finish()
        }

        carregarTop10()
    }

    private fun carregarTop10() {
        FirebaseConfig.firestore
            .collection("treinos")
            .get()
            .addOnSuccessListener { result ->
                if (result == null || result.isEmpty) return@addOnSuccessListener

                val documentos = result.documents

                val treinosOrdenados = documentos.mapNotNull { doc ->
                    val tempo = doc.getDouble("tempo") ?: 0.0
                    val passos = (doc.getLong("passos") ?: 0L).toInt()
                    val data = doc.getString("data") ?: ""

                    // Captura os novos campos do usuário atrelados ao treino
                    val nome = doc.getString("nome") ?: "Corredor Anônimo"
                    val foto = doc.getString("foto") ?: ""

                    if (tempo <= 0.1 || passos == 0) return@mapNotNull null

                    val eficiencia = passos / tempo

                    LeaderboardItem(
                        nome = nome,
                        fotoBase64 = foto,
                        eficiencia = eficiencia,
                        tempo = tempo,
                        passos = passos,
                        data = data
                    )
                }.sortedByDescending { it.eficiencia }

                val listaTop10 = if (treinosOrdenados.size > 10) {
                    treinosOrdenados.subList(0, 10)
                } else {
                    treinosOrdenados
                }

                binding.rvLeaderboard.adapter = LeaderboardAdapter(listaTop10)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar ranking", Toast.LENGTH_SHORT).show()
            }
    }
}