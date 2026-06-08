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
import com.example.mibanca.model.AccountResponse
import com.example.mibanca.network.BankApiService
import com.example.mibanca.network.FundAccountRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FondeoFragment : Fragment(R.layout.fragment_fondeo) {

    private val apiService: BankApiService by lazy {
        Retrofit.Builder()
            // NOTA: Cambiar esta URL por la URL real del Mocki
            .baseUrl("https://us-central1-bankapp-e47b0.cloudfunctions.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BankApiService::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etAmount = view.findViewById<EditText>(R.id.etAmount)
        val btnConfirmFund = view.findViewById<Button>(R.id.btnConfirmFund)

        btnConfirmFund.setOnClickListener {
            val amountText = etAmount.text.toString()
            if (amountText.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor ingresa un monto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amountDouble = amountText.toDoubleOrNull() ?: 0.0
            val amountInCents = (amountDouble * 100).toLong()

            if (amountInCents <= 0) {
                Toast.makeText(requireContext(), "Monto inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            ejecutarFondeo(amountInCents)
        }
    }

    private fun ejecutarFondeo(montoCentavos: Long) {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.fundAccount(FundAccountRequest(montoCentavos))
                }

                if (response.isSuccessful && response.body() != null) {
                    val cuentaActualizada: AccountResponse = response.body()!!

                    Toast.makeText(
                        requireContext(),
                        "¡Fondeo exitoso! Nuevo saldo: ${cuentaActualizada.getFormattedBalance()}",
                        Toast.LENGTH_LONG
                    ).show()

                    findNavController().popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Error en el servidor al fondear", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}