package com.example.sprinttrack.model

/**
 * Modelo de dados do treino.
 * Representa um sprint registrado no Firestore na coleção 'treinos'.
 */
data class Treino(
    var id: String = "", // ID do documento
    val uid: String = "", // UID do usuário dono do treino
    val tempo: Double = 0.0, // Tempo da corrida em segundos
    val passos: Int = 0, // Quantidade total de passos
    val data: String = "", // Data e hora formatada (dd/MM/yyyy HH:mm)
    val observacao: String = "", // Observação digitada ou ditada por voz
    var timestamp: Long = 0 // Timestamp Unix para ordenação cronológica
)