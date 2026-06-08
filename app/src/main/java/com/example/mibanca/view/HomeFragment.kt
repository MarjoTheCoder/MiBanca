package com.example.mibanca.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mibanca.R
import com.example.mibanca.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cargarInformacionUsuario()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnTransferir.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_transferirSeleccionFragment)
        }

        binding.btnFondear.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_fondeoFragment)
        }

        binding.tvVerTodos.setOnClickListener {
            Toast.makeText(requireContext(), "Abriendo historial completo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarInformacionUsuario() {
        val userId = firebaseAuth.currentUser?.uid ?: return

        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val nombreCompleto = document.getString("fullName") ?: "Usuario"
                    binding.tvUserName.text = nombreCompleto
                } else {
                    binding.tvUserName.text = "Cliente Banca"
                }
            }
            .addOnFailureListener {
                binding.tvUserName.text = "Bienvenido"
            }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    com.example.mibanca.di.NetworkModule.apiService.getAccount()
                }

                if (response.isSuccessful && response.body() != null) {
                    val account = response.body()!!

                    binding.tvSaldoMonto.text = account.getFormattedBalance()
                } else {
                    binding.tvSaldoMonto.text = "$ 0.00"
                    binding.tvCuentaDetalle.text = "MXN • Cuenta No Activa"
                }
            } catch (e: Exception) {
                binding.tvSaldoMonto.text = "$ 0.00"
                binding.tvCuentaDetalle.text = "MXN • Error de conexión"
                android.util.Log.e("HomeFragment", "Error al mapear saldo de API: ${e.message}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}