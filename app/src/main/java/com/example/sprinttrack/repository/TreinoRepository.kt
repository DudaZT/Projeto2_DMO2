package com.example.sprinttrack.repository

import com.example.sprinttrack.firebase.FirebaseConfig
import com.example.sprinttrack.model.Treino

class TreinoRepository {

    fun salvarTreino(treino: Treino) {

        FirebaseConfig.firestore
            .collection("treinos")
            .add(treino)
    }
}