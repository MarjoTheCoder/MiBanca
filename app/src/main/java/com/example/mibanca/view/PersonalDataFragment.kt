package com.example.mibanca.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mibanca.HomeActivity
import com.example.mibanca.databinding.FragmentPersonalDataBinding
import com.example.mibanca.viewmodel.AuthViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PersonalDataFragment : Fragment() {

    private var _binding: FragmentPersonalDataBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel

    // Instancias de Firebase
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        setupListeners()
        setupTextWatchers()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.etFechaNacimiento.setOnClickListener {
            showDatePicker()
        }

        binding.btnContinuar.setOnClickListener {
            guardarDatosEnFirestore()
        }
    }

    private fun guardarDatosEnFirestore() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "Error: No hay usuario autenticado", Toast.LENGTH_SHORT).show()
            return
        }
        binding.btnContinuar.isEnabled = false

        val nombre = binding.etNombre.text.toString().trim()
        val apellidos = binding.etApellidos.text.toString().trim()
        val celular = binding.etCelular.text.toString().trim()
        val fechaNacimiento = binding.etFechaNacimiento.text.toString().trim()

        val userProfile = hashMapOf(
            "uid" to userId,
            "firstName" to nombre,
            "lastName" to apellidos,
            "fullName" to "$nombre $apellidos",
            "phone" to celular,
            "birthdate" to fechaNacimiento
        )

        firestore.collection("users")
            .document(userId)
            .set(userProfile)
            .addOnSuccessListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    // SOLUCIÓN COMPLETA: Usamos apiCall para envolver la petición directa.
                    // Esto elimina por completo los errores de .isSuccessful y .code()
                    com.example.mibanca.network.apiCall {
                        com.example.mibanca.di.NetworkModule.apiService.createAccount()
                    }
                        .onSuccess { account ->
                            // Si entra aquí, la cuenta bancaria se creó perfectamente por primera vez
                            Toast.makeText(requireContext(), "¡Cuenta bancaria creada con éxito!", Toast.LENGTH_SHORT).show()
                            goToHome()
                        }
                        .onFailure { error ->
                            // Si falla, apiCall nos da el error. Evaluamos el mensaje o tipo para saber si es un 409 (Conflict)
                            // De acuerdo a la guía del profesor, si ya tenía cuenta de una sesión anterior, redirigimos a Home de forma segura.
                            if (error.message?.contains("409") == true || error.message?.contains("account_exists") == true) {
                                Toast.makeText(requireContext(), "Aviso: Ya posees una cuenta activa.", Toast.LENGTH_LONG).show()
                                goToHome()
                            } else {
                                // Cualquier otro fallo de red auténtico
                                binding.btnContinuar.isEnabled = true
                                Toast.makeText(requireContext(), "Error al crear cuenta: ${error.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                }
            }
            .addOnFailureListener { exception ->
                binding.btnContinuar.isEnabled = true
                Toast.makeText(requireContext(), "Error en Firestore: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
    private fun showDatePicker() {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Fecha de nacimiento")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        picker.addOnPositiveButtonClickListener { selection ->
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = selection
            val format = SimpleDateFormat("dd / MM / yyyy", Locale.getDefault())
            binding.etFechaNacimiento.setText(format.format(calendar.time))

            binding.tilFechaNacimiento.error = null
        }

        picker.show(childFragmentManager, "DATE_PICKER")
    }

    private fun setupTextWatchers() {
        val fields = listOf(
            binding.etNombre,
            binding.etApellidos,
            binding.etCelular,
            binding.etFechaNacimiento
        )

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val nombre = binding.etNombre.text.toString().trim()
                val apellidos = binding.etApellidos.text.toString().trim()
                val celular = binding.etCelular.text.toString().trim()
                val fecha = binding.etFechaNacimiento.text.toString().trim()

                val isNombreValid = nombre.isNotEmpty()
                val isApellidosValid = apellidos.isNotEmpty()
                val isPhoneValid = celular.length == 10
                val isFechaValid = fecha.isNotEmpty()

                binding.btnContinuar.isEnabled = isNombreValid &&
                        isApellidosValid &&
                        isPhoneValid &&
                        isFechaValid
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        fields.forEach { it.addTextChangedListener(watcher) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun goToHome() {
        val intent = Intent(requireContext(), HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}