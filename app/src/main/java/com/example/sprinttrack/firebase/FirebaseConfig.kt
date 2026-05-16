package com.example.sprinttrack.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseConfig {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val firestore: FirebaseFirestore =
        FirebaseFirestore.getInstance()
}