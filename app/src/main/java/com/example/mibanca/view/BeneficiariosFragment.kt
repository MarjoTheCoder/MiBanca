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
import com.example.mibanca.databinding.FragmentBeneficiariosBinding
import com.example.mibanca.di.NetworkModule
import com.example.mibanca.model.Beneficiary
import com.example.mibanca.network.apiCall
import kotlinx.coroutines.launch

class BeneficiariosFragment : Fragment() {

    private var _binding: FragmentBeneficiariosBinding? = null
    private val binding get() = _binding!!

    private val listaCompleta = mutableListOf<Beneficiary>()
    private lateinit var beneficiariosAdapter: BeneficiariosAdapter

    private val repository = BankingRepositoryImpl(NetworkModule.apiService)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBeneficiariosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            setupRecyclerView()
        configurarBuscador()

        binding.btnAddBeneficiario.setOnClickListener { irAFormulario(null) }
        binding.btnAgregarPrimero.setOnClickListener { irAFormulario(null) }

        cargarBeneficiariosDesdeAPI()
    }

    private fun setupRecyclerView() {
        beneficiariosAdapter = BeneficiariosAdapter(
            mutableListOf(),
            onElementoClick = { beneficiario -> irAFormulario(beneficiario) },
            onMenuMoreClick = { beneficiario -> irAFormulario(beneficiario) }
        )

        binding.rvBeneficiarios.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = beneficiariosAdapter
            setHasFixedSize(true)
        }
    }

    private fun cargarBeneficiariosDesdeAPI() {
        binding.progressIndicator.visibility = View.VISIBLE
        binding.rvBeneficiarios.visibility = View.GONE
        binding.layoutEmptyBeneficiarios.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            apiCall { repository.getBeneficiaries() }
                .onSuccess { listaRecibida ->
                    binding.progressIndicator.visibility = View.GONE

                    listaCompleta.clear()
                    listaCompleta.addAll(listaRecibida)

                    if (listaRecibida.isEmpty()) {
                        binding.rvBeneficiarios.visibility = View.GONE
                        binding.layoutEmptyBeneficiarios.visibility = View.VISIBLE
                    } else {
                        beneficiariosAdapter.filtrarLista(listaCompleta)
                        binding.rvBeneficiarios.visibility = View.VISIBLE
                        binding.layoutEmptyBeneficiarios.visibility = View.GONE
                    }
                }
                .onFailure { error ->
                    binding.progressIndicator.visibility = View.GONE
                    binding.layoutEmptyBeneficiarios.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun configurarBuscador() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                realizarFiltrado(s.toString().trim().lowercase())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun realizarFiltrado(query: String) {
        if (query.isEmpty()) {
            beneficiariosAdapter.filtrarLista(listaCompleta)
            binding.rvBeneficiarios.visibility =
                if (listaCompleta.isEmpty()) View.GONE else View.VISIBLE
            binding.layoutEmptyBeneficiarios.visibility =
                if (listaCompleta.isEmpty()) View.VISIBLE else View.GONE
            return
        }

        val listaFiltrada = listaCompleta.filter { b ->
            b.name.orEmpty().lowercase().contains(query) ||
                    b.lastName.orEmpty().lowercase().contains(query) ||
                    b.alias.orEmpty().lowercase().contains(query) ||
                    b.bankName.orEmpty().lowercase().contains(query)
        }

        beneficiariosAdapter.filtrarLista(listaFiltrada)
        binding.rvBeneficiarios.visibility =
            if (listaFiltrada.isEmpty()) View.GONE else View.VISIBLE
        binding.layoutEmptyBeneficiarios.visibility =
            if (listaFiltrada.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun irAFormulario(beneficiario: Beneficiary?) {
        val bundle = Bundle().apply { putSerializable("KEY_BENEFICIARIO", beneficiario) }
        findNavController().navigate(R.id.fragment_registro_beneficiario, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}