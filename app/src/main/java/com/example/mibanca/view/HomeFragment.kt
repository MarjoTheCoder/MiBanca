package com.example.mibanca.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mibanca.R
import com.example.mibanca.databinding.FragmentHomeBinding
import com.example.mibanca.viewmodel.AccountUiState
import com.example.mibanca.viewmodel.BankingViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val viewModel: BankingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cargarNombreFirestore()
        setupClickListeners()
        observarEstadoDeCuenta()

        viewModel.obtenerDatosDeCuenta()
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

    private fun cargarNombreFirestore() {
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
    }

    private fun observarEstadoDeCuenta() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.accountState.collect { state ->
                when (state) {
                    is AccountUiState.Loading -> {
                        binding.tvSaldoMonto.text = "Cargando..."
                        binding.tvCuentaDetalle.text = "Consultando información..."
                    }
                    is AccountUiState.Success -> {
                        val cuenta = state.account

                        binding.tvSaldoMonto.text = cuenta.getFormattedBalance()

                        binding.tvCuentaDetalle.text = "MXN • No. ${cuenta.accountNumber}"
                    }
                    is AccountUiState.Error -> {
                        binding.tvSaldoMonto.text = "$ 0.00"
                        binding.tvCuentaDetalle.text = "MXN • Error de conexión"

                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}