package com.example.sprinttrack.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Objeto singleton responsável por centralizar as instâncias do Firebase.
 * Usamos 'object' para garantir que exista apenas uma instância
 * durante todo o ciclo de vida do aplicativo.

 * centralizamos as conexões com o Firebase em um Singleton
 * assim qualquer Activity ou Fragment acessa a autenticação
 * e o banco de dados pelo mesmo objeto, evitando múltiplas instâncias
 */
object FirebaseConfig {

    // Instância do Firebase Authentication, usada para login, cadastro e logout
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Instância do Firestore, nosso banco de dados em nuvem
    val firestore: FirebaseFirestore =
        FirebaseFirestore.getInstance()
}