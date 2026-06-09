package com.example.mibanca.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mibanca.R
import com.example.mibanca.databinding.FragmentTransferirMontoBinding
import com.example.mibanca.viewmodel.BankingViewModel
import com.example.mibanca.viewmodel.OperationUiState
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect // <-- CRUCIAL para solucionar el 'Cannot infer type'

class TransferirMontoFragment : Fragment() {

    private var _binding: FragmentTransferirMontoBinding? = null
    private val binding get() = _binding!!

    private var beneficiaryId: String? = null
    private val viewModel: BankingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransferirMontoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        beneficiaryId = arguments?.getString("beneficiaryId")

        configurarComponentesVisuales()
        configurarChipsSugeridos()
        configurarLogicaMonto()
        observarViewModel()

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.btnCambiar.setOnClickListener { findNavController().navigateUp() }

        binding.btnTransferir.setOnClickListener {
            ejecutarFlujoTransferencia()
        }
    }

    private fun observarViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is OperationUiState.Idle -> {}
                    is OperationUiState.Loading -> {
                        binding.progressIndicator.visibility = View.VISIBLE
                        binding.btnTransferir.isEnabled = false
                        binding.btnTransferir.text = "Procesando..."
                    }
                    is OperationUiState.Success -> {
                        binding.progressIndicator.visibility = View.GONE
                        Toast.makeText(requireContext(), "¡Transferencia realizada con éxito!", Toast.LENGTH_LONG).show()
                        viewModel.resetState()
                        findNavController().popBackStack(R.id.navigation_home, false)
                    }
                    is OperationUiState.Error -> {
                        binding.progressIndicator.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()

                        val montoText = binding.etMonto.text.toString().trim()
                        val montoDouble = montoText.toDoubleOrNull() ?: 0.0
                        binding.btnTransferir.isEnabled = montoDouble > 0
                        binding.btnTransferir.text = String.format("Transferir $%.2f", montoDouble)

                        viewModel.resetState()
                    }
                }
            }
        }
    }

    private fun configurarComponentesVisuales() {
        binding.tvNombreBeneficiario.text = "Beneficiario verificado"
        binding.tvSaldoDisponible.text = "Saldo disponible: $12,450.75"
        binding.progressIndicator.visibility = View.GONE
    }

    private fun configurarChipsSugeridos() {
        binding.chip100.setOnClickListener { binding.etMonto.setText("100"); binding.chipGroupSugeridos.clearCheck() }
        binding.chip200.setOnClickListener { binding.etMonto.setText("200"); binding.chipGroupSugeridos.clearCheck() }
        binding.chip500.setOnClickListener { binding.etMonto.setText("500"); binding.chipGroupSugeridos.clearCheck() }
        binding.chip1000.setOnClickListener { binding.etMonto.setText("1000"); binding.chipGroupSugeridos.clearCheck() }
    }

    private fun configurarLogicaMonto() {
        binding.etMonto.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val montoText = s.toString().trim()
                if (montoText.isEmpty()) {
                    binding.btnTransferir.isEnabled = false
                    binding.btnTransferir.text = "Transferir $0.00"
                } else {
                    val montoDouble = montoText.toDoubleOrNull() ?: 0.0
                    if (montoDouble > 0) {
                        binding.btnTransferir.isEnabled = true
                        binding.btnTransferir.text = String.format("Transferir $%.2f", montoDouble)
                    } else {
                        binding.btnTransferir.isEnabled = false
                        binding.btnTransferir.text = "Transferir $0.00"
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun ejecutarFlujoTransferencia() {
        val montoText = binding.etMonto.text.toString().trim()
        val amountLong = montoText.toLongOrNull() ?: 0L
        val concepto = binding.etConcepto.text.toString().trim()

        if (beneficiaryId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Error: No se seleccionó un beneficiario válido", Toast.LENGTH_SHORT).show()
            return
        }

        if (amountLong <= 0) {
            Toast.makeText(requireContext(), "Por favor ingresa un monto válido", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.transferir(beneficiaryId!!, amountLong, concepto)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}