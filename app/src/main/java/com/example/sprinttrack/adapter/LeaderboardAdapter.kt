package com.example.sprinttrack.ui

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sprinttrack.R
import com.example.sprinttrack.databinding.ItemLeaderboardBinding
import com.example.sprinttrack.model.LeaderboardItem
import java.util.Locale

class LeaderboardAdapter(
    private val lista: List<LeaderboardItem>
) : RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

    class LeaderboardViewHolder(val binding: ItemLeaderboardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val binding = ItemLeaderboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeaderboardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        val context = holder.itemView.context
        val item = lista[position]

        val dataFormatada = item.data.split(" ").first()

        val prefixoPosicao = when (position) {
            0 -> "🥇"
            1 -> "🥈"
            2 -> "🥉"
            else -> "${position + 1}º"
        }

        // Define os textos
        holder.binding.txtNomeUsuario.text = item.nome
        holder.binding.txtPosicaoEColocacao.text = String.format(Locale.getDefault(), "%s %.2f p/s", prefixoPosicao, item.eficiencia)
        holder.binding.txtDetalhesTreino.text = String.format(Locale.getDefault(), "%d passos em %.1fs", item.passos, item.tempo)
        holder.binding.txtDataRegistro.text = dataFormatada

        // Lógica para carregar a foto em Base64 (com fallback caso esteja vazia)
        if (item.fotoBase64.isNotEmpty()) {
            try {
                val imagemBytes = Base64.decode(item.fotoBase64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imagemBytes, 0, imagemBytes.size)
                holder.binding.imgPerfilLeaderboard.setImageBitmap(bitmap)
            } catch (e: Exception) {
                holder.binding.imgPerfilLeaderboard.setImageResource(R.drawable.ic_launcher_foreground)
            }
        } else {
            holder.binding.imgPerfilLeaderboard.setImageResource(R.drawable.ic_launcher_foreground)
        }

        // Cor de destaque do pódio
        if (position < 3) {
            holder.binding.txtPosicaoEColocacao.setTextColor(ContextCompat.getColor(context, R.color.green))
        } else {
            holder.binding.txtPosicaoEColocacao.setTextColor(ContextCompat.getColor(context, R.color.white))
        }
    }

    override fun getItemCount(): Int = lista.size
}