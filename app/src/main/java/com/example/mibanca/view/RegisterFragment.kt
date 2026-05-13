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
import com.example.mibanca.databinding.FragmentRegisterBinding
import com.example.mibanca.viewmodel.AuthViewModel

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        setupObservers()

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()
            val confirmPass = binding.etConfirmPassword.text.toString().trim()

            when {
                email.isEmpty() || pass.isEmpty() -> {
                    Toast.makeText(requireContext(), "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
                }
                pass.length < 8 -> {
                    binding.tilPassword.error = "Mínimo 8 caracteres"
                }
                pass != confirmPass -> {
                    binding.tilConfirmPassword.error = "Las contraseñas no coinciden"
                }
                else -> {
                    binding.tilPassword.error = null
                    binding.tilConfirmPassword.error = null
                    authViewModel.register(email, pass)
                }
            }
        }

        binding.tvGoToLogin.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupObservers() {
        authViewModel.registerResult.observe(viewLifecycleOwner) { success ->
            if (success) {

                Toast.makeText(
                    requireContext(),
                    "¡Cuenta creada con éxito!",
                    Toast.LENGTH_SHORT
                ).show()

                findNavController().navigate(
                    R.id.action_register_to_personalData
                )
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