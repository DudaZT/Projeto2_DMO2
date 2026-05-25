package com.example.sprinttrack.model

data class Treino(

    var id: String = "",
    val uid: String = "",
    val tempo: Double = 0.0,
    val passos: Int = 0,
    val data: String = "",
    val observacao: String = "",
    val fotoUrl: String = "",
    var timestamp: Long = 0
)