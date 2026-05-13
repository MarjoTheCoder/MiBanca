package com.example.mibanca.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mibanca.R
import com.example.mibanca.databinding.FragmentLoginBinding
import com.example.mibanca.viewmodel.AuthViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        // Lógica de botones
        setupListeners()

        // Observadores de Firebase
        setupObservers()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            // Validaciones visuales con TextInputLayout
            when {
                email.isEmpty() -> {
                    binding.tilEmail.error = "Ingresa tu correo"
                    binding.tilPassword.error = null
                }
                password.isEmpty() -> {
                    binding.tilPassword.error = "Ingresa tu contraseña"
                    binding.tilEmail.error = null
                }
                else -> {
                    binding.tilEmail.error = null
                    binding.tilPassword.error = null
                    authViewModel.login(email, password)
                }
            }
        }

        // Navegación al registro usando el nav_onboarding
        binding.tvGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }
    }

    private fun setupObservers() {
        authViewModel.loginResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                // Como aún no hay HomeActivity, solo avisamos
                Toast.makeText(requireContext(), "¡Login Exitoso! (Esperando Home)", Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}