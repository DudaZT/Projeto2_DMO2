package com.example.sprinttrack.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sprinttrack.adapter.TreinoAdapter
import com.example.sprinttrack.databinding.FragmentHistoricoBinding
import com.example.sprinttrack.firebase.FirebaseConfig
import com.example.sprinttrack.model.Treino

class HistoricoFragment : Fragment() {

    private var _binding: FragmentHistoricoBinding? = null
    private val binding get() = _binding!!

    private val listaTreinos = mutableListOf<Treino>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHistoricoBinding.inflate(
            inflater,
            container,
            false
        )

        binding.recyclerTreinos.layoutManager =
            LinearLayoutManager(requireContext())

        carregarTreinos()

        return binding.root
    }

    private fun carregarTreinos() {

        val uid =
            FirebaseConfig.auth.currentUser?.uid ?: return

        FirebaseConfig.firestore
            .collection("treinos")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { result ->

                listaTreinos.clear()

                for(document in result) {

                    val treino =
                        document.toObject(Treino::class.java)

                    listaTreinos.add(treino)
                }

                binding.recyclerTreinos.adapter =
                    TreinoAdapter(listaTreinos)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}