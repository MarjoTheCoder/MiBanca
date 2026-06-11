package com.example.mibanca.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mibanca.R
import com.example.mibanca.adapter.BeneficiariosAdapter
import com.example.mibanca.data.repository.BankingRepositoryImpl
import com.example.mibanca.databinding.FragmentTransferirSeleccionBinding
import com.example.mibanca.di.NetworkModule
import com.example.mibanca.model.Beneficiary
import com.example.mibanca.network.apiCall
import kotlinx.coroutines.launch

class TransferirSeleccionFragment : Fragment() {

    private var _binding: FragmentTransferirSeleccionBinding? = null
    private val binding get() = _binding!!

    private val listaCompleta = mutableListOf<Beneficiary>()
    private lateinit var beneficiariosAdapter: BeneficiariosAdapter

    private val repository = BankingRepositoryImpl(NetworkModule.apiService)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransferirSeleccionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        cargarBeneficiariosDesdeAPI()
    }

    private fun setupRecyclerView() {
        beneficiariosAdapter = BeneficiariosAdapter(
            lista = emptyList(),
            onElementoClick = { beneficiario ->
                val bundle = Bundle().apply {
                    putString("beneficiaryId", beneficiario.id)
                }
                findNavController().navigate(R.id.action_transferirSeleccionFragment_to_transferirMontoFragment, bundle)
            },
            onMenuMoreClick = { beneficiario ->
                val bundle = Bundle().apply { putSerializable("KEY_BENEFICIARIO", beneficiario) }
                findNavController().navigate(R.id.fragment_registro_beneficiario, bundle)
            }
        )

        binding.rvBeneficiariosSelector.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = beneficiariosAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.cardAddNuevo.setOnClickListener {
            findNavController().navigate(R.id.fragment_registro_beneficiario)
        }
    }

    private fun cargarBeneficiariosDesdeAPI() {
        binding.progressIndicator.visibility = View.VISIBLE
        binding.rvBeneficiariosSelector.visibility = View.GONE
        binding.layoutEmptySelector.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            apiCall { repository.getBeneficiaries() }
                .onSuccess { listaBeneficiarios ->
                    binding.progressIndicator.visibility = View.GONE

                    listaCompleta.clear()
                    listaCompleta.addAll(listaBeneficiarios)

                    if (listaBeneficiarios.isEmpty()) {
                        binding.rvBeneficiariosSelector.visibility = View.GONE
                        binding.layoutEmptySelector.visibility = View.VISIBLE
                    } else {
                        beneficiariosAdapter.filtrarLista(listaCompleta)
                        binding.rvBeneficiariosSelector.visibility = View.VISIBLE
                        binding.layoutEmptySelector.visibility = View.GONE
                    }
                }
                .onFailure { error ->
                    binding.progressIndicator.visibility = View.GONE
                    binding.layoutEmptySelector.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}