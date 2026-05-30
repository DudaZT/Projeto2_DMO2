package com.example.sprinttrack.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sprinttrack.databinding.ItemTreinoBinding
import com.example.sprinttrack.model.Treino

/**
 * Adapter do RecyclerView do histórico de treinos.
 * Recebe dois callbacks: onDelete (excluir) e onClick (abrir detalhes).
 * O Adapter liga a lista de treinos ao RecyclerView.
 * Cada item mostra tempo, passos e data.
 * Os cliques são delegados para o Fragment via callbacks.
 */
class TreinoAdapter(
    private val lista: List<Treino>,
    private val onDelete: (Treino) -> Unit,
    private val onClick: (Treino) -> Unit
) : RecyclerView.Adapter<TreinoAdapter.MyViewHolder>() {

    // ViewHolder que guarda a referência ao binding do layout do item
    inner class MyViewHolder(
        val binding: ItemTreinoBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {

        val binding = ItemTreinoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return MyViewHolder(binding)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {

        val treino = lista[position]

        // Preenche os campos visuais com os dados do treino
        holder.binding.txtTempo.text = "${treino.tempo}s"
        holder.binding.txtPassos.text = "${treino.passos} passos"
        holder.binding.txtData.text = treino.data

        // Clique no item inteiro: abre tela de detalhes
        holder.itemView.setOnClickListener {

            onClick(treino)
        }

        // Clique no botão de excluir: deleta o treino
        holder.binding.btnExcluir.setOnClickListener {

            onDelete(treino)
        }
    }
}