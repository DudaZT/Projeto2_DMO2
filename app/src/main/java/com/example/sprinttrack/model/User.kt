package com.example.sprinttrack.model

/**
    Usamos uma data class para representar o usuário.
    Os campos batem exatamente com os documentos salvos no Firestore
*/

data class User(
    val uid: String = "", // Identificador único
    val nome: String = "", // Nome de exibição do usuário
    val email: String = "", // Email cadastrado
    val fotoBase64: String = "" // Foto de perfil armazenada em Base64
)