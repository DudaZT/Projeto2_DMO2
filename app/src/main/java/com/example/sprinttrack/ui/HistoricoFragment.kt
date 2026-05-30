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

/**
 * O histórico carrega apenas os treinos do usuário logado,
 * ordenados por timestamp.
 * Cada item permite abrir detalhes ou excluir.
 * A exclusão remove do Firestore e recarrega a lista
 */
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

        // Configura o RecyclerView com lista vertical
        binding.recyclerTreinos.layoutManager = LinearLayoutManager(requireContext())

        carregarTreinos()

        return binding.root
    }

    /**
     * Busca todos os treinos do usuário logado,
     * ordenados do mais recente para o mais antigo.
     */
    private fun carregarTreinos() {

        val uid =
            FirebaseConfig.auth.currentUser?.uid ?: return

        FirebaseConfig.firestore
            .collection("treinos")
            .whereEqualTo("uid", uid) // Filtra por usuário
            .orderBy(
                "timestamp",
                Query.Direction.DESCENDING // Mais recentes primeiro
            )
            .get()
            .addOnSuccessListener { result ->

                listaTreinos.clear()

                for(document in result) {

                    val treino =
                        document.toObject(Treino::class.java)

                    treino.id = document.id // Guarda o ID para operações de delete

                    listaTreinos.add(treino)
                }

                // Vincula o adapter com os callbacks
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

    /**
     * Abre a tela de detalhes passando os dados do treino via Intent.
     */
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

    /**
     * Remove um treino do Firestore e atualiza a lista.
     */
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

                carregarTreinos() // Recarrega a lista atualizada
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}