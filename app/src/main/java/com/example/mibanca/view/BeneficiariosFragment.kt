package com.example.mibanca.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mibanca.R
import com.example.mibanca.databinding.FragmentBeneficiariosBinding
import com.example.mibanca.model.Beneficiary
import com.example.mibanca.adapter.BeneficiariosAdapter
import com.example.mibanca.network.apiCall
import android.text.TextWatcher
import android.text.Editable
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.mibanca.network.apiCall

class BeneficiariosFragment : Fragment() {
    private var _binding: FragmentBeneficiariosBinding? = null
    private val binding get() = _binding!!

    private val listaCompleta = mutableListOf<Beneficiary>()
    private lateinit var beneficiariosAdapter: BeneficiariosAdapter

    private val repository = com.example.mibanca.data.repository.BankingRepositoryImpl(
        com.example.mibanca.di.NetworkModule.apiService
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View {
        _binding = FragmentBeneficiariosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddBeneficiario.setOnClickListener { irAFormulario(null) }
        binding.btnAgregarPrimero.setOnClickListener { irAFormulario(null) }

        configurarBuscador()
        cargarBeneficiariosDesdeAPI()
    }

    private fun cargarBeneficiariosDesdeAPI() {

        viewLifecycleOwner.lifecycleScope.launch {
            apiCall<List<com.example.mibanca.model.Beneficiary>> { repository.getBeneficiaries() }
                .onSuccess { listaRecibida ->
                    binding.progressIndicator.visibility = View.GONE

                    val listaMutable = listaRecibida.toMutableList()
                    listaCompleta.clear()
                    listaCompleta.addAll(listaMutable)

                    if (listaMutable.isEmpty()) {
                        binding.rvBeneficiarios.visibility = View.GONE
                        binding.layoutEmptyBeneficiarios.visibility = View.VISIBLE
                    } else {
                        binding.rvBeneficiarios.visibility = View.VISIBLE
                        binding.layoutEmptyBeneficiarios.visibility = View.GONE

                        val adapter = com.example.mibanca.adapter.BeneficiariosAdapter(listaMutable) { beneficiario ->
                            val bundle = Bundle().apply {
                                putString("beneficiaryId", beneficiario.id)
                            }
                            findNavController().navigate(
                                R.id.action_transferirSeleccionFragment_to_transferirMontoFragment,
                                bundle
                            )
                        }
                        binding.rvBeneficiarios.adapter = adapter
                        beneficiariosAdapter = adapter //
                    }
                }
                .onFailure { error ->
                    android.widget.Toast.makeText(requireContext(), "Error: ${error.message}", android.widget.Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun configurarBuscador() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val textoBuscado = s.toString().trim().lowercase()
                realizarFiltrado(textoBuscado)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun realizarFiltrado(query: String) {
        if (!::beneficiariosAdapter.isInitialized) return

        if (query.isEmpty()) {
            beneficiariosAdapter.filtrarLista(listaCompleta)
            binding.rvBeneficiarios.visibility = View.VISIBLE
            binding.layoutEmptyBeneficiarios.visibility = View.GONE
            return
        }

        val listaFiltrada = listaCompleta.filter { beneficiario ->
            val nombre = beneficiario.name.orEmpty().lowercase()
            val apellido = beneficiario.lastName.orEmpty().lowercase()
            val alias = beneficiario.alias.orEmpty().lowercase()
            val banco = beneficiario.bankName.orEmpty().lowercase()

            nombre.contains(query) ||
                    apellido.contains(query) ||
                    alias.contains(query) ||
                    banco.contains(query)
        }

        beneficiariosAdapter.filtrarLista(listaFiltrada)

        if (listaFiltrada.isEmpty()) {
            binding.rvBeneficiarios.visibility = View.GONE
            binding.layoutEmptyBeneficiarios.visibility = View.VISIBLE
        } else {
            binding.rvBeneficiarios.visibility = View.VISIBLE
            binding.layoutEmptyBeneficiarios.visibility = View.GONE
        }
    }

    private fun mostrarPantallaVacia() {
        binding.rvBeneficiarios.visibility = View.GONE
        binding.layoutEmptyBeneficiarios.visibility = View.VISIBLE
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