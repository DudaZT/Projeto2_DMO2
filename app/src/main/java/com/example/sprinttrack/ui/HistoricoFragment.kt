package com.example.sprinttrack.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sprinttrack.adapter.TreinoAdapter
import com.example.sprinttrack.databinding.FragmentHistoricoBinding
import com.example.sprinttrack.firebase.FirebaseConfig
import com.example.sprinttrack.model.Treino
import com.google.firebase.firestore.Query
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
            .orderBy(
                "timestamp",
                Query.Direction.DESCENDING
            )
            .get()
            .addOnSuccessListener { result ->

                listaTreinos.clear()

                for(document in result) {

                    val treino =
                        document.toObject(Treino::class.java)

                    treino.id = document.id

                    listaTreinos.add(treino)
                }

                binding.recyclerTreinos.adapter =
                    TreinoAdapter(
                        listaTreinos,
                        onDelete = { treino ->

                            excluirTreino(treino)
                        },

                        onClick = { treino ->

                            abrirDetalhes(treino)
                        }
                    )
            }

            .addOnFailureListener {

                Toast.makeText(
                    requireContext(),
                    it.message,
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun abrirDetalhes(treino: Treino) {

        val intent =
            Intent(
                requireContext(),
                DetalheTreinoActivity::class.java
            )

        intent.putExtra("tempo", treino.tempo)

        intent.putExtra("passos", treino.passos)

        intent.putExtra("data", treino.data)

        intent.putExtra(
            "observacao",
            treino.observacao
        )

        startActivity(intent)
    }
    private fun excluirTreino(treino: Treino) {

        FirebaseConfig.firestore
            .collection("treinos")
            .document(treino.id)
            .delete()
            .addOnSuccessListener {

                Toast.makeText(
                    requireContext(),
                    "Sprint excluída!",
                    Toast.LENGTH_SHORT
                ).show()

                carregarTreinos()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}