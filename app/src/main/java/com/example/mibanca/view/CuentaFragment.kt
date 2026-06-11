package com.example.mibanca.view

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mibanca.R
import com.example.mibanca.databinding.FragmentCuentaBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.bumptech.glide.Glide

class CuentaFragment : Fragment() {

    private var _binding: FragmentCuentaBinding? = null
    private val binding get() = _binding!!
    private val firebaseAuth = FirebaseAuth.getInstance()


    //Abrir la Galería de fotos del teléfono
    private val abrirGaleriaLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            cargarFotoEnAvatar(uri)
        }
    }

    // Solicitar los permisos de la Galería
    private val permisoGaleriaLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { esConcedido ->
        if (esConcedido) {
            abrirGaleriaLauncher.launch("image/*")
        } else {
            Toast.makeText(requireContext(), "Permiso de galería denegado", Toast.LENGTH_SHORT).show()
        }
    }

    // Solicitar el permiso de la Cámara
    private val permisoCamaraLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { esConcedido ->
        if (esConcedido) {
            tomarFotoLauncher.launch(null)
        } else {
            Toast.makeText(requireContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    //Lanzador para abrir la cámara nativa y recibir la foto tomada
    private val tomarFotoLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: android.graphics.Bitmap? ->
        if (bitmap != null) {
            Glide.with(this)
                .load(bitmap)
                .placeholder(R.drawable.ic_avatar_placeholder)
                .error(R.drawable.ic_avatar_placeholder)
                .circleCrop()
                .into(binding.imgAvatar)
        } else {
            Toast.makeText(requireContext(), "No se tomó ninguna fotografía", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCuentaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cargarDatosDeUsuario()

        binding.btnEditPhoto.setOnClickListener {
            mostrarOpcionesFoto()
        }

        binding.btnCerrarSesion.setOnClickListener {
            mostrarDialogoCerrarSesion()
        }
    }

    private fun cargarDatosDeUsuario() {
        val firebaseUser = firebaseAuth.currentUser
        val userId = firebaseUser?.uid

        if (firebaseUser != null) {
            binding.tvUserEmail.text = firebaseUser.email ?: "usuario@unam.mx"
        }

        if (userId != null) {
            val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()

            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val nombreCorto = document.getString("firstName") ?: "Usuario"
                        val nombreCompleto = document.getString("fullName") ?: "Nombre no registrado"
                        val telefono = document.getString("phone") ?: "Sin teléfono"
                        val fechaNaci = document.getString("birthdate") ?: "Sin fecha"

                        binding.tvUserName.text = nombreCorto
                        binding.tvUserFullName.text = nombreCompleto
                        binding.tvUserPhone.text = telefono
                        binding.tvUserBirthdate.text = fechaNaci
                    } else {
                        binding.tvUserName.text = "Mi Cuenta"
                        binding.tvUserFullName.text = "Perfil incompleto en base de datos"
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Error al cargar perfil: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // REPARACIÓN COMPLETA: Usamos apiCall para recibir directamente AccountResponse
        viewLifecycleOwner.lifecycleScope.launch {
            com.example.mibanca.network.apiCall {
                com.example.mibanca.di.NetworkModule.apiService.getAccount()
            }
                .onSuccess { account ->
                    // ¡Éxito! Imprimimos de inmediato el saldo formateado de forma nativa
                    binding.tvTitle.text = "Mi cuenta (${account.getFormattedBalance()})"
                }
                .onFailure { error ->
                    // Si la cuenta no existe o hay falla de red, apiCall captura el error de forma segura aquí
                    binding.tvTitle.text = "Mi cuenta ($ 0.00)"
                    Toast.makeText(
                        requireContext(),
                        "No se pudo sincronizar el saldo: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        cargarFotoEnAvatar("https://api.dicebear.com/7.x/avataaars/svg?seed=Maria")
    }
    private fun cargarFotoEnAvatar(anySource: Any) {
        Glide.with(this)
            .load(anySource)
            .placeholder(R.drawable.ic_avatar_placeholder)
            .error(R.drawable.ic_avatar_placeholder)
            .circleCrop()
            .into(binding.imgAvatar)
    }

    private fun mostrarOpcionesFoto() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.dialog_selector_foto, null)
        val btnCamera = dialogView.findViewById<android.widget.LinearLayout>(R.id.lnrCamera)
        val btnGallery = dialogView.findViewById<android.widget.LinearLayout>(R.id.lnrGallery)

        btnCamera?.setOnClickListener {
            bottomSheetDialog.dismiss()
            verificarYPedirPermisoCamara()
        }

        btnGallery?.setOnClickListener {
            bottomSheetDialog.dismiss()
            verificarYPedirPermisoGaleria()
        }

        bottomSheetDialog.setContentView(dialogView)
        bottomSheetDialog.show()
    }

    private fun verificarYPedirPermisoGaleria() {
        val permisoNecesario = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES // Android 13+
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE // Android 12 e inferiores
        }

        if (ContextCompat.checkSelfPermission(requireContext(), permisoNecesario) == PackageManager.PERMISSION_GRANTED) {
            abrirGaleriaLauncher.launch("image/*")
        } else {
            permisoGaleriaLauncher.launch(permisoNecesario)
        }
    }

    private fun verificarYPedirPermisoCamara() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            tomarFotoLauncher.launch(null)
        } else {
            permisoCamaraLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun mostrarDialogoCerrarSesion() {
        AlertDialog.Builder(requireContext())
            .setTitle("Cerrar sesión")
            .setMessage("¿Estás seguro de que deseas salir de tu cuenta?")
            .setPositiveButton("Sí, salir") { _, _ ->
                firebaseAuth.signOut()
                val intent = Intent(requireContext(), MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                requireActivity().finish()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}