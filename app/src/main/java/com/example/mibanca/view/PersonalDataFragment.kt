package com.example.mibanca.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mibanca.databinding.FragmentPersonalDataBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class PersonalDataFragment : Fragment() {

    private var _binding: FragmentPersonalDataBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

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
            Toast.makeText(requireContext(), "Perfil completado", Toast.LENGTH_SHORT).show()
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
}