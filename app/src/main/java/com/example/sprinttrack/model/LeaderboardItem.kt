package com.example.sprinttrack.model

/**
 * Modelo usado exclusivamente para montar o ranking (leaderboard).
 * Combina dados do treino com dados do perfil do usuário.
 */
data class LeaderboardItem(
    val nome: String,           // Nome do corredor (vem do perfil)
    val fotoBase64: String,     // Foto do corredor em Base64 (vem do perfil)
    val eficiencia: Double,     // Passos por segundo
    val tempo: Double,          // Tempo do treino
    val passos: Int,            // Quantidade de passos
    val data: String            // Data do treino
)