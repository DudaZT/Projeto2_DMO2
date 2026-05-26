package com.example.sprinttrack.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sprinttrack.databinding.FragmentHomeBinding
import com.example.sprinttrack.firebase.FirebaseConfig
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(
            inflater,
            container,
            false
        )

        carregarMelhorTempo()
        carregarLeaderboard()

        binding.btnNovoTreino.setOnClickListener {

            startActivity(
                Intent(requireContext(), SprintActivity::class.java)
            )
        }

        // Abre a LeaderboardActivity em uma nova tela completa ao clicar no card
        binding.containerLeaderboard.setOnClickListener {
            startActivity(
                Intent(requireContext(), LeaderboardActivity::class.java)
            )
        }

        return binding.root
    }

    private fun carregarMelhorTempo() {

        val uid =
            FirebaseConfig.auth.currentUser?.uid ?: return

        FirebaseConfig.firestore
            .collection("treinos")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { result ->

                if (result.isEmpty) {
                    binding.txtBestTime.text = "--"
                    return@addOnSuccessListener
                }

                val tempos = mutableListOf<Double>()

                for (document in result) {

                    val tempo =
                        document.getDouble("tempo") ?: 0.0

                    if (tempo > 1) {
                        tempos.add(tempo)
                    }
                }

                if (tempos.isEmpty()) {
                    binding.txtBestTime.text = "--"
                } else {

                    val melhorTempo = tempos.min()

                    binding.txtBestTime.text =
                        String.format(
                            Locale.getDefault(),
                            "%.2fs",
                            melhorTempo
                        )
                }
            }
    }

    private fun carregarLeaderboard() {

        FirebaseConfig.firestore
            .collection("treinos")
            .get()
            .addOnSuccessListener { result ->

                binding.txtLugar1.text = "1º Lugar: --"
                binding.txtLugar2.text = "2º Lugar: --"
                binding.txtLugar3.text = "3º Lugar: --"

                if (result.isEmpty) return@addOnSuccessListener

                val documentos = result.documents

                // Calcula a eficiência e ordena de forma decrescente para o Top 3
                val treinosOrdenados = documentos.mapNotNull { doc ->
                    val tempo = doc.getDouble("tempo") ?: 0.0
                    val passos = (doc.getLong("passos") ?: 0L).toInt()
                    val data = doc.getString("data") ?: ""

                    if (tempo <= 0.1 || passos == 0) return@mapNotNull null

                    val eficiencia = passos / tempo
                    Triple(eficiencia, tempo, data)
                }.sortedByDescending { it.first }

                val limiteTop3 = if (treinosOrdenados.size > 3) 3 else treinosOrdenados.size

                for (i in 0 until limiteTop3) {
                    val item = treinosOrdenados[i]
                    val dataFormatada = item.third.split(" ")

                    val textoFormatado = String.format(
                        Locale.getDefault(),
                        "%dº Lugar: %.2f pas/s (%s)",
                        i + 1,
                        item.first,
                        dataFormatada
                    )

                    when (i) {
                        0 -> binding.txtLugar1.text = textoFormatado
                        1 -> binding.txtLugar2.text = textoFormatado
                        2 -> binding.txtLugar3.text = textoFormatado
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}