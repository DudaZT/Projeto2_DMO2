package com.example.sprinttrack.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.sprinttrack.auth.LoginActivity
import com.example.sprinttrack.databinding.FragmentPerfilBinding
import com.example.sprinttrack.firebase.FirebaseConfig
import com.example.sprinttrack.util.ImageUtils

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

        binding.imgPerfil.setOnClickListener {
            verificarPermissaoECamera()
        }

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

                // Tenta ler por 'fotoBase64', se estiver nulo tenta ler pela chave curta 'foto'
                val fotoBase64 = it.getString("fotoBase64") ?: it.getString("foto")
                if (!fotoBase64.isNullOrEmpty()) {
                    val bitmap = ImageUtils.base64ToBitmap(fotoBase64)
                    bitmap?.let { bmp ->
                        binding.imgPerfil.setImageBitmap(bmp)
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val tirarFotoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val bitmap: Bitmap? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                data?.extras?.getParcelable("data", Bitmap::class.java)
            } else {
                @Suppress("DEPRECATION")
                data?.extras?.get("data") as? Bitmap
            }

            bitmap?.let {
                val copia = it.copy(Bitmap.Config.ARGB_8888, true)
                salvarFotoNoFirestore(copia)
            }
        }
    }

    private val requisitarCameraLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            abrirCamera()
        } else {
            Toast.makeText(
                requireContext(),
                "Permissão de câmera negada",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun verificarPermissaoECamera() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            abrirCamera()
        } else {
            requisitarCameraLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun abrirCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        tirarFotoLauncher.launch(intent)
    }

    private fun salvarFotoNoFirestore(bitmap: Bitmap) {
        val uid = FirebaseConfig.auth.currentUser?.uid ?: return

        val stringBase64 = ImageUtils.bitmapToBase64(bitmap)

        // Alinhamento das chaves: Atualiza tanto a chave antiga quanto a nova curta usada pela Leaderboard
        val atualizacaoFoto = hashMapOf<String, Any>(
            "fotoBase64" to stringBase64,
            "foto" to stringBase64
        )

        FirebaseConfig.firestore
            .collection("usuarios")
            .document(uid)
            .update(atualizacaoFoto)
            .addOnSuccessListener {
                binding.imgPerfil.setImageBitmap(bitmap)
                Toast.makeText(
                    requireContext(),
                    "Foto de perfil updated!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Erro ao salvar a foto",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}