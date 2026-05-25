package com.example.sprinttrack.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sprinttrack.databinding.ItemTreinoBinding
import com.example.sprinttrack.model.Treino

class TreinoAdapter(
    private val lista: List<Treino>,
    private val onDelete: (Treino) -> Unit,
    private val onClick: (Treino) -> Unit
) : RecyclerView.Adapter<TreinoAdapter.MyViewHolder>() {

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

        holder.binding.txtTempo.text =
            "${treino.tempo}s"

        holder.binding.txtPassos.text =
            "${treino.passos} passos"

        holder.binding.txtData.text =
            treino.data

        holder.itemView.setOnClickListener {

            onClick(treino)
        }

        holder.binding.btnExcluir.setOnClickListener {

            onDelete(treino)
        }
    }
}