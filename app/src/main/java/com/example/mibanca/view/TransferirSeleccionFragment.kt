package com.example.mibanca.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mibanca.R
import com.example.mibanca.data.repository.BankingRepository
import com.example.mibanca.databinding.FragmentTransferirSeleccionBinding
import com.example.mibanca.network.apiCall
import kotlinx.coroutines.launch

class TransferirSeleccionFragment : Fragment() {

    private var _binding: FragmentTransferirSeleccionBinding? = null
    private val binding get() = _binding!!

    // Inicializamos el repositorio conectado a la API v2
    private val repository: BankingRepository = com.example.mibanca.data.repository.BankingRepositoryImpl(
        com.example.mibanca.di.NetworkModule.apiService
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransferirSeleccionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        configurarRecyclerView()
        cargarBeneficiariosDesdeAPI()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.cardAddNuevo.setOnClickListener {
            findNavController().navigate(R.id.fragment_registro_beneficiario)
        }
    }
    private fun configurarRecyclerView() {
        // CORREGIDO: Usamos el ID exacto de tu XML 'rvBeneficiariosSelector'
        binding.rvBeneficiariosSelector.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun cargarBeneficiariosDesdeAPI() {
        // Activamos la barra de progreso mientras se descargan los datos
        binding.progressIndicator.visibility = View.VISIBLE

        viewLifecycleOwner.lifecycleScope.launch {
            apiCall { repository.getBeneficiaries() }
                .onSuccess { listaBeneficiarios ->
                    binding.progressIndicator.visibility = View.GONE

                    if (listaBeneficiarios.isEmpty()) {
                        // Si no hay datos, mostramos el layout vacío que diseñaste
                        binding.rvBeneficiariosSelector.visibility = View.GONE
                        binding.layoutEmptySelector.visibility = View.VISIBLE
                    } else {
                        // Si hay datos, aseguramos que la lista se vea y ocultamos lo vacío
                        binding.rvBeneficiariosSelector.visibility = View.VISIBLE
                        binding.layoutEmptySelector.visibility = View.GONE

                        // 🚀 CONEXIÓN CON EL ADAPTER:
                        // Busca en tu proyecto cómo se llama tu adaptador (ej. BeneficiaryAdapter).
                        // Descomenta las líneas de abajo y pon el tuyo:

                        /*
                        val adapter = BeneficiaryAdapter(listaBeneficiarios) { beneficiario ->
                            // Al tocar un beneficiario, guardamos su información y avanzamos al paso 2 (Monto)
                            val bundle = Bundle().apply {
                                putSerializable("KEY_BENEFICIARIO", beneficiario)
                            }
                            findNavController().navigate(R.id.action_transferirSeleccionFragment_to_transferirMontoFragment, bundle)
                        }
                        binding.rvBeneficiariosSelector.adapter = adapter
                        */

                        // Aviso temporal en lo que vinculas el nombre de tu Adapter exacto:
                        Toast.makeText(
                            requireContext(),
                            "¡Conectado! Cargados ${listaBeneficiarios.size} beneficiarios.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .onFailure { error ->
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Error en el servidor: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}