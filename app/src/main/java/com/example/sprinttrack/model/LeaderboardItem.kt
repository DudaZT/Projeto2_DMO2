package com.example.sprinttrack.model

data class LeaderboardItem(
    val nome: String,
    val fotoBase64: String,
    val eficiencia: Double,
    val tempo: Double,
    val passos: Int,
    val data: String
)