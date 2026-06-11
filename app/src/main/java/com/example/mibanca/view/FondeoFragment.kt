package com.example.mibanca.view

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mibanca.R
import com.example.mibanca.di.NetworkModule
import com.example.mibanca.model.FundRequest
import com.example.mibanca.network.apiCall
import kotlinx.coroutines.launch

class FondeoFragment : Fragment(R.layout.fragment_fondeo) {

    private val apiService = NetworkModule.apiService

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etAmount = view.findViewById<EditText>(R.id.etAmount)
        val btnConfirmFund = view.findViewById<Button>(R.id.btnConfirmFund)

        btnConfirmFund.setOnClickListener {
            val amountText = etAmount.text.toString().trim()
            if (amountText.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor ingresa un monto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amountLong = amountText.toLongOrNull() ?: 0L

            if (amountLong <= 0) {
                Toast.makeText(requireContext(), "Monto inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            ejecutarFondeo(amountLong)
        }
    }

    private fun ejecutarFondeo(montoCentavos: Long) {
        lifecycleScope.launch {
            apiCall { apiService.fundAccount(FundRequest(montoCentavos)) }
                .onSuccess { cuentaActualizada ->
                    Toast.makeText(
                        requireContext(),
                        "¡Fondeo exitoso! Nuevo saldo: ${cuentaActualizada.getFormattedBalance()}",
                        Toast.LENGTH_LONG
                    ).show()

                    findNavController().popBackStack()
                }
                .onFailure { error ->
                    Toast.makeText(
                        requireContext(),
                        "Error al procesar fondeo: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }
}