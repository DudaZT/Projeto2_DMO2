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

        binding.btnNovoTreino.setOnClickListener {

            startActivity(
                Intent(requireContext(), SprintActivity::class.java)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}