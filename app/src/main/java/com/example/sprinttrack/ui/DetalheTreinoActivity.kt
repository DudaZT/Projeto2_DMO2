package com.example.sprinttrack.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sprinttrack.databinding.ActivityDetalheTreinoBinding

/**
 * A tela de detalhes é read-only.
 * Ela recebe os dados via Intent e exibe de forma organizada
 */
class DetalheTreinoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalheTreinoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityDetalheTreinoBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Recebe os dados enviados pela Intent
        val tempo = intent.getDoubleExtra("tempo", 0.0)

        val passos = intent.getIntExtra("passos", 0)

        val data = intent.getStringExtra("data") ?: ""

        val observacao = intent.getStringExtra("observacao") ?: ""

        // Preenche a interface com os dados
        binding.txtTempo.text = String.format("%.2fs", tempo)

        binding.txtPassos.text = "$passos passos"

        binding.txtData.text = data

        binding.txtObservacao.text =
            if (observacao.isEmpty()) {
                "Nenhuma observação"
            } else {
                observacao
            }

        binding.btnVoltar.setOnClickListener {

            finish()
        }
    }
}