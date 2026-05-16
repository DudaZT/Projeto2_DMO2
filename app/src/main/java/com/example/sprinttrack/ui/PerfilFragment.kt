package com.example.sprinttrack.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sprinttrack.auth.LoginActivity
import com.example.sprinttrack.databinding.FragmentPerfilBinding
import com.example.sprinttrack.firebase.FirebaseConfig

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPerfilBinding.inflate(
            inflater,
            container,
            false
        )

        carregarUsuario()

        binding.btnLogout.setOnClickListener {

            FirebaseConfig.auth.signOut()

            startActivity(
                Intent(requireContext(), LoginActivity::class.java)
            )

            requireActivity().finish()
        }

        return binding.root
    }

    private fun carregarUsuario() {

        val uid =
            FirebaseConfig.auth.currentUser?.uid ?: return

        FirebaseConfig.firestore
            .collection("usuarios")
            .document(uid)
            .get()
            .addOnSuccessListener {

                binding.txtNome.text =
                    it.getString("nome")

                binding.txtEmail.text =
                    it.getString("email")
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}